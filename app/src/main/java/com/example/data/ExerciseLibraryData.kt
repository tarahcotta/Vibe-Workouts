package com.example.data

data class ExerciseLibraryItem(
    val id: String,
    val name: String,
    val muscleGroup: String, // e.g. "Lower Body (Quadriceps)", "Posterior Chain (Glutes/Hamstrings)", "Upper Body Pull (Back/Lats)", etc.
    val healthFocusCategory: HealthFocusCategory,
    val equipment: String, // "Barbell", "Dumbbell", "Kettlebell", "Bodyweight", "Cable"
    val difficulty: String, // "Beginner", "Intermediate", "Advanced"
    val targetBonesAndJoints: String, // e.g., "Femoral Neck, Lumbar Vertebrae, Knee Cartilage"
    val summary: String,
    val stepByStepForm: List<String>,
    val proFormTips: List<String>,
    val commonMistakes: List<String>,
    val longevityScienceNote: String,
    val videoUrl: String
)

enum class HealthFocusCategory(
    val displayName: String,
    val description: String,
    val iconName: String
) {
    ALL("All Focuses", "Show all exercises in the library", "All"),
    BONE_DENSITY("Bone Density", "Heavy axial & compressive loading to stimulate bone osteoblast remodeling", "Bone"),
    JOINT_HEALTH("Joint Health", "Promotes synovial fluid circulation & tendon stiffness without joint shearing", "Joint"),
    SPINE_POSTURE("Spine & Posture", "Scapular retraction, thoracic extension & spinal erect balance", "Spine"),
    BALANCE_FALL_PREVENTION("Balance & Fall Prevention", "Single-leg stability, proprioception & ankle reactivity", "Balance"),
    CORE_STABILITY("Core Bracing", "Intra-abdominal pressure & anti-rotational spinal protection", "Core"),
    POWER_MOBILITY("Power & Functional Mobility", "Dynamic hip drive, loaded carry & daily movement velocity", "Power")
}

object ExerciseLibraryRepository {

    val categories = HealthFocusCategory.values().toList()

    val exercises: List<ExerciseLibraryItem> = listOf(
        ExerciseLibraryItem(
            id = "barbell_back_squat",
            name = "Barbell Back Squat",
            muscleGroup = "Lower Body (Quadriceps & Glutes)",
            healthFocusCategory = HealthFocusCategory.BONE_DENSITY,
            equipment = "Barbell",
            difficulty = "Intermediate",
            targetBonesAndJoints = "Femoral Neck, Lumbar Spine, Hip Socket",
            summary = "The gold-standard exercise for systemic bone density stimulation through heavy axial loading on the spine and hips.",
            stepByStepForm = listOf(
                "Position barbell on upper trapezius muscles with a firm shoulder-width grip.",
                "Unrack bar, step back, and set feet slightly wider than shoulder-width with toes angled outward 15-30 degrees.",
                "Inhale deeply into abdomen and brace core hard (360-degree intra-abdominal pressure).",
                "Break at hips and knees simultaneously, lowering hips down and back until thighs are parallel to the floor.",
                "Drive through the mid-foot/heel to press back up to starting position while keeping chest elevated."
            ),
            proFormTips = listOf(
                "Maintain 'tripod foot' pressure (big toe, pinky toe, and heel firmly rooted).",
                "Keep elbows pulled down towards your ribcage to keep upper back tight.",
                "Pause for a fraction of a second at the bottom to build tendon control."
            ),
            commonMistakes = listOf(
                "Allowing knees to cave inward (valgus collapse) during ascent.",
                "Rounding the lower back at the bottom of the movement ('butt wink').",
                "Rising onto toes or lifting heels off the ground."
            ),
            longevityScienceNote = "Ground reaction force combined with axial barbell load triggers mechanotransduction in bone tissue, signaling osteoblasts to deposit calcium and increase bone mineral density in the femoral neck and lumbar vertebrae."
,
            videoUrl = "https://www.youtube.com/embed/dQw4w9WgXcQ"
        ),
        ExerciseLibraryItem(
            id = "romanian_deadlift",
            name = "Romanian Deadlift (RDL)",
            muscleGroup = "Posterior Chain (Glutes, Hamstrings, Erector Spinae)",
            healthFocusCategory = HealthFocusCategory.BONE_DENSITY,
            equipment = "Barbell / Dumbbell",
            difficulty = "Intermediate",
            targetBonesAndJoints = "Hip Joint, Lumbar Spine, Posterior Tendons",
            summary = "Essential hinge movement that builds glute-hamstring strength, protects lower back joints, and density in the pelvis.",
            stepByStepForm = listOf(
                "Stand tall holding barbell or dumbbells in front of thighs with shoulder-width stance.",
                "Retract shoulder blades, pull ribs down, and soft-bend the knees slightly.",
                "Push hips back towards the wall behind you while lowering weights down along the shins.",
                "Stop descending once hips can no longer travel backwards or when hamstrings reach full tension (around mid-shin).",
                "Squeeze glutes and drive hips forward to return to standing tall."
            ),
            proFormTips = listOf(
                "Keep the weight grazing your legs throughout the entire movement.",
                "Think 'hip hinge' rather than bending forward at the waist.",
                "Keep neck neutral by soft-gazing 6-10 feet in front of you on the floor."
            ),
            commonMistakes = listOf(
                "Rounding the thoracic or lumbar spine to lower the weight further.",
                "Squatting down into the movement instead of pushing hips backward.",
                "Hyperextending lower back at the top lockout position."
            ),
            longevityScienceNote = "Hinge mechanics train the posterior kinetic chain to absorb force efficiently during daily activities, drastically reducing lower back strain and improving pelvic bone density."
,
            videoUrl = "https://www.youtube.com/embed/JCXUYuzwNrM"
        ),
        ExerciseLibraryItem(
            id = "goblet_squat",
            name = "Goblet Squat",
            muscleGroup = "Lower Body & Core",
            healthFocusCategory = HealthFocusCategory.JOINT_HEALTH,
            equipment = "Kettlebell / Dumbbell",
            difficulty = "Beginner",
            targetBonesAndJoints = "Patellar Tendon, Hip Capsule, Ankle Complex",
            summary = "An accessible squat variation that encourages deep knee flexion, upright posture, and joint lubrication.",
            stepByStepForm = listOf(
                "Hold a dumbbell or kettlebell vertically against your chest with palms cupping the weight.",
                "Set feet shoulder-width apart with toes turned slightly outward.",
                "Keep chest high and push knees outwards as you descend down between your ankles.",
                "Achieve deep squat depth comfortably while maintaining a flat back.",
                "Push through the floor to return upright."
            ),
            proFormTips = listOf(
                "Use your elbows to gently nudge your knees outward at the bottom position.",
                "Keep your torso upright like a goblet filled with water that shouldn't spill.",
                "Great warm-up exercise for lubricating hip and knee cartilage."
            ),
            commonMistakes = listOf(
                "Holding the dumbbell away from chest, straining arms and neck.",
                "Collapsing chest forward during deep descent.",
                "Heels rising off floor."
            ),
            longevityScienceNote = "Deep full-range knee flexion under moderate front load increases synovial fluid diffusion across articular cartilage, preserving knee joint longevity."
,
            videoUrl = "https://www.youtube.com/embed/MeIiIdhgPwg"
        ),
        ExerciseLibraryItem(
            id = "chest_supported_row",
            name = "Chest-Supported Dumbbell Row",
            muscleGroup = "Upper Back (Rhomboids, Middle Traps, Latissimus Dorsi)",
            healthFocusCategory = HealthFocusCategory.SPINE_POSTURE,
            equipment = "Dumbbell & Incline Bench",
            difficulty = "Beginner",
            targetBonesAndJoints = "Scapulothoracic Joint, Thoracic Spine",
            summary = "Maximizes upper back muscle recruitment and scapular retraction while eliminating lower back shear stress.",
            stepByStepForm = listOf(
                "Lie face down on a 30 to 45-degree incline bench with feet firmly on floor.",
                "Hold dumbbells with arms fully extended towards the floor.",
                "Initiate movement by pulling shoulder blades together and down.",
                "Drive elbows back past your torso towards your hips, squeezing upper back muscles tight.",
                "Lower weights with control back to starting extended position."
            ),
            proFormTips = listOf(
                "Pause for 1-2 seconds at full contraction at top of each rep.",
                "Avoid shrugging shoulders up towards your ears.",
                "Press chest lightly against pad to prevent using momentum."
            ),
            commonMistakes = listOf(
                "Arching lower back off pad to lift heavier weights.",
                "Yanking dumbbells with arms rather than leading with shoulder blades.",
                "Rushing through the eccentric (lowering) portion."
            ),
            longevityScienceNote = "Strengthening upper back posture muscles reverses kyphotic rounding caused by sitting, protecting thoracic spinal discs and improving breathing kinematics."
,
            videoUrl = "https://www.youtube.com/embed/dQw4w9WgXcQ"
        ),
        ExerciseLibraryItem(
            id = "farmers_walk",
            name = "Farmer's Walk (Heavy Carry)",
            muscleGroup = "Grip, Forearms, Core, Traps, Hips",
            healthFocusCategory = HealthFocusCategory.BONE_DENSITY,
            equipment = "Dumbbells / Kettlebells",
            difficulty = "Beginner",
            targetBonesAndJoints = "Radius & Ulna, Metacarpals, Shoulder Girdle",
            summary = "One of the most potent predictor markers for longevity. Builds incredible grip strength, core bracing, and bone mass.",
            stepByStepForm = listOf(
                "Place two heavy kettlebells or dumbbells beside your feet.",
                "Squat down with flat back to grip weights firmly, then stand up tall.",
                "Pull shoulders back and down, brace core, and walk in a straight line with deliberate steps.",
                "Maintain upright posture without swaying or leaning.",
                "Set weights down under control after target distance/time."
            ),
            proFormTips = listOf(
                "Take short, quick, controlled heel-to-toe steps.",
                "Imagine holding tennis balls in your armpits to engage lats.",
                "Breathe continuously into belly while maintaining braced abdominal wall."
            ),
            commonMistakes = listOf(
                "Slouching shoulders forward or letting weight bounce against legs.",
                "Holding breath during the walk.",
                "Leaning laterally when carrying unequal loads."
            ),
            longevityScienceNote = "Grip strength correlates directly with cognitive reserve, cardiovascular health, and all-cause longevity in epidemiological research. Loaded carries exert compressive density forces on long arm bones and spine."
,
            videoUrl = "https://www.youtube.com/embed/dQw4w9WgXcQ"
        ),
        ExerciseLibraryItem(
            id = "bulgarian_split_squat",
            name = "Bulgarian Split Squat",
            muscleGroup = "Single-Leg (Quadriceps, Glutes, Adductors)",
            healthFocusCategory = HealthFocusCategory.BALANCE_FALL_PREVENTION,
            equipment = "Dumbbells & Bench",
            difficulty = "Intermediate",
            targetBonesAndJoints = "Hip Stabilizers, Patellofemoral Joint, Ankle",
            summary = "Unilateral lower body power movement that corrects muscle imbalances, improves dynamic balance, and prevents falls.",
            stepByStepForm = listOf(
                "Stand 2-3 feet in front of a bench, placing top of rear foot flat on the bench.",
                "Hold dumbbells at sides with tall spine and core engaged.",
                "Descend by bending front knee until rear knee nearly touches the floor.",
                "Keep front knee tracking over 2nd/3rd toe.",
                "Drive through front heel to return to standing position."
            ),
            proFormTips = listOf(
                "Leaning slightly forward at 10 degrees increases glute recruitment.",
                "Focus vision on a fixed stationary spot in front of you for stability.",
                "Start with bodyweight to master balance before adding weights."
            ),
            commonMistakes = listOf(
                "Front foot placed too close to bench, lifting front heel.",
                "Knee collapsing inward towards midline.",
                "Pushing off rear leg on bench instead of driving with front leg."
            ),
            longevityScienceNote = "Single-leg strength builds gluteus medius reactivity, maintaining lateral pelvic stability required for swift balance recovery during trips or stumbles."
,
            videoUrl = "https://www.youtube.com/embed/2C-uNgKwPLE"
        ),
        ExerciseLibraryItem(
            id = "pallof_press",
            name = "Pallof Press (Anti-Rotation)",
            muscleGroup = "Core (Obliques, Transverse Abdominis, Multifidus)",
            healthFocusCategory = HealthFocusCategory.CORE_STABILITY,
            equipment = "Cable / Resistance Band",
            difficulty = "Beginner",
            targetBonesAndJoints = "Lumbar Vertebrae, Rotational Spinal Joints",
            summary = "Isometric anti-rotational core movement that stabilizes the lower back and prevents lumbar torsional strain.",
            stepByStepForm = listOf(
                "Attach resistance band or cable at chest height.",
                "Stand perpendicular to anchor point, holding band/handle at center of chest with both hands.",
                "Set feet shoulder-width apart, knees slightly soft, core braced.",
                "Slowly press hands straight out in front of chest without letting torso rotate.",
                "Hold extended position for 2 seconds, then return hands to chest under control."
            ),
            proFormTips = listOf(
                "Exhale forcefully as you press outward to brace deep abdominal wall.",
                "Keep shoulders down away from ears.",
                "Resistance should feel like it wants to twist you towards the wall."
            ),
            commonMistakes = listOf(
                "Allowing hips or torso to rotate towards the anchor point.",
                "Rushing reps instead of pausing during full extension.",
                "Arching lower back."
            ),
            longevityScienceNote = "Anti-rotation training fortifies the transverse abdominis and deep spinal stabilization muscles, shielding intervertebral discs against twisting forces under load."
,
            videoUrl = "https://www.youtube.com/embed/nrsUXN0Ww58"
        ),
        ExerciseLibraryItem(
            id = "hip_thrust",
            name = "Barbell / Band Hip Thrust",
            muscleGroup = "Gluteus Maximus & Pelvic Floor",
            healthFocusCategory = HealthFocusCategory.POWER_MOBILITY,
            equipment = "Barbell / Band & Bench",
            difficulty = "Intermediate",
            targetBonesAndJoints = "Pelvic Girdle, Hip Joint, Sacroiliac Joint",
            summary = "Isolated hip extension builder that optimizes glute power, supports pelvic bone structure, and enhances stride speed.",
            stepByStepForm = listOf(
                "Sit on floor with upper back leaned against a padded bench, barbell over hips.",
                "Bend knees with feet flat on floor, hip-width apart.",
                "Drive through heels to extend hips vertically until shoulders, hips, and knees form a straight line.",
                "Squeeze glutes forcefully at full extension, keeping chin tucked slightly.",
                "Lower hips back to floor with control."
            ),
            proFormTips = listOf(
                "Keep ribcage down and abdominal wall engaged to prevent lower back arching.",
                "Use a foam barbell pad for hip comfort.",
                "Focus on squeezing glutes hard at the top."
            ),
            commonMistakes = listOf(
                "Over-extending lower back at top instead of extending through hips.",
                "Pushing through toes rather than heels.",
                "Placing feet too far out, shifting tension onto hamstrings."
            ),
            longevityScienceNote = "Strong glutes act as the primary engine for human locomotion, offloading excessive strain from lower back discs and hip joints."
,
            videoUrl = "https://www.youtube.com/embed/dQw4w9WgXcQ"
        ),
        ExerciseLibraryItem(
            id = "neutral_floor_press",
            name = "Neutral Grip Floor Press",
            muscleGroup = "Chest, Anterior Deltoids, Triceps",
            healthFocusCategory = HealthFocusCategory.JOINT_HEALTH,
            equipment = "Dumbbells",
            difficulty = "Beginner",
            targetBonesAndJoints = "Glenohumeral Joint, Rotator Cuff, Elbow",
            summary = "Delivers upper body pushing strength while safeguarding shoulder joints by limiting shoulder extension range.",
            stepByStepForm = listOf(
                "Lie flat on floor with knees bent and feet flat on ground.",
                "Hold dumbbells over chest with palms facing each other (neutral grip).",
                "Lower dumbbells smoothly until upper arms touch the floor softly.",
                "Pause for a second on floor, keeping tension in chest and arms.",
                "Press dumbbells back up over chest to full extension."
            ),
            proFormTips = listOf(
                "The floor acts as a natural safety stop preventing shoulder hyperextension.",
                "Neutral grip reduces impingement pressure on rotator cuff tendons.",
                "Squeeze chest muscles together at top."
            ),
            commonMistakes = listOf(
                "Letting elbows slam onto floor instead of soft touch.",
                "Flaring elbows wide to 90 degrees.",
                "Arching lower back off the floor."
            ),
            longevityScienceNote = "Ideal pressing alternative for individuals with prior shoulder discomfort, strengthening shoulder girdle bone attachments without tendon impingement."
,
            videoUrl = "https://www.youtube.com/embed/dQw4w9WgXcQ"
        ),
        ExerciseLibraryItem(
            id = "tibialis_raise",
            name = "Tibialis Raise",
            muscleGroup = "Anterior Shin (Tibialis Anterior)",
            healthFocusCategory = HealthFocusCategory.JOINT_HEALTH,
            equipment = "Bodyweight / Wall",
            difficulty = "Beginner",
            targetBonesAndJoints = "Ankle Joint, Shin Bone, Knee Cap",
            summary = "Strengthens the front shin muscle responsible for decelerating foot strike impact and protecting knee joint cartilage.",
            stepByStepForm = listOf(
                "Stand with lower back and buttocks resting flat against a smooth wall.",
                "Walk feet out approximately 1.5 to 2 feet away from wall with legs straight.",
                "Flex toes and feet upward towards shins as high as possible.",
                "Pause briefly at top flexed position.",
                "Lower forefeet back to ground with smooth control."
            ),
            proFormTips = listOf(
                "To increase difficulty, step feet further away from wall.",
                "Focus on pulling toes towards knees with maximum contraction.",
                "Perform 15-20 reps for high tendon endurance."
            ),
            commonMistakes = listOf(
                "Bending knees during the movement.",
                "Slapping feet down onto floor fast without control.",
                "Peeling buttocks off wall."
            ),
            longevityScienceNote = "The tibialis anterior absorbs ground force shock wave with every step during walking and stair climbing, acting as a natural brake for patellofemoral knee forces."
,
            videoUrl = "https://www.youtube.com/embed/gWia_82_xGo"
        ),
        ExerciseLibraryItem(
            id = "step_up_knee_drive",
            name = "Step-Up with Knee Drive",
            muscleGroup = "Quadriceps, Glutes, Hip Flexors, Ankle",
            healthFocusCategory = HealthFocusCategory.BALANCE_FALL_PREVENTION,
            equipment = "Dumbbells / Box or Bench",
            difficulty = "Intermediate",
            targetBonesAndJoints = "Hip Joint, Tibia, Patella, Ankle Ligaments",
            summary = "Functional climbing movement that trains high single-leg propulsion, hip flexor power, and dynamic stance balance.",
            stepByStepForm = listOf(
                "Stand facing a sturdy box or step (12-16 inches high).",
                "Place entire foot firmly onto the step.",
                "Drive through heel of lead foot to lift body upright, driving opposite knee up to 90-degree angle.",
                "Pause for 1 second at top single-leg balance point.",
                "Step back down with control and repeat."
            ),
            proFormTips = listOf(
                "Avoid pushing off with trailing bottom foot; force lead leg to do 100% of the work.",
                "Keep chest upright and tall throughout.",
                "Select box height where hip joint sits at or slightly below knee level."
            ),
            commonMistakes = listOf(
                "Bouncing off back toe to initiate movement.",
                "Letting knee cave inward during drive phase.",
                "Using box that is dangerously tall."
            ),
            longevityScienceNote = "Stair navigation and elevation step-ups are crucial functional independence tasks for older adults; step-ups preserve single-leg functional power reserves."
,
            videoUrl = "https://www.youtube.com/embed/9w_Y29G9H3Y"
        ),
        ExerciseLibraryItem(
            id = "face_pull",
            name = "Band / Cable Face Pull",
            muscleGroup = "Rear Deltoids, Rotator Cuff, Upper Trapezius",
            healthFocusCategory = HealthFocusCategory.SPINE_POSTURE,
            equipment = "Resistance Band / Cable",
            difficulty = "Beginner",
            targetBonesAndJoints = "Rotator Cuff, Glenohumeral Joint, Cervicothoracic Spine",
            summary = "Must-do shoulder health and scapular external rotation exercise that counters forward head and rounded shoulder posture.",
            stepByStepForm = listOf(
                "Attach band or cable rope at eye height.",
                "Grip rope with thumbs pointing backward towards you.",
                "Step back to create tension, chest high, knees soft.",
                "Pull hands towards eyes/ears while rotating forearms upward into a 'double bicep' pose.",
                "Squeeze rear shoulders tight for 1-2 seconds, then return smoothly."
            ),
            proFormTips = listOf(
                "Think of pulling hands apart as you reach your face.",
                "Keep elbows higher than wrists at full contraction.",
                "Focus on quality squeeze rather than heavy weight."
            ),
            commonMistakes = listOf(
                "Pulling rope down to chest instead of face/eyes.",
                "Leaning back and using body momentum.",
                "Internal rotation of shoulders at end range."
            ),
            longevityScienceNote = "Infraspinatus and teres minor strengthening maintains humerus head centralization in shoulder socket, preventing rotator cuff impingement."
,
            videoUrl = "https://www.youtube.com/embed/rep-qVOkqgk"
        ),
        ExerciseLibraryItem(
            id = "dead_bug_bracing",
            name = "Dead Bug Core Bracing",
            muscleGroup = "Deep Abdominals (Transverse Abdominis) & Pelvic Floor",
            healthFocusCategory = HealthFocusCategory.CORE_STABILITY,
            equipment = "Bodyweight",
            difficulty = "Beginner",
            targetBonesAndJoints = "Lumbar Vertebrae, Sacroiliac Joint",
            summary = "Gold-standard physical therapy exercise to teach lumbar stability while moving limbs independently.",
            stepByStepForm = listOf(
                "Lie on back with arms reaching straight up to ceiling, knees bent at 90 degrees directly over hips.",
                "Flatten lower back completely against the floor (no gap under lower back).",
                "Slowly extend right arm overhead while lowering left leg straight towards floor.",
                "Pause just above floor without letting lower back arch or peel off ground.",
                "Return to center and switch sides."
            ),
            proFormTips = listOf(
                "Exhale through pursed lips as you extend arm and leg.",
                "Place a small folded towel under lower back for tactile feedback.",
                "Quality over speed—slower pace builds deeper muscular control."
            ),
            commonMistakes = listOf(
                "Allowing lower back to arch off floor during extension.",
                "Rushing movement and losing breathing synchronization.",
                "Moving both legs at once."
            ),
            longevityScienceNote = "Teaches lumbo-pelvic dissociation, allowing limbs to generate power without transferring harmful spinal flexion or extension stress to lumbar discs."
,
            videoUrl = "https://www.youtube.com/embed/4XLEnwUr1d8"
        ),
        ExerciseLibraryItem(
            id = "overhead_press",
            name = "Single-Arm Dumbbell Overhead Press",
            muscleGroup = "Deltoids, Upper Chest, Core Obliques",
            healthFocusCategory = HealthFocusCategory.POWER_MOBILITY,
            equipment = "Dumbbell",
            difficulty = "Intermediate",
            targetBonesAndJoints = "Humeral Head, Scapula, Clavicle, Thoracic Spine",
            summary = "Unilateral vertical pressing exercise that promotes thoracic spine mobility and shoulder girdle strength.",
            stepByStepForm = listOf(
                "Stand tall with dumbbell at shoulder height in a neutral grip (palm facing ear).",
                "Brace core tight and squeeze non-working hand into a fist for stability.",
                "Press dumbbell overhead in a smooth path until arm is fully locked out next to ear.",
                "Keep ribs pressed down to avoid arching lower back.",
                "Lower weight back to shoulder height with control."
            ),
            proFormTips = listOf(
                "Single-arm variation allows natural scapular movement without forcing lumbar arching.",
                "If shoulder tightness limits overhead range, perform pressing at a 75-degree incline bench.",
                "Squeeze glutes tight throughout press."
            ),
            commonMistakes = listOf(
                "Bending backward at waist to press weight up.",
                "Flaring elbow directly to side instead of 30 degrees in scapular plane.",
                "Shrugging neck excessively."
            ),
            longevityScienceNote = "Overhead mobility preserves shoulder range required for reaching overhead shelves, loading luggage, and maintaining upper torso flexibility."
,
            videoUrl = "https://www.youtube.com/embed/QAQ64hK4Xxs"
        )
    )

    fun searchExercises(
        query: String = "",
        category: HealthFocusCategory = HealthFocusCategory.ALL,
        equipmentFilter: String = "All",
        muscleFilter: String = "All"
    ): List<ExerciseLibraryItem> {
        return exercises.filter { item ->
            // Category filter
            val matchesCategory = category == HealthFocusCategory.ALL || item.healthFocusCategory == category

            // Equipment filter
            val matchesEquipment = equipmentFilter == "All" || item.equipment.contains(equipmentFilter, ignoreCase = true)

            // Muscle group filter
            val matchesMuscle = muscleFilter == "All" || item.muscleGroup.contains(muscleFilter, ignoreCase = true)

            // Search query filter
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase()
                item.name.lowercase().contains(q) ||
                item.muscleGroup.lowercase().contains(q) ||
                item.targetBonesAndJoints.lowercase().contains(q) ||
                item.summary.lowercase().contains(q) ||
                item.equipment.lowercase().contains(q) ||
                item.healthFocusCategory.displayName.lowercase().contains(q) ||
                item.proFormTips.any { it.lowercase().contains(q) } ||
                item.longevityScienceNote.lowercase().contains(q)
            }

            matchesCategory && matchesEquipment && matchesMuscle && matchesQuery
        }
    }
}
