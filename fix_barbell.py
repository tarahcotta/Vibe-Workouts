import re

with open("app/src/main/java/com/example/ui/screens/PlateCalculatorScreen.kt", "r") as f:
    content = f.read()

visual_barbell_code = """
@Composable
fun VisualBarbell(plateBreakdown: List<PlateCount>, modifier: Modifier = Modifier) {
    // Standard plate widths and heights
    val barHeight = 16.dp
    val sleeveLength = 140.dp
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        // Draw the Barbell shaft and sleeve
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Shaft
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(barHeight)
                    .background(Color.DarkGray)
            )
            // Sleeve Collar
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(barHeight + 10.dp)
                    .background(Color.Gray, RoundedCornerShape(2.dp))
            )
            // Sleeve with Plates
            Box(
                modifier = Modifier
                    .width(sleeveLength)
                    .height(140.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Sleeve background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .background(Color.LightGray)
                )
                
                // Plates stacked
                Row(
                    modifier = Modifier.padding(start = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    plateBreakdown.forEach { plateCount ->
                        repeat(plateCount.count) {
                            val plateHeight = when (plateCount.plateWeight) {
                                55.0, 45.0 -> 120.dp
                                35.0 -> 100.dp
                                25.0 -> 80.dp
                                10.0 -> 60.dp
                                5.0 -> 40.dp
                                2.5 -> 30.dp
                                else -> 50.dp
                            }
                            val plateWidth = when (plateCount.plateWeight) {
                                55.0, 45.0, 35.0, 25.0 -> 16.dp
                                else -> 10.dp
                            }
                            
                            Box(
                                modifier = Modifier
                                    .width(plateWidth)
                                    .height(plateHeight)
                                    .background(plateCount.color, RoundedCornerShape(2.dp))
                                    .border(1.dp, Color.Black.copy(alpha=0.2f), RoundedCornerShape(2.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = plateCount.plateWeight.toInt().toString(),
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
"""

if "fun VisualBarbell" not in content:
    content += visual_barbell_code

target_list_insertion = """            if (plateBreakdown.isEmpty()) {
                Text(
                    text = "No plates needed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                plateBreakdown.forEach { pc ->"""

replacement_list_insertion = """            if (plateBreakdown.isEmpty()) {
                Text(
                    text = "No plates needed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                VisualBarbell(plateBreakdown = plateBreakdown)
                Spacer(modifier = Modifier.height(16.dp))
                plateBreakdown.forEach { pc ->"""

if target_list_insertion in content:
    content = content.replace(target_list_insertion, replacement_list_insertion)
else:
    print("Warning: could not insert VisualBarbell")

with open("app/src/main/java/com/example/ui/screens/PlateCalculatorScreen.kt", "w") as f:
    f.write(content)

print("Plate Calculator VisualBarbell inserted")
