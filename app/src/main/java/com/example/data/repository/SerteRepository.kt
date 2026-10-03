package com.example.data.repository

import com.example.data.firebase.FirestoreService
import com.example.data.local.SerteDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class SerteRepository(
    private val dao: SerteDao,
    private val firestoreService: FirestoreService = FirestoreService()
) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val allExercises: Flow<List<ExerciseEntity>> = dao.getAllExercises()
    val allWorkouts: Flow<List<WorkoutEntity>> = dao.getAllWorkouts()
    val allChallenges: Flow<List<ChallengeEntity>> = dao.getAllChallenges()
    val allHabits: Flow<List<HabitEntity>> = dao.getAllHabits()
    val allAchievements: Flow<List<AchievementEntity>> = dao.getAllAchievements()
    val allChatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()
    val communityPosts: Flow<List<CommunityPostEntity>> = dao.getCommunityPosts()
    val aiAnalytics: Flow<AIAnalyticsEntity?> = dao.getAIAnalytics()
    val allArticles: Flow<List<EducationalArticleEntity>> = dao.getAllArticles()

    fun filterArticles(category: String, query: String): Flow<List<EducationalArticleEntity>> {
        return dao.filterArticles(category, query)
    }

    suspend fun toggleBookmarkArticle(article: EducationalArticleEntity) {
        dao.updateArticle(article.copy(isBookmarked = !article.isBookmarked))
    }

    fun filterExercises(
        category: String,
        difficulty: String,
        equipment: String,
        query: String
    ): Flow<List<ExerciseEntity>> {
        return dao.filterExercises(category, difficulty, equipment, query)
    }

    suspend fun getExerciseById(id: Long): ExerciseEntity? = dao.getExerciseById(id)

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        dao.insertOrUpdateProfile(profile)
        firestoreService.syncUserProfile(profile)
        firestoreService.syncProgress(profile)
    }

    suspend fun addExercise(exercise: ExerciseEntity): Long {
        return dao.insertExercise(exercise)
    }

    suspend fun updateExercise(exercise: ExerciseEntity) {
        dao.updateExercise(exercise)
    }

    suspend fun saveWorkout(workout: WorkoutEntity): Long {
        val id = dao.insertWorkout(workout)
        // Update analytics
        val analytics = dao.getAIAnalytics().firstOrNull() ?: AIAnalyticsEntity()
        dao.updateAIAnalytics(
            analytics.copy(
                totalWorkoutsGenerated = analytics.totalWorkoutsGenerated + 1
            )
        )
        // Sync to cloud
        val user = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        firestoreService.syncWorkout(user.id, workout.copy(id = id))
        return id
    }

    suspend fun completeWorkoutWithFeedback(workoutId: Long, feedback: String) {
        val workout = dao.getWorkoutById(workoutId) ?: return
        val updatedWorkout = workout.copy(
            isCompleted = true,
            completedAt = System.currentTimeMillis(),
            userFeedback = feedback
        )
        dao.updateWorkout(updatedWorkout)

        // Update User Profile with Adaptive XP, Streak, and Feedback counts
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        val newEasy = if (feedback.equals("Fácil", ignoreCase = true)) profile.feedbackEasyCount + 1 else profile.feedbackEasyCount
        val newMod = if (feedback.equals("Moderado", ignoreCase = true)) profile.feedbackModerateCount + 1 else profile.feedbackModerateCount
        val newHard = if (feedback.equals("Difícil", ignoreCase = true)) profile.feedbackHardCount + 1 else profile.feedbackHardCount

        val xpGained = 15
        val newXp = profile.xp + xpGained
        val newLevel = (newXp / 100) + 1
        val newWorkoutsCount = profile.completedWorkoutsCount + 1

        val updatedProfile = profile.copy(
            xp = newXp,
            level = newLevel,
            completedWorkoutsCount = newWorkoutsCount,
            feedbackEasyCount = newEasy,
            feedbackModerateCount = newMod,
            feedbackHardCount = newHard,
            lastWorkoutDate = System.currentTimeMillis()
        )
        dao.insertOrUpdateProfile(updatedProfile)

        // Sync workout and progress to Firestore
        firestoreService.syncWorkout(profile.id, updatedWorkout)
        firestoreService.syncProgress(updatedProfile)

        // Check and unlock first workout badge
        checkAchievements(newWorkoutsCount, profile.streakDays)

        // Update AI Analytics
        val analytics = dao.getAIAnalytics().firstOrNull() ?: AIAnalyticsEntity()
        val completed = analytics.workoutsCompleted + 1
        val totalGen = maxOf(analytics.totalWorkoutsGenerated, completed)
        val rate = if (totalGen > 0) ((completed.toFloat() / totalGen) * 100).toInt() else 100
        dao.updateAIAnalytics(
            analytics.copy(
                workoutsCompleted = completed,
                completionRatePercentage = rate
            )
        )
    }

    suspend fun syncAllDataToCloud(): String {
        val profile = dao.getUserProfile().firstOrNull() ?: return "Nenhum perfil local para sincronizar."
        val userRes = firestoreService.syncUserProfile(profile)
        val progRes = firestoreService.syncProgress(profile)
        val workouts = dao.getAllWorkouts().firstOrNull().orEmpty()
        for (w in workouts.take(10)) {
            firestoreService.syncWorkout(profile.id, w)
        }
        return if (userRes.isSuccess && progRes.isSuccess) {
            "Sincronizado com sucesso no Firebase Firestore!"
        } else {
            "Dados salvos localmente. Sincronização em nuvem ativa em segundo plano."
        }
    }

    private suspend fun checkAchievements(workoutsCount: Int, streakDays: Int) {
        val achievements = dao.getAllAchievements().firstOrNull().orEmpty()
        for (ach in achievements) {
            if (!ach.isUnlocked) {
                var unlock = false
                if (ach.code == "FIRST_WORKOUT" && workoutsCount >= 1) unlock = true
                if (ach.code == "STREAK_3" && streakDays >= 3) unlock = true
                if (ach.code == "STREAK_7" && streakDays >= 7) unlock = true

                if (unlock) {
                    dao.updateAchievement(
                        ach.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis())
                    )
                }
            }
        }
    }

    suspend fun toggleHabit(habit: HabitEntity) {
        val newStatus = !habit.isCompletedToday
        val updatedHabit = habit.copy(
            isCompletedToday = newStatus,
            streak = if (newStatus) habit.streak + 1 else maxOf(0, habit.streak - 1)
        )
        dao.updateHabit(updatedHabit)

        if (newStatus) {
            val profile = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
            dao.insertOrUpdateProfile(profile.copy(xp = profile.xp + habit.xpReward))
        }
    }

    suspend fun advanceChallengeDay(challenge: ChallengeEntity) {
        val nextDay = challenge.currentDay + 1
        val isDone = nextDay >= challenge.durationDays
        val updated = challenge.copy(
            currentDay = nextDay,
            isCompleted = isDone,
            isActive = !isDone
        )
        dao.updateChallenge(updated)

        val profile = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        val xpBonus = if (isDone) challenge.xpReward else 20
        dao.insertOrUpdateProfile(profile.copy(xp = profile.xp + xpBonus))

        if (isDone) {
            val achievements = dao.getAllAchievements().firstOrNull().orEmpty()
            val chalAch = achievements.find { it.code == "CHALLENGE_CHAMP" }
            if (chalAch != null && !chalAch.isUnlocked) {
                dao.updateAchievement(chalAch.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
            }
        }
    }

    suspend fun joinChallenge(challenge: ChallengeEntity) {
        dao.updateChallenge(challenge.copy(isActive = true, currentDay = 1))
    }

    suspend fun sendChatMessage(
        isFromUser: Boolean,
        message: String,
        intent: String? = null,
        workoutJson: String? = null
    ): Long {
        val id = dao.insertChatMessage(
            ChatMessageEntity(
                isFromUser = isFromUser,
                message = message,
                detectedIntent = intent,
                relatedWorkoutJson = workoutJson
            )
        )
        if (isFromUser) {
            val analytics = dao.getAIAnalytics().firstOrNull() ?: AIAnalyticsEntity()
            dao.updateAIAnalytics(
                analytics.copy(
                    totalQueries = analytics.totalQueries + 1,
                    topIntent = intent ?: analytics.topIntent
                )
            )
        }
        return id
    }

    suspend fun clearChat() {
        dao.clearChatHistory()
    }

    suspend fun toggleLikePost(post: CommunityPostEntity) {
        val newLike = !post.isLikedByMe
        val newCount = if (newLike) post.likesCount + 1 else maxOf(0, post.likesCount - 1)
        dao.updateCommunityPost(post.copy(isLikedByMe = newLike, likesCount = newCount))
    }

    suspend fun addCommunityPost(content: String, workoutTag: String?) {
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        val post = CommunityPostEntity(
            authorName = profile.name,
            authorBadge = "Nível ${profile.level} • ${profile.fitnessLevel}",
            timeAgo = "agora",
            content = content,
            workoutTag = workoutTag,
            likesCount = 0,
            isLikedByMe = false,
            commentsCount = 0
        )
        dao.insertCommunityPost(post)
    }

    suspend fun addComment(postId: Long, content: String) {
        val profile = dao.getUserProfile().firstOrNull() ?: UserProfileEntity()
        dao.insertComment(
            CommunityCommentEntity(
                postId = postId,
                authorName = profile.name,
                content = content
            )
        )
    }

    fun getCommentsForPost(postId: Long): Flow<List<CommunityCommentEntity>> {
        return dao.getCommentsForPost(postId)
    }
}
