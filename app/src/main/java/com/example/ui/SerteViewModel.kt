package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.*
import com.example.data.auth.GoogleAuthManager
import com.example.data.auth.GoogleUserData
import com.example.data.local.DatabaseInitializer
import com.example.data.local.SerteDatabase
import com.example.data.model.*
import com.example.data.repository.SerteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SerteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SerteRepository
    val userProfile: StateFlow<UserProfileEntity?>
    val allExercises: StateFlow<List<ExerciseEntity>>
    val allWorkouts: StateFlow<List<WorkoutEntity>>
    val allChallenges: StateFlow<List<ChallengeEntity>>
    val allHabits: StateFlow<List<HabitEntity>>
    val allAchievements: StateFlow<List<AchievementEntity>>
    val chatMessages: StateFlow<List<ChatMessageEntity>>
    val communityPosts: StateFlow<List<CommunityPostEntity>>
    val aiAnalytics: StateFlow<AIAnalyticsEntity?>
    val allArticles: StateFlow<List<EducationalArticleEntity>>

    // Educational Articles filtering
    val selectedArticleCategory = MutableStateFlow("Todos")
    val articleSearchQuery = MutableStateFlow("")
    val filteredArticles: StateFlow<List<EducationalArticleEntity>>

    // Exercises filtering state
    val selectedCategory = MutableStateFlow("Todos")
    val selectedDifficulty = MutableStateFlow("Todos")
    val selectedEquipment = MutableStateFlow("Todos")
    val searchQuery = MutableStateFlow("")

    val filteredExercises: StateFlow<List<ExerciseEntity>>

    // Interactive Workout Player State
    val activeWorkout = MutableStateFlow<WorkoutEntity?>(null)
    val isWorkoutRunning = MutableStateFlow(false)
    val showFeedbackDialog = MutableStateFlow(false)
    val completedWorkoutForFeedback = MutableStateFlow<WorkoutEntity?>(null)
    val lastFeedbackGiven = MutableStateFlow<String?>(null)

    // Chat processing state
    val isAiThinking = MutableStateFlow(false)
    val cloudSyncStatus = MutableStateFlow<String?>(null)
    val isSyncingCloud = MutableStateFlow(false)

    // Google Auth Manager
    val authManager = GoogleAuthManager(application)
    val googleUser: StateFlow<GoogleUserData?> = authManager.currentUser

    init {
        val database = SerteDatabase.getInstance(application)
        val dao = database.serteDao()
        repository = SerteRepository(dao)

        viewModelScope.launch {
            DatabaseInitializer.seedDatabaseIfEmpty(dao)
        }

        userProfile = repository.userProfile
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        allExercises = repository.allExercises
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allWorkouts = repository.allWorkouts
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allChallenges = repository.allChallenges
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allHabits = repository.allHabits
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allAchievements = repository.allAchievements
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        chatMessages = repository.allChatMessages
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        communityPosts = repository.communityPosts
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        aiAnalytics = repository.aiAnalytics
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        allArticles = repository.allArticles
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        filteredArticles = combine(
            selectedArticleCategory,
            articleSearchQuery
        ) { cat, q ->
            repository.filterArticles(cat, q)
        }.flatMapLatest { it }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        filteredExercises = combine(
            selectedCategory,
            selectedDifficulty,
            selectedEquipment,
            searchQuery
        ) { cat, diff, eq, q ->
            repository.filterExercises(cat, diff, eq, q)
        }.flatMapLatest { it }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun completeOnboarding(
        name: String,
        ageRange: String,
        level: String,
        goal: String,
        dailyMinutes: Int,
        location: String,
        equipment: String,
        weeklyDays: Int
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val updated = current.copy(
                name = name.ifBlank { "Atleta Serte" },
                ageRange = ageRange,
                fitnessLevel = level,
                primaryGoal = goal,
                dailyMinutes = dailyMinutes,
                location = location,
                equipment = equipment,
                weeklyDays = weeklyDays,
                isOnboarded = true
            )
            repository.saveUserProfile(updated)
        }
    }

    fun generateAndSaveCustomWorkout(
        goal: String,
        level: String,
        minutes: Int,
        location: String,
        equipment: String,
        onGenerated: (WorkoutEntity) -> Unit = {}
    ) {
        viewModelScope.launch {
            val workout = WorkoutGeneratorEngine.generateWorkout(
                goal = goal,
                level = level,
                timeMinutes = minutes,
                location = location,
                equipment = equipment,
                profile = userProfile.value,
                availableExercises = allExercises.value
            )
            val id = repository.saveWorkout(workout)
            val savedWorkout = workout.copy(id = id)
            onGenerated(savedWorkout)
        }
    }

    fun startWorkoutSession(workout: WorkoutEntity) {
        activeWorkout.value = workout
        isWorkoutRunning.value = true
    }

    fun closeWorkoutSession() {
        activeWorkout.value = null
        isWorkoutRunning.value = false
    }

    fun finishWorkoutSession(workout: WorkoutEntity) {
        isWorkoutRunning.value = false
        activeWorkout.value = null
        completedWorkoutForFeedback.value = workout
        showFeedbackDialog.value = true
    }

    fun submitWorkoutFeedback(feedback: String) {
        val workout = completedWorkoutForFeedback.value ?: return
        viewModelScope.launch {
            repository.completeWorkoutWithFeedback(workout.id, feedback)
            lastFeedbackGiven.value = feedback
            showFeedbackDialog.value = false
            completedWorkoutForFeedback.value = null
        }
    }

    fun dismissFeedbackDialog() {
        showFeedbackDialog.value = false
        completedWorkoutForFeedback.value = null
    }

    fun sendUserChatMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            // Save user message
            repository.sendChatMessage(isFromUser = true, message = text)

            isAiThinking.value = true
            val profile = userProfile.value
            val exercises = allExercises.value

            // Process with Serte AI Assistant (Hybrid Engine + Gemini API call)
            val response = SerteAiAssistant.processMessageWithApi(text, profile, exercises)

            // If a workout was suggested, save it to DB so user can start it
            var workoutJson: String? = null
            if (response.suggestedWorkout != null) {
                val savedId = repository.saveWorkout(response.suggestedWorkout)
                workoutJson = "${response.suggestedWorkout.title}##${savedId}##${response.suggestedWorkout.durationMinutes}"
            }

            // Save AI reply
            repository.sendChatMessage(
                isFromUser = false,
                message = response.replyText,
                intent = response.detectedIntent,
                workoutJson = workoutJson
            )
            isAiThinking.value = false
        }
    }

    fun toggleHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.toggleHabit(habit)
        }
    }

    fun advanceChallenge(challenge: ChallengeEntity) {
        viewModelScope.launch {
            repository.advanceChallengeDay(challenge)
        }
    }

    fun joinChallenge(challenge: ChallengeEntity) {
        viewModelScope.launch {
            repository.joinChallenge(challenge)
        }
    }

    fun togglePostLike(post: CommunityPostEntity) {
        viewModelScope.launch {
            repository.toggleLikePost(post)
        }
    }

    fun addPost(content: String, tag: String?) {
        viewModelScope.launch {
            repository.addCommunityPost(content, tag)
        }
    }

    fun addComment(postId: Long, text: String) {
        viewModelScope.launch {
            repository.addComment(postId, text)
        }
    }

    fun getCommentsForPost(postId: Long): Flow<List<CommunityCommentEntity>> {
        return repository.getCommentsForPost(postId)
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun adminAddExercise(exercise: ExerciseEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.addExercise(exercise)
            onComplete()
        }
    }

    fun toggleBookmarkArticle(article: EducationalArticleEntity) {
        viewModelScope.launch {
            repository.toggleBookmarkArticle(article)
        }
    }

    fun syncToCloud() {
        viewModelScope.launch {
            isSyncingCloud.value = true
            val msg = repository.syncAllDataToCloud()
            cloudSyncStatus.value = msg
            isSyncingCloud.value = false
        }
    }

    fun signInWithGoogle(email: String, name: String) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogleCredential(fallbackEmail = email, fallbackName = name)
            if (result.isSuccess) {
                val current = userProfile.value ?: UserProfileEntity()
                val updated = current.copy(name = name.ifBlank { current.name })
                repository.saveUserProfile(updated)
            }
        }
    }

    fun signOutGoogle() {
        authManager.signOut()
    }
}
