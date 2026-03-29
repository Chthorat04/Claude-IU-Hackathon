from ollama import Client

client = Client()
response = client.chat(
    model="mistral:7b-instruct",
    messages=[
        {"role": "system", "content": "You are a first-aid assistant."},
        {"role": "user", "content": "I have a burn on my hand."}
    ]
)
print(response["message"]["content"])
