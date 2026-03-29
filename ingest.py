# ingest.py — Run this ONCE to build the vector index from all PDFs
import os
import json
import sqlite3
import numpy as np
from pathlib import Path
from langchain_community.document_loaders import PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from sentence_transformers import SentenceTransformer
import chromadb

DATA_DIR = "data"
DB_PATH = "readyaid_vectors.db"
CHROMA_PATH = "chroma_index"

MEDICAL_KEYWORDS = [
    "burn", "burns", "bleeding", "blood", "cut", "wound", "choking", "choke",
    "fracture", "broken bone", "seizure", "convulsion", "asthma", "inhaler",
    "anaphylaxis", "allergy", "allergic", "cardiac", "heart attack", "stroke",
    "unconscious", "faint", "fainting", "hypothermia", "heat stroke", "poisoning",
    "overdose", "shock", "trauma", "hemorrhage", "airway", "breathing", "cpr",
    "resuscitation", "defibrillator", "aed", "sprain", "dislocation", "head injury",
    "concussion", "diabetic", "hypoglycemia", "insulin", "drowning", "electric shock",
    "eye injury", "nose bleed", "nosebleed", "broken", "spinal", "recovery position"
]

SOURCE_MAP = {
    "european_resuscitation_council_guidelines_2025_first_aid.pdf": "ERC_2025",
    "icrc-002-0526.pdf": "ICRC",
    "american-red-cross-firstaid-guide.pdf": "RedCross",
    "FARG_February-2024_EN_0.pdf": "SJA_Canada",
    "First-aid-reference-guide_V4.1_Public.pdf": "SJA_Condensed"
}

def extract_condition_tag(text: str) -> str:
    text_lower = text.lower()
    if any(w in text_lower for w in ["burn", "scald", "hot"]):
        return "burns"
    if any(w in text_lower for w in ["bleed", "blood", "wound", "hemorrhage"]):
        return "bleeding"
    if any(w in text_lower for w in ["chok", "airway", "obstruction"]):
        return "choking"
    if any(w in text_lower for w in ["fracture", "broken", "bone"]):
        return "fracture"
    if any(w in text_lower for w in ["seiz", "convuls"]):
        return "seizure"
    if any(w in text_lower for w in ["asthma", "inhaler", "wheez"]):
        return "asthma"
    if any(w in text_lower for w in ["cardiac", "heart", "cpr", "defibrillat", "arrest"]):
        return "cardiac"
    if any(w in text_lower for w in ["stroke", "facial droop", "fast"]):
        return "stroke"
    if any(w in text_lower for w in ["unconscious", "unresponsive", "faint"]):
        return "unconscious"
    if any(w in text_lower for w in ["anaphylax", "epipen", "allergic reaction"]):
        return "anaphylaxis"
    if any(w in text_lower for w in ["diabetic", "hypoglycemia", "sugar", "insulin"]):
        return "diabetes"
    if any(w in text_lower for w in ["poison", "overdose", "toxic"]):
        return "poisoning"
    if any(w in text_lower for w in ["drown", "water", "submersion"]):
        return "drowning"
    if any(w in text_lower for w in ["heat", "hyperthermia", "sun"]):
        return "heat"
    if any(w in text_lower for w in ["hypotherm", "cold", "frost"]):
        return "cold"
    return "general"

def extract_section_type(text: str, heading: str) -> str:
    combined = (text + " " + heading).lower()
    if any(w in combined for w in ["do not", "do not do", "avoid", "never", "warning"]):
        return "do_not"
    if any(w in combined for w in ["when to call", "call emergency", "call 911", "seek help"]):
        return "when_to_call"
    if any(w in combined for w in ["step 1", "step 2", "first step", "do this", "action"]):
        return "do_now"
    if any(w in combined for w in ["check", "assess", "look for", "signs", "symptoms"]):
        return "check_first"
    return "general"

def build_index():
    embedding_model = SentenceTransformer("thenlper/gte-small")
    chroma_client = chromadb.PersistentClient(path=CHROMA_PATH)
    collection = chroma_client.get_or_create_collection("readyaid_firstaid")

    splitter = RecursiveCharacterTextSplitter(
        chunk_size=1000,
        chunk_overlap=150,
        separators=["\n\n", "\n", ".", " "]
    )

    conn = sqlite3.connect(DB_PATH)
    conn.execute("""
        CREATE TABLE IF NOT EXISTS chunks (
            id TEXT PRIMARY KEY,
            source TEXT,
            condition_tag TEXT,
            section_type TEXT,
            text TEXT,
            embedding BLOB,
            keywords TEXT
        )
    """)

    for pdf_file in Path(DATA_DIR).glob("*.pdf"):
        source_name = SOURCE_MAP.get(pdf_file.name, pdf_file.stem)
        print(f"Processing: {pdf_file.name} → {source_name}")

        loader = PyPDFLoader(str(pdf_file))
        pages = loader.load()

        for page in pages:
            chunks = splitter.split_text(page.page_content)
            for i, chunk_text in enumerate(chunks):
                if len(chunk_text.strip()) < 50:
                    continue

                chunk_id = f"{source_name}_{page.metadata.get('page', 0)}_{i}"
                condition_tag = extract_condition_tag(chunk_text)
                section_type = extract_section_type(chunk_text, "")

                # Extract keywords present in this chunk
                chunk_lower = chunk_text.lower()
                present_keywords = [kw for kw in MEDICAL_KEYWORDS if kw in chunk_lower]

                # Embed
                embedding = embedding_model.encode(chunk_text, normalize_embeddings=True)
                embedding_int8 = (embedding * 127).astype(np.int8)

                # Store in SQLite (for on-device)
                conn.execute(
                    "INSERT OR REPLACE INTO chunks VALUES (?, ?, ?, ?, ?, ?, ?)",
                    (
                        chunk_id, source_name, condition_tag, section_type,
                        chunk_text, embedding_int8.tobytes(),
                        json.dumps(present_keywords)
                    )
                )

                # Store in ChromaDB (for server-side dev)
                collection.upsert(
                    ids=[chunk_id],
                    embeddings=[embedding.tolist()],
                    documents=[chunk_text],
                    metadatas=[{
                        "source": source_name,
                        "condition": condition_tag,
                        "section_type": section_type,
                        "keywords": ",".join(present_keywords)
                    }]
                )

    conn.commit()
    conn.close()
    print(f"Index built. Total chunks stored.")

if __name__ == "__main__":
    build_index()
