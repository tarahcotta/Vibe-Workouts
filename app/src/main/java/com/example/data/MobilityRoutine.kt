package com.example.data

data class MobilityDrill(
    val title: String,
    val targetArea: String,
    val durationOrReps: String,
    val instructionCue: String,
    val primaryBenefit: String
)

object MobilityRoutineManager {

    fun generateTailoredWarmup(exercises: List<WorkoutExerciseEntity>): List<MobilityDrill> {
        val exerciseNames = exercises.map { it.exerciseName.lowercase() }
        val primaryGoals = exercises.map { it.primaryGoal.lowercase() }

        val hasSquat = exerciseNames.any { it.contains("squat") || it.contains("leg press") } ||
                primaryGoals.any { it.contains("quad") || it.contains("knee") }

        val hasHinge = exerciseNames.any { it.contains("deadlift") || it.contains("rdl") || it.contains("hinge") || it.contains("hip") } ||
                primaryGoals.any { it.contains("posterior") || it.contains("glute") || it.contains("hamstring") }

        val hasPress = exerciseNames.any { it.contains("press") || it.contains("bench") || it.contains("dip") } ||
                primaryGoals.any { it.contains("chest") || it.contains("shoulder") || it.contains("triceps") }

        val hasPull = exerciseNames.any { it.contains("row") || it.contains("pull") || it.contains("lat") } ||
                primaryGoals.any { it.contains("back") || it.contains("posture") || it.contains("lat") }

        val drills = mutableListOf<MobilityDrill>()

        // 1. Lower body primary lift mobility
        if (hasSquat) {
            drills.add(
                MobilityDrill(
                    title = "90/90 Hip Capsule Switches",
                    targetArea = "Hip Joint & Pelvis",
                    durationOrReps = "60 seconds (8 switches)",
                    instructionCue = "Sit tall with knees at 90 degrees. Smoothly rotate knees side to side without touching hands to ground.",
                    primaryBenefit = "Lubricates hip socket, improving squat depth and knee joint comfort."
                )
            )
            drills.add(
                MobilityDrill(
                    title = "Ankle Dorsiflexion Wall Slides",
                    targetArea = "Calf & Achilles Tendon",
                    durationOrReps = "60 seconds (10 reps/side)",
                    instructionCue = "Face wall with front foot 4 inches away. Drive front knee toward wall keeping heel glued down.",
                    primaryBenefit = "Enhances ankle mobility to maintain upright torso under heavy loaded squats."
                )
            )
        }

        if (hasHinge) {
            drills.add(
                MobilityDrill(
                    title = "Cat-Camel Spinal Waves",
                    targetArea = "Thoracic & Lumbar Spine",
                    durationOrReps = "60 seconds (10 fluid reps)",
                    instructionCue = "On quadruped position, arch upper back high on exhale, then gently sag lumbar into light extension on inhale.",
                    primaryBenefit = "Hydrates intervertebral discs and prepares spinal stabilizers for hinge loading."
                )
            )
            drills.add(
                MobilityDrill(
                    title = "Hinge to Overhead Y-Reach",
                    targetArea = "Hamstrings & Lat Alignment",
                    durationOrReps = "60 seconds (10 reps)",
                    instructionCue = "Soft knees, push hips back into stretch, then extend hips forward and reach arms overhead into Y position.",
                    primaryBenefit = "Activates posterior chain tension tolerance prior to heavy pulls."
                )
            )
        }

        // 2. Upper body primary lift mobility
        if (hasPress || hasPull) {
            drills.add(
                MobilityDrill(
                    title = "Thoracic Windmills & Openers",
                    targetArea = "Mid-Back & Shoulder Capsule",
                    durationOrReps = "60 seconds (6/side)",
                    instructionCue = "Side-lying with top knee pinned to ground. Sweep top arm in a wide circle over head, exhaling on opening.",
                    primaryBenefit = "Unlocks upper back rotation, taking strain off AC joints during pressing and pulling."
                )
            )
            drills.add(
                MobilityDrill(
                    title = "Scapular Wall Slides",
                    targetArea = "Lower Trapezius & Serratus",
                    durationOrReps = "60 seconds (10 slow reps)",
                    instructionCue = "Back against wall, press wrists and elbows flat while sliding arms overhead without arching lower back.",
                    primaryBenefit = "Activates scapular stabilizers to protect rotator cuff under overhead/pressing loads."
                )
            )
        }

        // Anchor Universal Mobility Drill if less than 4 drills accumulated
        if (drills.size < 4) {
            drills.add(
                MobilityDrill(
                    title = "World's Greatest Stretch & T-Reach",
                    targetArea = "Full Kinetic Chain (Hips + T-Spine)",
                    durationOrReps = "60 seconds (5 reps/side)",
                    instructionCue = "Step into deep runner's lunge, drop inside elbow to instep, then rotate arm high up toward ceiling.",
                    primaryBenefit = "Comprehensive full-body mobilization for multi-joint compound lifts."
                )
            )
        }

        if (drills.size < 4) {
            drills.add(
                MobilityDrill(
                    title = "Deep Squat Pry with Thoracic Reaches",
                    targetArea = "Hip Adductors & Ankle Capsules",
                    durationOrReps = "60 seconds hold",
                    instructionCue = "Sink into bottom squat, press elbows against inner knees, and gently alternate reaching arms overhead.",
                    primaryBenefit = "Opens hip adductors and primes ankle/knee joint fluids for maximum load safety."
                )
            )
        }

        return drills.take(4)
    }
}
