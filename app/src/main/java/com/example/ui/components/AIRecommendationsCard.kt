package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.data.LoggedWorkoutSessionEntity
import com.example.data.WorkoutRoutineEntity
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import com.example.network.Tool
import kotlinx.serialization.json.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AIRecommendationsCard(
    sessions: List<LoggedWorkoutSessionEntity>,
    routines: List<WorkoutRoutineEntity>,
    modifier: Modifier = Modifier
) {
    var recommendation by remember { mutableStateOf("Generating personalized insight...") }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(sessions.size, routines.size) {
        if (sessions.isEmpty()) {
            recommendation = "Log your first workout to get personalized AI strength recommendations!"
            return@LaunchedEffect
        }
        scope.launch {
            try {
                val prompt = "I have logged ${sessions.size} workouts. " +
                        "My latest workout was ${sessions.firstOrNull()?.routineDayTitle ?: "a session"}. " +
                        "Give me a short, 1-sentence motivational recommendation or insight for my next workout."
                
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    tools = listOf(Tool(googleSearch = JsonObject(emptyMap())))
                )
                
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    recommendation = "AI features require a valid Gemini API key in secrets."
                } else {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.service.generateContent(apiKey, request)
                    }
                    recommendation = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Ready to crush your next session!"
                }
            } catch (e: Exception) {
                recommendation = "Ready to crush your next session! Keep progressing."
            }
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Insight",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "AI Insight",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = recommendation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
