import os
import re

directory = 'app/src/main/java/com/example/ui'

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r') as f:
                content = f.read()
            
            # Very basic regex replacement for some obvious ones
            # We'll replace them manually if it's tricky, but let's see how many we can do automatically
            # Or just set them to a generic descriptive string based on the icon name
            
            def replace_desc(match):
                icon_name = match.group(1)
                desc = '"' + icon_name + '"'
                return f'contentDescription = {desc}'
            
            # Regex to match Icon(imageVector = Icons.Default.XYZ, contentDescription = null)
            # This is hard to do perfectly with regex because it might be multiline.
            pass

