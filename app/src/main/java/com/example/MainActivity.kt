package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SerteViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.SerteAiTheme

enum class AppSubScreen {
    MAIN,
    CHAT,
    ADMIN,
    ONBOARDING
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SerteAiTheme {
                val viewModel: SerteViewModel = viewModel()
                val profile by viewModel.userProfile.collectAsState()
                val activeWorkout by viewModel.activeWorkout.collectAsState()
                val isWorkoutRunning by viewModel.isWorkoutRunning.collectAsState()
                val showFeedbackDialog by viewModel.showFeedbackDialog.collectAsState()
                val workoutForFeedback by viewModel.completedWorkoutForFeedback.collectAsState()
                val googleUser by viewModel.googleUser.collectAsState()

                var currentTab by remember { mutableStateOf(SerteNavDestination.INICIO) }
                var currentSubScreen by remember { mutableStateOf(AppSubScreen.MAIN) }
                var triggerGeneratorDialog by remember { mutableStateOf(false) }

                // Enforce Google Sign-In whenever entering the app
                if (googleUser == null) {
                    LoginScreen(
                        onSignInWithGoogle = { email, name ->
                            viewModel.signInWithGoogle(email, name)
                        }
                    )
                } else {
                    // Check onboarding status
                    val isProfileOnboarded = profile?.isOnboarded ?: false

                    if (!isProfileOnboarded && profile != null) {
                        OnboardingScreen(
                            currentProfile = profile,
                            onComplete = { name, age, level, goal, minutes, loc, eq, days ->
                                viewModel.completeOnboarding(name, age, level, goal, minutes, loc, eq, days)
                            }
                        )
                    } else {
                    when (currentSubScreen) {
                        AppSubScreen.CHAT -> {
                            BackHandler { currentSubScreen = AppSubScreen.MAIN }
                            SerteChatScreen(
                                viewModel = viewModel,
                                onStartWorkout = { workout ->
                                    currentSubScreen = AppSubScreen.MAIN
                                    viewModel.startWorkoutSession(workout)
                                },
                                onClose = { currentSubScreen = AppSubScreen.MAIN }
                            )
                        }

                        AppSubScreen.ADMIN -> {
                            BackHandler { currentSubScreen = AppSubScreen.MAIN }
                            AdminDashboardScreen(
                                viewModel = viewModel,
                                onBack = { currentSubScreen = AppSubScreen.MAIN }
                            )
                        }

                        AppSubScreen.ONBOARDING -> {
                            BackHandler { currentSubScreen = AppSubScreen.MAIN }
                            OnboardingScreen(
                                currentProfile = profile,
                                onComplete = { name, age, level, goal, minutes, loc, eq, days ->
                                    viewModel.completeOnboarding(name, age, level, goal, minutes, loc, eq, days)
                                    currentSubScreen = AppSubScreen.MAIN
                                }
                            )
                        }

                        AppSubScreen.MAIN -> {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                topBar = {
                                    SerteTopBar(
                                        profile = profile,
                                        onOpenChat = { currentSubScreen = AppSubScreen.CHAT }
                                    )
                                },
                                bottomBar = {
                                    SerteBottomNav(
                                        currentDestination = currentTab,
                                        onNavigate = { tab ->
                                            currentTab = tab
                                        }
                                    )
                                }
                            ) { innerPadding ->
                                when (currentTab) {
                                    SerteNavDestination.INICIO -> {
                                        HomeScreen(
                                            viewModel = viewModel,
                                            onNavigateTab = { tab -> currentTab = tab },
                                            onOpenChat = { currentSubScreen = AppSubScreen.CHAT },
                                            onStartWorkout = { workout -> viewModel.startWorkoutSession(workout) },
                                            onOpenCreateWorkout = {
                                                currentTab = SerteNavDestination.TREINOS
                                                triggerGeneratorDialog = true
                                            },
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }

                                    SerteNavDestination.TREINOS -> {
                                        WorkoutsScreen(
                                            viewModel = viewModel,
                                            onStartWorkout = { workout -> viewModel.startWorkoutSession(workout) },
                                            showGeneratorDialogDirectly = triggerGeneratorDialog,
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                        triggerGeneratorDialog = false
                                    }

                                    SerteNavDestination.EXERCICIOS -> {
                                        ExerciseCatalogScreen(
                                            viewModel = viewModel,
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }

                                    SerteNavDestination.CONHECIMENTO -> {
                                        EducationScreen(
                                            viewModel = viewModel,
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }

                                    SerteNavDestination.COMUNIDADE -> {
                                        CommunityScreen(
                                            viewModel = viewModel,
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }

                                    SerteNavDestination.PERFIL -> {
                                        ProfileScreen(
                                            viewModel = viewModel,
                                            onOpenAdmin = { currentSubScreen = AppSubScreen.ADMIN },
                                            onOpenOnboarding = { currentSubScreen = AppSubScreen.ONBOARDING },
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Workout Player Modal (Overlays when active)
                    if (isWorkoutRunning && activeWorkout != null) {
                        WorkoutPlayerModal(
                            workout = activeWorkout,
                            onFinish = { finishedWorkout ->
                                viewModel.finishWorkoutSession(finishedWorkout)
                            },
                            onClose = { viewModel.closeWorkoutSession() }
                        )
                    }

                    // Adaptive Difficulty Feedback Dialog (Fácil, Moderado, Difícil)
                    if (showFeedbackDialog && workoutForFeedback != null) {
                        FeedbackModal(
                            workout = workoutForFeedback,
                            onFeedbackSubmitted = { feedback ->
                                viewModel.submitWorkoutFeedback(feedback)
                            },
                            onDismiss = { viewModel.dismissFeedbackDialog() }
                        )
                    }
                }
            }
            }
        }
    }
}
