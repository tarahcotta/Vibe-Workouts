with open('app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt', 'r') as f:
    content = f.read()

content = content.replace("            Surface(\n                modifier = Modifier.fillMaxWidth(),", 
"            Surface(\n                modifier = Modifier.fillMaxWidth().navigationBarsPadding(),")

with open('app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt', 'w') as f:
    f.write(content)

