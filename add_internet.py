with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

content = content.replace('<uses-permission android:name="android.permission.RECORD_AUDIO" />', 
                          '<uses-permission android:name="android.permission.RECORD_AUDIO" />\n    <uses-permission android:name="android.permission.INTERNET" />')

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
