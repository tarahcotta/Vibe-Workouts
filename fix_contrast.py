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

            # Fix low contrast text colors
            # specifically we are looking for color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) or similar
            
            # Let's use regex to find colorScheme.<something>.copy(alpha = <value>)
            # and if value is < 0.7, change it to 0.7f or just remove the alpha.
            
            def replacer(match):
                prefix = match.group(1)
                alpha_str = match.group(2)
                alpha_val = float(alpha_str.replace('f', ''))
                if alpha_val < 0.7:
                    # boost contrast
                    return f"{prefix}.copy(alpha = 0.7f)"
                return match.group(0)

            # Match patterns like: MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            new_content = re.sub(r'(MaterialTheme\.colorScheme\.[a-zA-Z0-9_]+)\.copy\(\s*alpha\s*=\s*([0-9\.]+[fF]?)\s*\)', replacer, content)
            
            if new_content != content:
                with open(filepath, 'w') as f:
                    f.write(new_content)
                count += 1

print(f"Updated {count} files with better contrast")
