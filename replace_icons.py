import os
import re

directory = 'app/src/main/java/com/example/ui'
count = 0

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r') as f:
                content = f.read()

            # Find instances of contentDescription = null and replace with "Decorative Icon" or something
            # Let's just do a naive replace for now
            new_content = content.replace('contentDescription = null', 'contentDescription = "Icon"')
            
            if new_content != content:
                with open(filepath, 'w') as f:
                    f.write(new_content)
                count += 1

print(f"Updated {count} files with contentDescription")
