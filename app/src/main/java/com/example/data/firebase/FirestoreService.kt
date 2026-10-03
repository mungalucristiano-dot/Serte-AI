package com.example.data.firebase

import android.util.Log
import com.example.data.model.UserProfileEntity
import com.example.data.model.WorkoutEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreService {

    private val tag = "FirestoreService"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(FirebaseApp.getInstance().applicationContext).isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(tag, "FirebaseApp não inicializado ainda ou google-services.json ausente: ${e.message}")
            null
        }
    }

    suspend fun syncUserProfile(profile: UserProfileEntity): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.failure(Exception("Firestore não configurado ou sem conexão."))
        try {
            val userMap = hashMapOf(
                "name" to profile.name,
                "ageRange" to profile.ageRange,
                "fitnessLevel" to profile.fitnessLevel,
                "primaryGoal" to profile.primaryGoal,
                "dailyMinutes" to profile.dailyMinutes,
                "location" to profile.location,
                "equipment" to profile.equipment,
                "weeklyDays" to profile.weeklyDays,
                "xp" to profile.xp,
                "level" to profile.level,
                "streakDays" to profile.streakDays,
                "lastWorkoutDate" to profile.lastWorkoutDate,
                "completedWorkoutsCount" to profile.completedWorkoutsCount,
                "feedbackEasyCount" to profile.feedbackEasyCount,
                "feedbackModerateCount" to profile.feedbackModerateCount,
                "feedbackHardCount" to profile.feedbackHardCount,
                "updatedAt" to System.currentTimeMillis()
            )

            val docRef = db.collection("users").document("user_${profile.id}")
            docRef.set(userMap, SetOptions.merge()).await()
            Log.d(tag, "Perfil sincronizado no Firestore com sucesso!")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Erro ao sincronizar perfil no Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun syncWorkout(userId: Int, workout: WorkoutEntity): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.failure(Exception("Firestore não configurado."))
        try {
            val workoutMap = hashMapOf(
                "id" to workout.id,
                "title" to workout.title,
                "goal" to workout.goal,
                "level" to workout.level,
                "durationMinutes" to workout.durationMinutes,
                "location" to workout.location,
                "equipment" to workout.equipment,
                "exercisesSummary" to workout.exercisesSummary,
                "structuredStagesJson" to workout.structuredStagesJson,
                "generatedByAi" to workout.generatedByAi,
                "createdAt" to workout.createdAt,
                "isCompleted" to workout.isCompleted,
                "completedAt" to (workout.completedAt ?: 0L),
                "userFeedback" to (workout.userFeedback ?: ""),
                "caloriesEstimated" to workout.caloriesEstimated,
                "syncedAt" to System.currentTimeMillis()
            )

            db.collection("users")
                .document("user_$userId")
                .collection("workouts")
                .document("workout_${workout.id}")
                .set(workoutMap, SetOptions.merge())
                .await()

            Log.d(tag, "Treino ${workout.id} sincronizado no Firestore!")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Erro ao sincronizar treino no Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun syncProgress(profile: UserProfileEntity): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.failure(Exception("Firestore não configurado."))
        try {
            val progressMap = hashMapOf(
                "xp" to profile.xp,
                "level" to profile.level,
                "streakDays" to profile.streakDays,
                "completedWorkouts" to profile.completedWorkoutsCount,
                "feedbackDistribution" to mapOf(
                    "facil" to profile.feedbackEasyCount,
                    "moderado" to profile.feedbackModerateCount,
                    "dificil" to profile.feedbackHardCount
                ),
                "lastActive" to System.currentTimeMillis()
            )

            db.collection("users")
                .document("user_${profile.id}")
                .collection("progress")
                .document("summary")
                .set(progressMap, SetOptions.merge())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Erro ao sincronizar progresso no Firestore", e)
            Result.failure(e)
        }
    }

    suspend fun fetchCloudProfile(userId: Int): UserProfileEntity? = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext null
        try {
            val snapshot = db.collection("users").document("user_$userId").get().await()
            if (snapshot.exists()) {
                UserProfileEntity(
                    id = userId,
                    name = snapshot.getString("name") ?: "Atleta Serte",
                    ageRange = snapshot.getString("ageRange") ?: "25-34",
                    fitnessLevel = snapshot.getString("fitnessLevel") ?: "Iniciante",
                    primaryGoal = snapshot.getString("primaryGoal") ?: "Condicionamento",
                    dailyMinutes = snapshot.getLong("dailyMinutes")?.toInt() ?: 20,
                    location = snapshot.getString("location") ?: "Casa",
                    equipment = snapshot.getString("equipment") ?: "Sem equipamento",
                    weeklyDays = snapshot.getLong("weeklyDays")?.toInt() ?: 4,
                    xp = snapshot.getLong("xp")?.toInt() ?: 100,
                    level = snapshot.getLong("level")?.toInt() ?: 1,
                    streakDays = snapshot.getLong("streakDays")?.toInt() ?: 1,
                    lastWorkoutDate = snapshot.getLong("lastWorkoutDate") ?: System.currentTimeMillis(),
                    completedWorkoutsCount = snapshot.getLong("completedWorkoutsCount")?.toInt() ?: 0,
                    feedbackEasyCount = snapshot.getLong("feedbackEasyCount")?.toInt() ?: 0,
                    feedbackModerateCount = snapshot.getLong("feedbackModerateCount")?.toInt() ?: 0,
                    feedbackHardCount = snapshot.getLong("feedbackHardCount")?.toInt() ?: 0,
                    isOnboarded = true
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(tag, "Perfil não encontrado no Firestore ou offline: ${e.message}")
            null
        }
    }
}
