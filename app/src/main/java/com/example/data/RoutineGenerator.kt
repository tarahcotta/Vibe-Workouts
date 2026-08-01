package com.example.data

object RoutineGenerator {

    fun generateRoutineForProfile(profile: UserProfileEntity): List<Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>>> {
        val days = profile.scheduleDaysPerWeek.coerceIn(2, 4)
        val equipment = profile.availableEquipment
        val hasKneeIssues = profile.jointHistory.contains("Knees", ignoreCase = true)
        val hasBackIssues = profile.jointHistory.contains("Lower Back", ignoreCase = true)
        val hasShoulderIssues = profile.jointHistory.contains("Shoulders", ignoreCase = true)

        val routines = mutableListOf<Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>>>()

        when (days) {
            2 -> {
                // 2-Day Split: Full Body A & Full Body B
                routines.add(createFullBodyA(equipment, hasKneeIssues, hasBackIssues, hasShoulderIssues))
                routines.add(createFullBodyB(equipment, hasKneeIssues, hasBackIssues, hasShoulderIssues))
            }
            3 -> {
                // 3-Day Split: Day 1 Squat & Push, Day 2 Hinge & Pull, Day 3 Full Body & Balance
                routines.add(create3Day1(equipment, hasKneeIssues, hasBackIssues, hasShoulderIssues))
                routines.add(create3Day2(equipment, hasKneeIssues, hasBackIssues, hasShoulderIssues))
                routines.add(create3Day3(equipment, hasKneeIssues, hasBackIssues, hasShoulderIssues))
            }
            4 -> {
                // 4-Day Split: Lower A (Squat), Upper A (Push/Pull), Lower B (Hinge/Single Leg), Upper B & Carry
                routines.add(create4DayLowerA(equipment, hasKneeIssues, hasBackIssues))
                routines.add(create4DayUpperA(equipment, hasShoulderIssues))
                routines.add(create4DayLowerB(equipment, hasKneeIssues, hasBackIssues))
                routines.add(create4DayUpperB(equipment, hasShoulderIssues))
            }
        }

        return routines
    }

    private fun createFullBodyA(
        equipment: String,
        knee: Boolean,
        back: Boolean,
        shoulder: Boolean
    ): Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>> {
        val routine = WorkoutRoutineEntity(
            title = "Longevity Strength Split",
            dayName = "Day 1: Heavy Axial Loading & Posture Focus",
            focusSummary = "Axial skeletal strain for bone density, upper back retraction, core bracing."
        )

        val exercises = mutableListOf<WorkoutExerciseEntity>()

        // 1. Primary Lower Compound (Bone Density)
        val squatEx = when {
            equipment.contains("Barbell", ignoreCase = true) -> if (knee || back) {
                WorkoutExerciseEntity(0, 0, "Barbell Box Squat", "Bone Density", 3, "6-8 reps", "120s", "RPE 7-8", "Sit back onto box under control. Drive feet hard into floor on ascent.", 1)
            } else {
                WorkoutExerciseEntity(0, 0, "Barbell Back Squat", "Bone Density", 3, "6-8 reps", "120s", "RPE 7-8", "Full axial loading for hip/spine density. Keep chest tall and brace core.", 1)
            }
            equipment.contains("Dumbbell", ignoreCase = true) -> WorkoutExerciseEntity(0, 0, "Goblet Squat (Heavy)", "Bone Density", 3, "6-8 reps", "90s", "RPE 7-8", "Hold heavy bell close to chest. Drive knees out over toes, tall spine.", 1)
            else -> WorkoutExerciseEntity(0, 0, "Tempo Bodyweight Squat + Hold", "Bone Density", 3, "10-12 reps", "60s", "RPE 8", "3-second descent, 2-second pause at bottom to maximize tendon/bone strain.", 1)
        }
        exercises.add(squatEx)

        // 2. Horizontal Pull (Posture & Upper Back)
        val rowEx = if (back) {
            WorkoutExerciseEntity(0, 0, "Chest-Supported Dumbbell Row", "Posture & Scapular Strength", 3, "8-10 reps", "90s", "RPE 7-8", "Eliminate lower back strain. Drive elbows back, squeeze shoulder blades.", 2)
        } else if (equipment.contains("Barbell", ignoreCase = true)) {
            WorkoutExerciseEntity(0, 0, "Bent-Over Barbell Row", "Posture & Spine Support", 3, "8-10 reps", "90s", "RPE 7-8", "Brace core tight, hinge at hips 45 degrees, pull bar to navel.", 2)
        } else {
            WorkoutExerciseEntity(0, 0, "Single-Arm Dumbbell Row", "Posture & Upper Back", 3, "8-10 reps", "90s", "RPE 7-8", "Hand supported on bench. Pull dumbbell to hip, keep chest square.", 2)
        }
        exercises.add(rowEx)

        // 3. Horizontal Push (Upper Body Strength)
        val pushEx = if (shoulder) {
            WorkoutExerciseEntity(0, 0, "Neutral Grip Dumbbell Floor Press", "Joint Safety & Strength", 3, "8-10 reps", "90s", "RPE 7-8", "Floor limits shoulder extension range, neutral grip protects rotator cuff.", 3)
        } else if (equipment.contains("Barbell", ignoreCase = true)) {
            WorkoutExerciseEntity(0, 0, "Barbell Bench Press", "Upper Body Density", 3, "6-8 reps", "90s", "RPE 7-8", "Plant feet firm, pull shoulder blades together, press bar with authority.", 3)
        } else {
            WorkoutExerciseEntity(0, 0, "Incline Dumbbell Press", "Upper Body & Shoulder Health", 3, "8-10 reps", "90s", "RPE 7-8", "45-degree bench angle, press bells together smoothly.", 3)
        }
        exercises.add(pushEx)

        // 4. Single-Leg & Balance (Fall Prevention)
        val balanceEx = WorkoutExerciseEntity(0, 0, "Reverse Lunge or Step-Up", "Single-Leg Balance & Hip Stability", 3, "10-12 reps/leg", "60s", "RPE 7", "Press through front heel. Stabilize pelvis to protect knees and hips.", 4)
        exercises.add(balanceEx)

        // 5. Loaded Carry (Grip Strength & Longevity)
        val carryEx = WorkoutExerciseEntity(0, 0, "Farmer's Walk (Heavy Carry)", "Grip Strength & Core Stability", 3, "45s carry", "60s", "RPE 8", "Stand tall, stack ribs over pelvis, grip heavy bells firmly without leaning.", 5)
        exercises.add(carryEx)

        return Pair(routine, exercises)
    }

    private fun createFullBodyB(
        equipment: String,
        knee: Boolean,
        back: Boolean,
        shoulder: Boolean
    ): Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>> {
        val routine = WorkoutRoutineEntity(
            title = "Longevity Strength Split",
            dayName = "Day 2: Posterior Chain Hinge & Functional Mobility",
            focusSummary = "Femoral head bone loading via hip hinge, grip, pelvic stability, anti-rotation core."
        )

        val exercises = mutableListOf<WorkoutExerciseEntity>()

        // 1. Primary Hinge (Bone Density - Hip & Lumbar)
        val hingeEx = when {
            back -> WorkoutExerciseEntity(0, 0, "Trap Bar / Dumbbell RDL", "Bone Density (Hip/Spine)", 3, "8-10 reps", "120s", "RPE 7-8", "Push hips back like closing a door with glutes. Keep weights tight to shins.", 1)
            equipment.contains("Barbell", ignoreCase = true) -> WorkoutExerciseEntity(0, 0, "Barbell Conventional Deadlift", "Bone Density & Posterior Chain", 3, "5-8 reps", "120s", "RPE 7-8", "Prime movement for spinal and pelvic bone density. Lock lats, drive heels.", 1)
            else -> WorkoutExerciseEntity(0, 0, "Heavy Romanian Deadlift (RDL)", "Bone Density (Femur/Hip)", 3, "8-10 reps", "90s", "RPE 7-8", "Soft knee bend, push hips back, stretch hamstrings, squeeze glutes to stand.", 1)
        }
        exercises.add(hingeEx)

        // 2. Overhead Vertical Press (Shoulder Health & Bone Load)
        val pressEx = if (shoulder) {
            WorkoutExerciseEntity(0, 0, "Single-Arm Landmine or High Incline Press", "Shoulder Preservation", 3, "8-10 reps", "90s", "RPE 7", "Angled pressing trajectory protects shoulder impingement zone.", 2)
        } else {
            WorkoutExerciseEntity(0, 0, "Standing Overhead Dumbbell Press", "Spine Loading & Posture", 3, "8-10 reps", "90s", "RPE 7-8", "Brace glutes and abs. Press bells overhead without arching lower back.", 2)
        }
        exercises.add(pressEx)

        // 3. Vertical Pull (Upper Back & Grip)
        val pullEx = WorkoutExerciseEntity(0, 0, "Lat Pulldown or Band Assisted Pull-Up", "Posture & Scapular Health", 3, "10-12 reps", "90s", "RPE 7-8", "Pull bar toward upper chest, initiate movement by pulling shoulder blades down.", 3)
        exercises.add(pullEx)

        // 4. Glute Isolation (Hip Stability & Joint Protection)
        val gluteEx = WorkoutExerciseEntity(0, 0, "Barbell / Dumbbell Hip Thrust", "Glute Capacity & Sacral Load", 3, "10-12 reps", "60s", "RPE 8", "Upper back rested on bench. Drive through heels, pause 1s at top extension.", 4)
        exercises.add(gluteEx)

        // 5. Anti-Rotation Core (Core Stability & Spine Protection)
        val coreEx = WorkoutExerciseEntity(0, 0, "Pallof Press or Suitcase Hold", "Core Anti-Rotation & Posture", 3, "30s hold/side", "60s", "RPE 7", "Resist rotational pull. Keep shoulders square and hips braced.", 5)
        exercises.add(coreEx)

        return Pair(routine, exercises)
    }

    private fun create3Day1(equipment: String, knee: Boolean, back: Boolean, shoulder: Boolean): Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>> {
        val (r, e) = createFullBodyA(equipment, knee, back, shoulder)
        val updatedR = r.copy(dayName = "Day 1: Heavy Axial Load & Push Strength", focusSummary = "Squat pattern bone loading, overhead/horizontal push, postural stability.")
        return Pair(updatedR, e)
    }

    private fun create3Day2(equipment: String, knee: Boolean, back: Boolean, shoulder: Boolean): Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>> {
        val (r, e) = createFullBodyB(equipment, knee, back, shoulder)
        val updatedR = r.copy(dayName = "Day 2: Hinge, Grip & Posterior Chain", focusSummary = "Hip hinge femoral loading, upper back pulling, grip strength, glute drive.")
        return Pair(updatedR, e)
    }

    private fun create3Day3(equipment: String, knee: Boolean, back: Boolean, shoulder: Boolean): Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>> {
        val routine = WorkoutRoutineEntity(
            title = "Longevity Strength Split",
            dayName = "Day 3: Balance, Unilateral Capacity & Longevity Carries",
            focusSummary = "Single-leg proprioception, multi-planar core, heavy carries for grip & bone density."
        )

        val exercises = mutableListOf(
            WorkoutExerciseEntity(0, 0, "Bulgarian Split Squat or Step-Up", "Single-Leg Balance & Bone Strain", 3, "8-10 reps/leg", "90s", "RPE 7-8", "Build unilateral hip strength to prevent falls. Keep front foot flat.", 1),
            WorkoutExerciseEntity(0, 0, "Chest-Supported Row or Face Pulls", "Posture & Rotator Cuff Health", 3, "10-12 reps", "60s", "RPE 7", "Focus on rear delts and mid-traps. Hold peak contraction 1 second.", 2),
            WorkoutExerciseEntity(0, 0, "Single-Leg Romanian Deadlift", "Balance & Hamstring Capacity", 3, "8-10 reps/leg", "60s", "RPE 7", "Hinge on standing leg, extend trailing leg behind. Protects ankle/hip joints.", 3),
            WorkoutExerciseEntity(0, 0, "Suitcase Carry (Unilateral)", "Anti-Lateral Flexion Core & Grip", 3, "40s carry/side", "60s", "RPE 8", "Carry heavy dumbbell on one side. Walk tall without dipping sideways.", 4),
            WorkoutExerciseEntity(0, 0, "Standing Calf Raise & Ankle Mobility", "Ankle Complex & Impact Capacity", 3, "12-15 reps", "45s", "RPE 8", "Full stretch at bottom, strong contraction at top for ankle stability.", 5)
        )

        return Pair(routine, exercises)
    }

    private fun create4DayLowerA(equipment: String, knee: Boolean, back: Boolean): Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>> {
        val routine = WorkoutRoutineEntity(
            title = "Longevity Strength Split",
            dayName = "Day 1: Lower Body - Squat & Bone Density",
            focusSummary = "Axial loading through squat pattern, glute hypertrophy, ankle stability."
        )
        val exercises = listOf(
            WorkoutExerciseEntity(0, 0, "Goblet Squat / Barbell Squat", "Bone Density", 4, "6-8 reps", "120s", "RPE 7-8", "Drive floor away, chest elevated.", 1),
            WorkoutExerciseEntity(0, 0, "Reverse Lunges", "Single-Leg Balance", 3, "10 reps/leg", "90s", "RPE 7", "Control landing, press through front midfoot.", 2),
            WorkoutExerciseEntity(0, 0, "Barbell / Dumbbell Hip Thrust", "Glute Density & Hip Power", 3, "10-12 reps", "90s", "RPE 8", "Pause at top extension.", 3),
            WorkoutExerciseEntity(0, 0, "Farmer's Walk", "Grip Strength & Core", 3, "45s carry", "60s", "RPE 8", "Stand upright, smooth gait.", 4)
        )
        return Pair(routine, exercises)
    }

    private fun create4DayUpperA(equipment: String, shoulder: Boolean): Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>> {
        val routine = WorkoutRoutineEntity(
            title = "Longevity Strength Split",
            dayName = "Day 2: Upper Body - Posture & Press Strength",
            focusSummary = "Horizontal push/pull balance, scapular retraction, rotator cuff safety."
        )
        val exercises = listOf(
            WorkoutExerciseEntity(0, 0, "Dumbbell / Barbell Bench Press", "Upper Body Density", 4, "6-8 reps", "90s", "RPE 7-8", "Lock shoulder blades into bench.", 1),
            WorkoutExerciseEntity(0, 0, "Single-Arm Dumbbell Row", "Posture & Scapular Pull", 4, "8-10 reps", "90s", "RPE 7-8", "Pull elbow to hip.", 2),
            WorkoutExerciseEntity(0, 0, "Face Pulls / Band Pull-Aparts", "Rotator Cuff & Shoulder Health", 3, "12-15 reps", "60s", "RPE 7", "Pull band to nose level, squeeze rear delts.", 3),
            WorkoutExerciseEntity(0, 0, "Plank / Anti-Extension Hold", "Core Bracing", 3, "45s hold", "60s", "RPE 7", "Maintain rib-cage to pelvis alignment.", 4)
        )
        return Pair(routine, exercises)
    }

    private fun create4DayLowerB(equipment: String, knee: Boolean, back: Boolean): Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>> {
        val routine = WorkoutRoutineEntity(
            title = "Longevity Strength Split",
            dayName = "Day 3: Lower Body - Hinge & Spine Density",
            focusSummary = "Femoral neck loading, hamstring hypertrophy, single-leg balance."
        )
        val exercises = listOf(
            WorkoutExerciseEntity(0, 0, "Romanian Deadlift (RDL)", "Bone Density (Hip & Lumbar)", 4, "6-8 reps", "120s", "RPE 7-8", "Hinge at hips, keep spine locked.", 1),
            WorkoutExerciseEntity(0, 0, "Step-Ups onto Box/Bench", "Single-Leg Balance & Knee Health", 3, "10 reps/leg", "90s", "RPE 7", "Drive through elevated leg without bouncing off trailing toe.", 2),
            WorkoutExerciseEntity(0, 0, "Glute Ham Bridge / Curl", "Hamstring Capacity", 3, "10-12 reps", "60s", "RPE 7", "Brace core, contract hamstrings.", 3),
            WorkoutExerciseEntity(0, 0, "Suitcase Carry", "Anti-Lateral Core & Grip", 3, "40s carry/side", "60s", "RPE 8", "Keep torso completely vertical while walking.", 4)
        )
        return Pair(routine, exercises)
    }

    private fun create4DayUpperB(equipment: String, shoulder: Boolean): Pair<WorkoutRoutineEntity, List<WorkoutExerciseEntity>> {
        val routine = WorkoutRoutineEntity(
            title = "Longevity Strength Split",
            dayName = "Day 4: Upper Body - Vertical Press & Pull Capacity",
            focusSummary = "Overhead loading for spine health, lat pull, core anti-rotation."
        )
        val exercises = listOf(
            WorkoutExerciseEntity(0, 0, "Standing Overhead Press", "Spine Support & Shoulder Power", 4, "8-10 reps", "90s", "RPE 7-8", "Brace glutes and abs tight.", 1),
            WorkoutExerciseEntity(0, 0, "Lat Pulldown / Pull-Up", "Posture & Lat Strength", 4, "8-10 reps", "90s", "RPE 7-8", "Pull bar to upper sternum.", 2),
            WorkoutExerciseEntity(0, 0, "Incline Dumbbell Fly / Y-Raise", "Posture & Scapular Range", 3, "12-15 reps", "60s", "RPE 7", "Control eccentric phase.", 3),
            WorkoutExerciseEntity(0, 0, "Pallof Press", "Core Anti-Rotation", 3, "12 reps/side", "60s", "RPE 7", "Press handles forward without trunk rotation.", 4)
        )
        return Pair(routine, exercises)
    }
}
