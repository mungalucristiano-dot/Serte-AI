package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Atleta Serte",
    val ageRange: String = "25-34",
    val fitnessLevel: String = "Iniciante", // Iniciante, Intermediário, Avançado
    val primaryGoal: String = "Condicionamento", // Resistência, Força, Mobilidade, Condicionamento, Hábito
    val dailyMinutes: Int = 20,
    val location: String = "Casa", // Casa, Academia, Ar Livre
    val equipment: String = "Sem equipamento", // Sem equipamento, Halteres, Elástico, Academia
    val weeklyDays: Int = 4,
    val xp: Int = 120,
    val level: Int = 1,
    val streakDays: Int = 3,
    val lastWorkoutDate: Long = System.currentTimeMillis(),
    val completedWorkoutsCount: Int = 2,
    val feedbackEasyCount: Int = 0,
    val feedbackModerateCount: Int = 2,
    val feedbackHardCount: Int = 0,
    val isOnboarded: Boolean = true
)

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String, // Corpo Inteiro, Pernas, Braços, Peito, Costas, Ombros, Core, Mobilidade, Flexibilidade, Cardio, Equilíbrio, Condicionamento
    val description: String,
    val instructions: String,
    val difficulty: String, // Iniciante, Intermediário, Avançado
    val equipment: String, // Sem equipamento, Halteres, Elástico, Tapete, Banco, Academia
    val defaultDurationSeconds: Int = 45,
    val defaultReps: String = "12 a 15 repetições",
    val targetMuscles: String,
    val variations: String,
    val alternatives: String,
    val commonMistakes: String,
    val safetyTips: String,
    val isFavorite: Boolean = false
)

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val goal: String,
    val level: String,
    val durationMinutes: Int,
    val location: String,
    val equipment: String,
    val exercisesSummary: String, // e.g. "Polichinelos, Agachamento, Flexão..."
    val structuredStagesJson: String, // stages serialized JSON
    val generatedByAi: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val userFeedback: String? = null, // "Fácil", "Moderado", "Difícil"
    val caloriesEstimated: Int = 140
)

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val durationDays: Int, // 7, 14, 21, 30
    val category: String, // Movimento, Mobilidade, Consistência, Sem Equipamento, Caminhada
    val description: String,
    val xpReward: Int,
    val isActive: Boolean = false,
    val currentDay: Int = 0,
    val isCompleted: Boolean = false
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val iconName: String,
    val target: String,
    val isCompletedToday: Boolean = false,
    val streak: Int = 1,
    val xpReward: Int = 5
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val xpValue: Int = 20
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val isFromUser: Boolean,
    val message: String,
    val detectedIntent: String? = null,
    val relatedWorkoutJson: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_posts")
data class CommunityPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorBadge: String,
    val timeAgo: String,
    val content: String,
    val workoutTag: String? = null,
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val commentsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "community_comments")
data class CommunityCommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val authorName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_analytics")
data class AIAnalyticsEntity(
    @PrimaryKey val id: Int = 1,
    val totalQueries: Int = 18,
    val totalWorkoutsGenerated: Int = 9,
    val workoutsCompleted: Int = 7,
    val topIntent: String = "TREINO",
    val completionRatePercentage: Int = 78
)

@Entity(tableName = "educational_articles")
data class EducationalArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Benefícios dos Exercícios, Cardio, Força, Mobilidade, Nutrição, Sono & Recuperação, Consistência
    val contentType: String = "Artigo", // Artigo, Vídeo Guia, Dica Prática
    val summary: String,
    val fullContent: String,
    val readTimeMinutes: Int = 3,
    val videoDuration: String? = null,
    val keyTakeaways: String,
    val tags: String,
    val isBookmarked: Boolean = false
)
