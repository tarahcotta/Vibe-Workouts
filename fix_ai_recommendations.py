with open("app/src/main/java/com/example/ui/components/AIRecommendationsCard.kt", "r") as f:
    content = f.read()

content = content.replace("import com.google.firebase.Firebase\nimport com.google.firebase.ai.generativemodels.GenerativeModel", 
                          "import com.google.firebase.vertexai.type.content\nimport com.google.firebase.vertexai.GenerativeModel\nimport com.google.firebase.Firebase\nimport com.google.firebase.vertexai.vertexAI")

content = content.replace("val model = GenerativeModel(\n                    modelName = \"gemini-3.5-flash\"\n                )", 
                          "val model = Firebase.vertexAI.generativeModel(\"gemini-1.5-flash\")")

with open("app/src/main/java/com/example/ui/components/AIRecommendationsCard.kt", "w") as f:
    f.write(content)
