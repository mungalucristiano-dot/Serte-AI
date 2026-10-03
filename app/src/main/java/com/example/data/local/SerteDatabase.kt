package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserProfileEntity::class,
        ExerciseEntity::class,
        WorkoutEntity::class,
        ChallengeEntity::class,
        HabitEntity::class,
        AchievementEntity::class,
        ChatMessageEntity::class,
        CommunityPostEntity::class,
        CommunityCommentEntity::class,
        AIAnalyticsEntity::class,
        EducationalArticleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SerteDatabase : RoomDatabase() {
    abstract fun serteDao(): SerteDao

    companion object {
        @Volatile
        private var INSTANCE: SerteDatabase? = null

        fun getInstance(context: Context): SerteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SerteDatabase::class.java,
                    "serte_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
