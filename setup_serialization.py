with open("gradle/libs.versions.toml", "r") as f:
    content = f.read()

if "kotlinx-serialization-json" not in content:
    content = content.replace("[libraries]", "[libraries]\nkotlinx-serialization-json = { group = \"org.jetbrains.kotlinx\", name = \"kotlinx-serialization-json\", version = \"1.6.3\" }")
    
if "kotlin-serialization" not in content:
    content = content.replace("[plugins]", "[plugins]\nkotlin-serialization = { id = \"org.jetbrains.kotlin.plugin.serialization\", version.ref = \"kotlin\" }")

if "retrofit-converter-kotlinx-serialization" not in content:
    content = content.replace("[libraries]", "[libraries]\nretrofit-converter-kotlinx-serialization = { group = \"com.squareup.retrofit2\", name = \"converter-kotlinx-serialization\", version.ref = \"retrofit\" }")

with open("gradle/libs.versions.toml", "w") as f:
    f.write(content)

with open("app/build.gradle.kts", "r") as f:
    gradle_content = f.read()

if "alias(libs.plugins.kotlin.serialization)" not in gradle_content:
    gradle_content = gradle_content.replace("alias(libs.plugins.kotlin.compose)", "alias(libs.plugins.kotlin.compose)\n  alias(libs.plugins.kotlin.serialization)")

if "implementation(libs.kotlinx.serialization.json)" not in gradle_content:
    gradle_content = gradle_content.replace("implementation(libs.retrofit)", "implementation(libs.retrofit)\n  implementation(libs.retrofit.converter.kotlinx.serialization)\n  implementation(libs.kotlinx.serialization.json)")

with open("app/build.gradle.kts", "w") as f:
    f.write(gradle_content)
