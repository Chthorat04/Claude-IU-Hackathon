from fastapi import FastAPI
from pydantic import BaseModel
import chromadb
from sentence_transformers import SentenceTransformer
from ollama import Client
import re
from fastapi.responses import StreamingResponse
import json

app = FastAPI()

# ----------- MODELS -----------

embedding_model = SentenceTransformer("thenlper/gte-small")
ollama_client = Client()
chroma_client = chromadb.PersistentClient(path="chroma_index")
collection = chroma_client.get_collection("readyaid_firstaid")

# ----------- PROFILE -----------

class Profile(BaseModel):
    age: int = 0
    conditions: list[str] = []
    allergies: list[str] = []
    medications: list[str] = []
    medical_history: str = ""

class Query(BaseModel):
    query: str
    profile: Profile | None = None


# ----------- CONDITION DETECTION -----------

def extract_condition(query: str) -> str:
    q = query.lower()

    if any(w in q for w in ["burn", "scald"]): return "burns"
    if any(w in q for w in ["bleed", "blood", "cut", "wound"]): return "bleeding"
    if any(w in q for w in ["chok", "airway"]): return "choking"
    if any(w in q for w in ["fracture", "broken"]): return "fracture"
    if any(w in q for w in ["seiz"]): return "seizure"
    if any(w in q for w in ["asthma", "inhaler", "breathing", "breath"]): return "asthma"
    if any(w in q for w in ["heart", "cardiac"]): return "cardiac"
    if any(w in q for w in ["stroke"]): return "stroke"

    return "general"


# ----------- MAIN API -----------

@app.post("/ask")
def ask(body: Query):
    def generate():
        try:
            query_lower = body.query.lower()

            # ----------- BLOCK NON-MEDICAL -----------
            if any(w in query_lower for w in ["who is", "movie", "celebrity", "joke"]):
                yield json.dumps({
                    "response": "I'm ReadyAid — I can only help with first-aid guidance.",
                    "sources": [],
                    "is_first_aid": False,
                    "condition_detected": "none",
                    "is_finished": True
                }) + "\n"
                return

            profile = body.profile
            age = profile.age if profile else 0
            conditions = profile.conditions if profile else []
            allergies = profile.allergies if profile else []
            medications = profile.medications if profile else []

            condition = extract_condition(body.query)

            # ----------- FAST + LIGHT RETRIEVAL -----------
            if condition in ["choking", "cardiac", "stroke"]:
                context = "Provide immediate life-saving first aid steps."
                sources = []
            else:
                query_embedding = embedding_model.encode(body.query, normalize_embeddings=True)
                results = collection.query(
                    query_embeddings=[query_embedding.tolist()],
                    n_results=2
                )

                chunks = results["documents"][0]
                metadatas = results["metadatas"][0]

                compressed_chunks = []
                for chunk in chunks:
                    sentences = re.split(r'(?<=[.!?]) +', chunk)
                    compressed_chunks.append(" ".join(sentences[:3]))

                context = "\n\n".join(compressed_chunks)
                sources = list(set([m.get("source", "") for m in metadatas]))

            # ----------- PROFILE CONTEXT -----------
            profile_context = ""
            if age:
                profile_context += f"Age {age}. "
            if conditions:
                profile_context += f"{', '.join(conditions)}. "
            if allergies:
                profile_context += f"Allergies: {', '.join(allergies)}. "
            if medications:
                profile_context += f"Meds: {', '.join(medications)}. "

            # ----------- BALANCED PROMPT (FAST + CLEAR) -----------
            system_prompt = f"""
You are a first aid assistant.

Give:
- Clear step-by-step instructions
- Each step must be a complete sentence
- Keep it short but easy to understand

Include:
- What to do
- When to call emergency services

Do NOT:
- Use keywords only
- Skip important details

Patient: {profile_context if profile_context else "None"}
Context: {context}
"""

            # ----------- INSTANT FEEDBACK -----------
            yield json.dumps({
                "response": "⚠️ Getting first aid steps...\n",
                "sources": sources,
                "is_first_aid": True,
                "condition_detected": condition,
                "is_finished": False
            }) + "\n"

            # ----------- LITE MODEL CALL -----------
            response_stream = ollama_client.chat(
                model="phi3:mini",
                messages=[
                    {"role": "system", "content": system_prompt},
                    {"role": "user", "content": body.query}
                ],
                stream=True,
                options={
                    "temperature": 0.1,
                    "num_predict": 250,
                    "num_ctx": 1024
                },
                keep_alive="5m"
            )

            # ----------- FAST STREAMING (INSTANT RESPONSE) -----------
            buffer = ""
            for chunk in response_stream:
                token = chunk["message"]["content"]
                buffer += token

                # Only flush when sentence is COMPLETE
                if any(buffer.endswith(p) for p in [". ", "! ", "? ", ".\n", "!\n", "?\n"]):
                    yield json.dumps({
                        "response": buffer,
                        "sources": sources,
                        "is_first_aid": True,
                        "condition_detected": condition,
                        "is_finished": False
                    }) + "\n"
                    buffer = ""

            # Flush final buffer
            if buffer.strip().endswith((".", "!", "?")):
                yield json.dumps({
                    "response": buffer,
                    "sources": sources,
                    "is_first_aid": True,
                    "condition_detected": condition,
                    "is_finished": False
    }) + "\n"

            # ----------- FINALIZE -----------
            yield json.dumps({
                "response": "",
                "sources": sources,
                "is_first_aid": True,
                "condition_detected": condition,
                "is_finished": True
            }) + "\n"

        except Exception as e:
            yield json.dumps({
                "response": f"Backend error: {str(e)}",
                "sources": [],
                "is_first_aid": False,
                "condition_detected": "error",
                "is_finished": True
            }) + "\n"

    return StreamingResponse(generate(), media_type="application/x-ndjson")