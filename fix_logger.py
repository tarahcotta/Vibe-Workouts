import re
import os

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "r") as f:
    content = f.read()

# Add CompactGymStepper component
stepper_comp = """
@Composable
fun CompactGymStepper(
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
}
"""
if "fun CompactGymStepper" not in content:
    content += stepper_comp

# Replace OutlinedTextField for weight with CompactGymStepper
target_weight_input = """                                    // Weight Input Field
                                    OutlinedTextField(
                                        value = setInput.weightText,
                                        onValueChange = { setInput.weightText = it },
                                        modifier = Modifier
                                            .width(72.dp)
                                            .testTag("weight_input_${exIndex}_$setIndex"),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                                        shape = RoundedCornerShape(10.dp)
                                    )"""

replacement_weight_input = """                                    // Weight Input Field (Gym Stepper)
                                    CompactGymStepper(
                                        valueText = setInput.weightText,
                                        onValueChange = { setInput.weightText = it },
                                        label = "Weight",
                                        step = 5f,
                                        modifier = Modifier.width(100.dp).testTag("weight_input_${exIndex}_$setIndex")
                                    )"""

if target_weight_input in content:
    content = content.replace(target_weight_input, replacement_weight_input)
else:
    print("Warning: weight input not found")


target_reps_input = """                                    // Reps Input Field
                                    OutlinedTextField(
                                        value = setInput.repsText,
                                        onValueChange = { setInput.repsText = it },
                                        modifier = Modifier
                                            .width(64.dp)
                                            .testTag("reps_input_${exIndex}_$setIndex"),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                                        shape = RoundedCornerShape(10.dp)
                                    )"""

replacement_reps_input = """                                    // Reps Input Field (Gym Stepper)
                                    CompactGymStepper(
                                        valueText = setInput.repsText,
                                        onValueChange = { setInput.repsText = it },
                                        label = "Reps",
                                        step = 1f,
                                        modifier = Modifier.width(90.dp).testTag("reps_input_${exIndex}_$setIndex")
                                    )"""
if target_reps_input in content:
    content = content.replace(target_reps_input, replacement_reps_input)
else:
    print("Warning: reps input not found")

# Fix Keyboard Overlap by adding WindowInsets.ime padding to Scaffold content
target_scaffold_modifier = "        modifier = modifier.fillMaxSize(),"
replacement_scaffold_modifier = "        modifier = modifier.fillMaxSize().padding(androidx.compose.foundation.layout.WindowInsets.ime.asPaddingValues()),"

if target_scaffold_modifier in content:
    content = content.replace(target_scaffold_modifier, replacement_scaffold_modifier)
else:
    print("Warning: Scaffold modifier not found")

with open("app/src/main/java/com/example/ui/screens/ActiveLoggerScreen.kt", "w") as f:
    f.write(content)

print("ActiveLogger updated")
