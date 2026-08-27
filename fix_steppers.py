import re

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "r") as f:
    content = f.read()

target_stepper = """fun CompactGymStepper(
    valueText: String,
    onValueChange: (String) -> Unit,
    label: String,
    step: Float = 1f,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            val current = valueText.toFloatOrNull() ?: 0f
            val next = (current - step).coerceAtLeast(0f)
            onValueChange(if (next % 1f == 0f) next.toInt().toString() else next.toString())
        }, modifier = Modifier.size(40.dp)) {
            Icon(androidx.compose.material.icons.Icons.Default.KeyboardArrowDown, "Decrease $label", modifier = Modifier.size(20.dp))
        }
        
        Text(text = valueText, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        
        IconButton(onClick = {
            val current = valueText.toFloatOrNull() ?: 0f
            val next = current + step
            onValueChange(if (next % 1f == 0f) next.toInt().toString() else next.toString())
        }, modifier = Modifier.size(40.dp)) {
            Icon(androidx.compose.material.icons.Icons.Default.KeyboardArrowUp, "Increase $label", modifier = Modifier.size(20.dp))
        }
    }
}"""

replacement_stepper = """fun CompactGymStepper(
    valueText: String,
    onValueChange: (String) -> Unit,
    label: String,
    step: Float = 1f,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            val current = valueText.toFloatOrNull() ?: 0f
            val next = (current - step).coerceAtLeast(0f)
            onValueChange(if (next % 1f == 0f) next.toInt().toString() else next.toString())
        }, modifier = Modifier.size(48.dp)) {
            Icon(androidx.compose.material.icons.Icons.Default.KeyboardArrowDown, "Decrease $label", modifier = Modifier.size(24.dp))
        }
        
        Text(text = valueText, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        
        IconButton(onClick = {
            val current = valueText.toFloatOrNull() ?: 0f
            val next = current + step
            onValueChange(if (next % 1f == 0f) next.toInt().toString() else next.toString())
        }, modifier = Modifier.size(48.dp)) {
            Icon(androidx.compose.material.icons.Icons.Default.KeyboardArrowUp, "Increase $label", modifier = Modifier.size(24.dp))
        }
    }
}"""

if target_stepper in content:
    content = content.replace(target_stepper, replacement_stepper)
else:
    print("Warning: target_stepper not found")

# We also need to change the heights/widths where it is called to accommodate the larger row height if any
# It seems they just have modifier = Modifier.width(100.dp), width(90.dp).
# The Row height handles it. We should bump the widths slightly to fit the larger 48dp icons.
content = content.replace("Modifier.width(100.dp).testTag(\"weight_input_", "Modifier.width(130.dp).testTag(\"weight_input_")
content = content.replace("Modifier.width(90.dp).testTag(\"reps_input_", "Modifier.width(120.dp).testTag(\"reps_input_")

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "w") as f:
    f.write(content)

print("Steppers touch targets updated")
