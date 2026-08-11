package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object GeminiRoutineGenerator {
    
    private const val MODEL_NAME = "gemini-1.5-flash"
    
    suspend fun generatePersonalizedRoutine(profile: UserProfileEntity): List<Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>>> {
        return withContext(Dispatchers.IO) {
            try {
                // If the user hasn't provided a key, fallback to static generation
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
                    Log.w("GeminiGenerator", "No API Key found, falling back to static generation")
                    return@withContext RoutineGenerator.generateRoutineForProfile(profile)
                }

                val generativeModel = GenerativeModel(
                    modelName = MODEL_NAME,
                    apiKey = apiKey
                )
                
                val prompt = """
                    You are an expert fitness coach. Create a customized workout routine for the following user:
                    Schedule: ${profile.scheduleDaysPerWeek} days per week
                    Available Equipment: ${profile.availableEquipment}
                    Goals: ${profile.healthGoals}
                    Joint/Injury History: ${profile.jointHistory}
                    
                    Return ONLY a JSON array of workout days. Each object in the array should have:
                    - "title": string (e.g. "Upper Body Power")
                    - "dayName": string (e.g. "Day 1")
                    - "focusSummary": string
                    - "exercises": array of objects with:
                        - "exerciseId": string (match known exercises if possible, e.g. "goblet_squat", "romanian_deadlift", "push_up_incline", "face_pull", "step_up_knee_drive")
                        - "recommendedSets": integer
                        - "recommendedReps": string
                        - "rpeTarget": integer
                """.trimIndent()
                
                val response = generativeModel.generateContent(content { text(prompt) })
                val jsonString = response.text?.replace("```json", "")?.replace("```", "")?.trim() ?: "[]"
                
                val routines = mutableListOf<Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>>>()
                val jsonArray = JSONArray(jsonString)
                
                for (i in 0 until jsonArray.length()) {
                    val dayObj = jsonArray.getJSONObject(i)
                    val routineEntity = WorkoutRoutineEntity(
                        title = dayObj.getString("title"),
                        dayName = dayObj.getString("dayName"),
                        focusSummary = dayObj.getString("focusSummary")
                    )
                    
                    val exercisesList = mutableListOf<WorkoutExerciseEntity>()
                    val exArray = dayObj.getJSONArray("exercises")
                    for (j in 0 until exArray.length()) {
                        val exObj = exArray.getJSONObject(j)
                        exercisesList.add(
                            WorkoutExerciseEntity(
                                routineId = 0L, // will be set when saved
                                exerciseId = exObj.getString("exerciseId"),
                                recommendedSets = exObj.getInt("recommendedSets"),
                                recommendedReps = exObj.getString("recommendedReps"),
                                rpeTarget = exObj.getInt("rpeTarget")
                            )
                        )
                    }
                    routines.add(Pair(routineEntity, exercisesList))
                }
                
                if (routines.isEmpty()) {
                    RoutineGenerator.generateRoutineForProfile(profile)
                } else {
                    routines
                }
            } catch (e: Exception) {
                Log.e("GeminiGenerator", "Error generating routine via Gemini", e)
                RoutineGenerator.generateRoutineForProfile(profile)
            }
        }
    }
}