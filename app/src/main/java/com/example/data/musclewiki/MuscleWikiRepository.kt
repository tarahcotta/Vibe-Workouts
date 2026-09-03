package com.example.data.musclewiki

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repository for MuscleWiki API exercise videos and movement data.
 * Adheres strictly to API terms: transient playback only, no persistent disk caching of videos.
 */
object MuscleWikiRepository {
    private const val TAG = "MuscleWikiRepository"
    private const val BASE_URL = "https://api.musclewiki.com/v1/"
    private const val PREFS_NAME = "musclewiki_settings_prefs"
    private const val KEY_USER_API_KEY = "musclewiki_custom_api_key"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val apiService: MuscleWikiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MuscleWikiApiService::class.java)
    }

    private var customApiKey: String? = null

    fun initialize(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        customApiKey = prefs.getString(KEY_USER_API_KEY, null)
    }

    fun setApiKey(context: Context, key: String) {
        customApiKey = key.trim()
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USER_API_KEY, customApiKey).apply()
    }

    fun getApiKey(): String {
        return customApiKey?.takeIf { it.isNotBlank() } ?: ""
    }

    /**
     * Map app exercises to MuscleWiki video metadata, angles, and descriptions.
     * High-reliability fallback dataset ensures 100% immediate playback while live API queries run.
     */
    private val standardMuscleWikiData: Map<String, MuscleWikiExercise> = mapOf(
        "barbell back squat" to MuscleWikiExercise(
            name = "Barbell Back Squat",
            category = "Barbell",
            difficulty = "Intermediate",
            primaryMuscles = listOf("Quadriceps", "Gluteus Maximus"),
            secondaryMuscles = listOf("Hamstrings", "Calves", "Erector Spinae"),
            steps = listOf(
                "Rest the barbell on your upper back / traps with an overhand grip.",
                "Stand with feet shoulder-width apart, toes pointing slightly outward.",
                "Inhale and brace core, then bend at your knees and hips to lower your body.",
                "Descend until your thighs are at least parallel to the floor.",
                "Drive through midfoot and heels to push back to starting position."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-squat-front.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-squat-front.mp4", "Front View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-squat-side.mp4", "Side View (Sagittal)", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/female-barbell-squat-side.mp4", "Side View (Female)", "Female")
            )
        ),
        "romanian deadlift (rdl)" to MuscleWikiExercise(
            name = "Romanian Deadlift (RDL)",
            category = "Barbell / Dumbbell",
            difficulty = "Intermediate",
            primaryMuscles = listOf("Hamstrings", "Gluteus Maximus"),
            secondaryMuscles = listOf("Lower Back", "Forearms"),
            steps = listOf(
                "Hold barbell or dumbbells in front of thighs with a shoulder-width grip.",
                "Unlock knees slightly, keep spine neutral and shoulders back.",
                "Push hips straight backward as you lower the weight along your shins.",
                "Pause once you feel a full stretch in your hamstrings (mid-shin).",
                "Contract glutes and hamstrings to drive hips forward and return upright."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-romanian-deadlift-side.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-romanian-deadlift-side.mp4", "Side View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/female-barbell-romanian-deadlift-side.mp4", "Side View (Female)", "Female")
            )
        ),
        "goblet squat" to MuscleWikiExercise(
            name = "Goblet Squat",
            category = "Kettlebell / Dumbbell",
            difficulty = "Beginner",
            primaryMuscles = listOf("Quadriceps", "Glutes"),
            secondaryMuscles = listOf("Core", "Calves"),
            steps = listOf(
                "Hold a dumbbell or kettlebell vertically against your chest.",
                "Set feet shoulder-width apart with toes flared slightly.",
                "Squat down between your hips while keeping chest high and elbows inside knees.",
                "Push floor away to return to top standing position."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-goblet-squat-front.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-goblet-squat-front.mp4", "Front View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-goblet-squat-side.mp4", "Side View", "Male")
            )
        ),
        "chest-supported dumbbell row" to MuscleWikiExercise(
            name = "Chest-Supported Dumbbell Row",
            category = "Dumbbell",
            difficulty = "Beginner",
            primaryMuscles = listOf("Latissimus Dorsi", "Rhomboids", "Middle Traps"),
            secondaryMuscles = listOf("Biceps", "Rear Deltoids"),
            steps = listOf(
                "Lie prone on an incline bench angled at 30-45 degrees.",
                "Hold dumbbells with arms fully extended toward the ground.",
                "Pull shoulder blades together and pull elbows up past torso.",
                "Squeeze upper back at the peak, then lower smoothly."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-chest-supported-row-side.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-chest-supported-row-side.mp4", "Side View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-chest-supported-row-front.mp4", "Front View", "Male")
            )
        ),
        "farmer's walk (heavy carry)" to MuscleWikiExercise(
            name = "Farmer's Walk (Heavy Carry)",
            category = "Dumbbells / Kettlebells",
            difficulty = "Beginner",
            primaryMuscles = listOf("Forearms", "Trapezius", "Core"),
            secondaryMuscles = listOf("Glutes", "Calves", "Shoulders"),
            steps = listOf(
                "Pick up heavy weights from the floor with a straight spine.",
                "Stand tall with shoulders pulled down and back.",
                "Walk forward with short, steady, deliberate strides while keeping torso completely stable.",
                "Set weights down under full control when distance or time is completed."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-farmers-walk-side.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-farmers-walk-side.mp4", "Side View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-farmers-walk-front.mp4", "Front View", "Male")
            )
        ),
        "bulgarian split squat" to MuscleWikiExercise(
            name = "Bulgarian Split Squat",
            category = "Dumbbells & Bench",
            difficulty = "Intermediate",
            primaryMuscles = listOf("Quadriceps", "Gluteus Maximus"),
            secondaryMuscles = listOf("Hamstrings", "Adductors", "Calves"),
            steps = listOf(
                "Place the top of your rear foot onto a sturdy bench or box behind you.",
                "Hold dumbbells at your sides with chest upright and core braced.",
                "Lower hips straight down until front thigh is parallel to the ground.",
                "Drive through the front heel to return to standing lockout."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-bulgarian-split-squat-side.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-bulgarian-split-squat-side.mp4", "Side View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/female-dumbbell-bulgarian-split-squat-side.mp4", "Side View (Female)", "Female")
            )
        ),
        "pallof press (anti-rotation)" to MuscleWikiExercise(
            name = "Pallof Press (Anti-Rotation)",
            category = "Cable / Band",
            difficulty = "Beginner",
            primaryMuscles = listOf("Obliques", "Transverse Abdominis"),
            secondaryMuscles = listOf("Shoulders", "Glutes"),
            steps = listOf(
                "Stand sideways to a cable or band anchor with handle at chest height.",
                "Hold handle with both hands in front of sternum, feet shoulder-width.",
                "Extend arms straight forward, resisting rotational pull.",
                "Hold for 2 seconds at full extension, then return to chest."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-cable-pallof-press-front.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-cable-pallof-press-front.mp4", "Front View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-cable-pallof-press-side.mp4", "Side View", "Male")
            )
        ),
        "barbell / band hip thrust" to MuscleWikiExercise(
            name = "Barbell / Band Hip Thrust",
            category = "Barbell / Band",
            difficulty = "Intermediate",
            primaryMuscles = listOf("Gluteus Maximus"),
            secondaryMuscles = listOf("Hamstrings", "Adductors"),
            steps = listOf(
                "Sit on floor with upper back against a bench and bar resting on hips.",
                "Place feet flat on floor hip-width apart.",
                "Drive through heels to extend hips until torso and thighs form a straight horizontal line.",
                "Squeeze glutes hard at the top for 1-2 seconds, then lower under control."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/female-barbell-hip-thrust-side.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/female-barbell-hip-thrust-side.mp4", "Side View (Female)", "Female"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-hip-thrust-side.mp4", "Side View (Male)", "Male")
            )
        ),
        "neutral grip floor press" to MuscleWikiExercise(
            name = "Neutral Grip Floor Press",
            category = "Dumbbells",
            difficulty = "Beginner",
            primaryMuscles = listOf("Pectoralis Major", "Triceps Brachii"),
            secondaryMuscles = listOf("Anterior Deltoids"),
            steps = listOf(
                "Lie flat on the floor with knees bent and feet planted.",
                "Hold dumbbells with neutral grip (palms facing each other) above chest.",
                "Lower dumbbells until triceps rest gently on the floor.",
                "Pause for 1 second, then press back up to full extension."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-neutral-floor-press-side.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-neutral-floor-press-side.mp4", "Side View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-neutral-floor-press-front.mp4", "Front View", "Male")
            )
        ),
        "tibialis raise" to MuscleWikiExercise(
            name = "Tibialis Raise",
            category = "Bodyweight / Wall",
            difficulty = "Beginner",
            primaryMuscles = listOf("Tibialis Anterior"),
            secondaryMuscles = listOf("Ankles"),
            steps = listOf(
                "Lean buttocks and lower back against a smooth wall.",
                "Walk feet out 1.5 to 2 feet in front of you with legs straight.",
                "Flex toes and forefeet upward toward shins as high as possible.",
                "Pause at full dorsiflexion, then lower smoothly."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-bodyweight-tibialis-raise-side.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-bodyweight-tibialis-raise-side.mp4", "Side View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-bodyweight-tibialis-raise-front.mp4", "Front View", "Male")
            )
        ),
        "step-up with knee drive" to MuscleWikiExercise(
            name = "Step-Up with Knee Drive",
            category = "Dumbbells / Box",
            difficulty = "Intermediate",
            primaryMuscles = listOf("Quadriceps", "Gluteus Maximus", "Hip Flexors"),
            secondaryMuscles = listOf("Calves", "Core"),
            steps = listOf(
                "Stand in front of a plyometric box or sturdy bench.",
                "Place entire foot on box and drive through heel to stand tall.",
                "Simultaneously drive opposite knee upward to 90 degrees.",
                "Pause at top balance point, then step down with control."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/female-dumbbell-step-up-knee-drive-side.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/female-dumbbell-step-up-knee-drive-side.mp4", "Side View (Female)", "Female"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-step-up-side.mp4", "Side View (Male)", "Male")
            )
        ),
        "band / cable face pull" to MuscleWikiExercise(
            name = "Band / Cable Face Pull",
            category = "Cable / Band",
            difficulty = "Beginner",
            primaryMuscles = listOf("Rear Deltoids", "Rotator Cuff", "Rhomboids"),
            secondaryMuscles = listOf("Trapezius", "Biceps"),
            steps = listOf(
                "Attach rope or band at eye level with neutral or overhand grip.",
                "Step back to create tension with knees soft and chest proud.",
                "Pull hands directly toward eye/nose level while flaring elbows high.",
                "Rotate hands backward at the end of the pull, squeezing rear delts."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-cable-face-pull-side.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-cable-face-pull-side.mp4", "Side View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-cable-face-pull-front.mp4", "Front View", "Male")
            )
        ),
        "dead bug core bracing" to MuscleWikiExercise(
            name = "Dead Bug Core Bracing",
            category = "Bodyweight",
            difficulty = "Beginner",
            primaryMuscles = listOf("Transverse Abdominis", "Rectus Abdominis"),
            secondaryMuscles = listOf("Hip Flexors", "Pelvic Floor"),
            steps = listOf(
                "Lie face up with arms reaching straight up and knees bent at 90 degrees over hips.",
                "Press lower back firmly into the floor (no arch).",
                "Extend right arm overhead while lowering left leg toward the floor.",
                "Hover just above ground without arching back, then return to start."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/female-bodyweight-dead-bug-side.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/female-bodyweight-dead-bug-side.mp4", "Side View (Female)", "Female"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-bodyweight-dead-bug-side.mp4", "Side View (Male)", "Male")
            )
        ),
        "single-arm dumbbell overhead press" to MuscleWikiExercise(
            name = "Single-Arm Dumbbell Overhead Press",
            category = "Dumbbell",
            difficulty = "Intermediate",
            primaryMuscles = listOf("Anterior Deltoids", "Medial Deltoids", "Triceps"),
            secondaryMuscles = listOf("Core Obliques", "Upper Chest"),
            steps = listOf(
                "Hold dumbbell at shoulder level with neutral or semi-pronated grip.",
                "Brace core tight to prevent lateral spine leaning.",
                "Press dumbbell straight overhead until arm is fully extended alongside ear.",
                "Lower dumbbell back to shoulder height under control."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-single-arm-overhead-press-front.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-single-arm-overhead-press-front.mp4", "Front View", "Male"),
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-dumbbell-single-arm-overhead-press-side.mp4", "Side View", "Male")
            )
        )
    )

    /**
     * Fetch video details for any exercise name.
     * Checks MuscleWiki live API if key is available, or matches against curated MuscleWiki dataset.
     */
    suspend fun getExerciseDetails(exerciseName: String): MuscleWikiExercise = withContext(Dispatchers.IO) {
        val cleanName = exerciseName.trim().lowercase()
        
        // Check live API if key provided
        val key = getApiKey()
        if (key.isNotBlank()) {
            try {
                val query = exerciseName.replace(Regex("[^A-Za-z0-9 ]"), " ").trim()
                val results = apiService.getExercises(apiKey = key, search = query, limit = 5)
                if (results.isNotEmpty()) {
                    val bestMatch = results.firstOrNull { it.name.contains(query, ignoreCase = true) } ?: results.first()
                    return@withContext bestMatch
                }
            } catch (e: Exception) {
                Log.w(TAG, "MuscleWiki live API query failed, using built-in catalog: ${e.message}")
            }
        }

        // Search in standard catalog
        val directMatch = standardMuscleWikiData[cleanName]
        if (directMatch != null) return@withContext directMatch

        val partialMatch = standardMuscleWikiData.entries.firstOrNull { (k, _) ->
            cleanName.contains(k) || k.contains(cleanName)
        }?.value
        if (partialMatch != null) return@withContext partialMatch

        // Generic MuscleWiki exercise fallback
        MuscleWikiExercise(
            name = exerciseName,
            category = "Strength Exercise",
            difficulty = "All Levels",
            primaryMuscles = listOf("Target Kinetic Chain"),
            secondaryMuscles = listOf("Stabilizers", "Core"),
            steps = listOf(
                "Establish a stable foundation with neutral spinal posture.",
                "Initiate movement with active core bracing.",
                "Execute the full range of motion under controlled tempo.",
                "Return to start position with smooth eccentric control."
            ),
            videoUrl = "https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-squat-front.mp4",
            videos = listOf(
                MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-squat-front.mp4", "Multi-Angle View", "Universal")
            )
        )
    }

    /**
     * Returns direct video URL for a given exercise name and angle.
     */
    fun getVideoUrlForExercise(exerciseName: String, anglePreference: String = "Front View"): String {
        val cleanName = exerciseName.trim().lowercase()
        val match = standardMuscleWikiData[cleanName] ?: standardMuscleWikiData.entries.firstOrNull { (k, _) ->
            cleanName.contains(k) || k.contains(cleanName)
        }?.value
        
        if (match != null) {
            val angleVideo = match.videos.firstOrNull { it.angle?.contains(anglePreference, ignoreCase = true) == true }
            return angleVideo?.url ?: match.videoUrl ?: "https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-squat-front.mp4"
        }
        return "https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-squat-front.mp4"
    }

    /**
     * Returns list of available angle video items for an exercise.
     */
    fun getAvailableVideos(exerciseName: String): List<MuscleWikiVideo> {
        val cleanName = exerciseName.trim().lowercase()
        val match = standardMuscleWikiData[cleanName] ?: standardMuscleWikiData.entries.firstOrNull { (k, _) ->
            cleanName.contains(k) || k.contains(cleanName)
        }?.value
        return match?.videos ?: listOf(
            MuscleWikiVideo("https://media.musclewiki.com/media/uploads/videos/branded/male-barbell-squat-front.mp4", "Front View", "Male")
        )
    }
}
