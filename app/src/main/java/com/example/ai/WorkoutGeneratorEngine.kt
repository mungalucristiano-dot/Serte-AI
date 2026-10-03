package com.example.ai

import com.example.data.model.ExerciseEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WorkoutEntity

data class WorkoutStage(
    val stageName: String,
    val durationSeconds: Int,
    val exercises: List<StageExercise>,
    val guidance: String
)

data class StageExercise(
    val name: String,
    val repsOrTime: String,
    val workSeconds: Int,
    val restSeconds: Int,
    val muscleGroup: String,
    val tip: String
)

object WorkoutGeneratorEngine {

    fun generateWorkout(
        goal: String,
        level: String,
        timeMinutes: Int,
        location: String,
        equipment: String,
        profile: UserProfileEntity?,
        availableExercises: List<ExerciseEntity>
    ): WorkoutEntity {
        // Evaluate user's adaptive feedback tendency
        val easyCount = profile?.feedbackEasyCount ?: 0
        val hardCount = profile?.feedbackHardCount ?: 0
        val modCount = profile?.feedbackModerateCount ?: 0

        // Adaptive scaling factor
        val adaptiveAdjustment: String = when {
            hardCount > easyCount + 1 -> "SUAVE" // User struggled previously -> adapt downwards, increase recovery
            easyCount > hardCount + 1 -> "PROGRESSIVO" // User found previous workouts easy -> adapt upwards
            else -> "EQUILIBRADO"
        }

        // Adjust work/rest intervals based on feedback
        val workSec: Int
        val restSec: Int
        when (adaptiveAdjustment) {
            "SUAVE" -> {
                workSec = 30
                restSec = 30
            }
            "PROGRESSIVO" -> {
                workSec = 45
                restSec = 15
            }
            else -> {
                workSec = 40
                restSec = 20
            }
        }

        // Filter suitable exercises
        val filtered = availableExercises.filter { ex ->
            val matchEq = if (equipment == "Sem equipamento") ex.equipment == "Sem equipamento" || ex.equipment == "Tapete" else true
            val matchLevel = when (level) {
                "Iniciante" -> ex.difficulty == "Iniciante" || adaptiveAdjustment == "PROGRESSIVO"
                "Avançado" -> ex.difficulty != "Iniciante" || adaptiveAdjustment == "SUAVE"
                else -> true
            }
            matchEq && matchLevel
        }.ifEmpty { availableExercises }

        val warmups = availableExercises.filter { it.category == "Mobilidade" || it.category == "Cardio" || it.category == "Flexibilidade" }
            .shuffled().take(2)
        val cooldowns = availableExercises.filter { it.category == "Flexibilidade" || it.category == "Mobilidade" }
            .shuffled().take(1)

        val mainPool = filtered.filter { it !in warmups && it !in cooldowns }.shuffled()
        val numMain = if (timeMinutes <= 15) 3 else 4
        val mainExercises = mainPool.take(numMain).ifEmpty { availableExercises.take(numMain) }

        val summaryList = mutableListOf<String>()
        warmups.forEach { summaryList.add(it.name) }
        mainExercises.forEach { summaryList.add(it.name) }
        cooldowns.forEach { summaryList.add(it.name) }

        // Build serialized stages representation
        val stagesBuilder = StringBuilder("[")
        // Stage 1: Warmup
        val warmupDuration = if (timeMinutes <= 10) 2 else 3
        stagesBuilder.append("{\"stageName\":\"Aquecimento Ativo\",\"duration\":\"$warmupDuration min\",\"guidance\":\"Prepare o sistema cardiovascular e aumente a temperatura articular.\",\"exercises\":[")
        warmups.forEachIndexed { i, ex ->
            stagesBuilder.append("{\"name\":\"${ex.name}\",\"repsOrTime\":\"${ex.defaultReps}\",\"workSeconds\":30,\"restSeconds\":15,\"muscleGroup\":\"${ex.targetMuscles}\",\"tip\":\"${ex.safetyTips}\"}")
            if (i < warmups.size - 1) stagesBuilder.append(",")
        }
        stagesBuilder.append("]},")

        // Stage 2: Main Block
        val mainDuration = timeMinutes - warmupDuration - 2
        stagesBuilder.append("{\"stageName\":\"Bloco Principal (${adaptiveAdjustment.lowercase()})\",\"duration\":\"$mainDuration min\",\"guidance\":\"Mantenha o ritmo constante. Descanse ${restSec}s entre séries.\",\"exercises\":[")
        mainExercises.forEachIndexed { i, ex ->
            val reps = if (adaptiveAdjustment == "PROGRESSIVO") "15 a 18 reps" else ex.defaultReps
            stagesBuilder.append("{\"name\":\"${ex.name}\",\"repsOrTime\":\"$reps\",\"workSeconds\":$workSec,\"restSeconds\":$restSec,\"muscleGroup\":\"${ex.targetMuscles}\",\"tip\":\"${ex.commonMistakes}\"}")
            if (i < mainExercises.size - 1) stagesBuilder.append(",")
        }
        stagesBuilder.append("]},")

        // Stage 3: Cooldown
        stagesBuilder.append("{\"stageName\":\"Finalização Regenerativa\",\"duration\":\"2 min\",\"guidance\":\"Desacelere os batimentos e respire profundamente.\",\"exercises\":[")
        cooldowns.forEachIndexed { i, ex ->
            stagesBuilder.append("{\"name\":\"${ex.name}\",\"repsOrTime\":\"${ex.defaultReps}\",\"workSeconds\":45,\"restSeconds\":10,\"muscleGroup\":\"${ex.targetMuscles}\",\"tip\":\"${ex.safetyTips}\"}")
            if (i < cooldowns.size - 1) stagesBuilder.append(",")
        }
        stagesBuilder.append("]}]")

        val titlePrefix = when (adaptiveAdjustment) {
            "SUAVE" -> "Treino Serte Regenerativo"
            "PROGRESSIVO" -> "Treino Serte Intensificado"
            else -> "Treino Serte AI de $goal"
        }

        val estimatedCalories = (timeMinutes * 6.5).toInt()

        return WorkoutEntity(
            title = "$titlePrefix ($timeMinutes min)",
            goal = goal,
            level = level,
            durationMinutes = timeMinutes,
            location = location,
            equipment = equipment,
            exercisesSummary = summaryList.joinToString(", "),
            structuredStagesJson = stagesBuilder.toString(),
            generatedByAi = true,
            createdAt = System.currentTimeMillis(),
            isCompleted = false,
            caloriesEstimated = estimatedCalories
        )
    }
}
