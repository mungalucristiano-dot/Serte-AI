package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.WorkoutEntity
import com.example.ui.SerteViewModel

@Composable
fun WorkoutsScreen(
    viewModel: SerteViewModel,
    onStartWorkout: (WorkoutEntity) -> Unit,
    showGeneratorDialogDirectly: Boolean = false,
    modifier: Modifier = Modifier
) {
    val workouts by viewModel.allWorkouts.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var showGeneratorDialog by remember { mutableStateOf(showGeneratorDialogDirectly) }

    LaunchedEffect(showGeneratorDialogDirectly) {
        if (showGeneratorDialogDirectly) {
            showGeneratorDialog = true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Header & AI Generator CTA
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GERADOR INTELIGENTE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Crie uma sessão adaptada para seu momento",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "Defina seu tempo, equipamento e objetivo. A Serte AI calcula as combinações e ajusta o esforço pelo seu feedback anterior.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    Button(
                        onClick = { showGeneratorDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("create_workout_generator_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Criar meu treino agora",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // Section Title: Treinos & Sessões
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Suas Sessões de Treino",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${workouts.size} geradas",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Workouts List
        if (workouts.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nenhum treino gerado ainda",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Toque em 'Criar meu treino' acima para gerar sua primeira sessão inteligente.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(workouts) { workout ->
                WorkoutCardItem(
                    workout = workout,
                    onStart = { onStartWorkout(workout) }
                )
            }
        }
    }

    // Generator Modal Dialog
    if (showGeneratorDialog) {
        WorkoutGeneratorDialog(
            defaultGoal = profile?.primaryGoal ?: "Condicionamento",
            defaultLevel = profile?.fitnessLevel ?: "Iniciante",
            defaultTime = profile?.dailyMinutes ?: 20,
            defaultLocation = profile?.location ?: "Casa",
            defaultEquipment = profile?.equipment ?: "Sem equipamento",
            onDismiss = { showGeneratorDialog = false },
            onGenerate = { goal, level, time, location, equipment ->
                showGeneratorDialog = false
                viewModel.generateAndSaveCustomWorkout(
                    goal = goal,
                    level = level,
                    minutes = time,
                    location = location,
                    equipment = equipment,
                    onGenerated = { newWorkout ->
                        onStartWorkout(newWorkout)
                    }
                )
            }
        )
    }
}

@Composable
fun WorkoutCardItem(
    workout: WorkoutEntity,
    onStart: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("workout_item_${workout.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workout.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        BadgeChip(text = "${workout.durationMinutes} min", icon = Icons.Default.Timer)
                        BadgeChip(text = "${workout.caloriesEstimated} kcal", icon = Icons.Default.LocalFireDepartment)
                        BadgeChip(text = workout.equipment, icon = Icons.Default.FitnessCenter)
                    }
                }

                // Completion or Feedback badge
                if (workout.isCompleted) {
                    val fb = workout.userFeedback
                    val fbColor = when (fb) {
                        "Fácil" -> Color(0xFF00E676)
                        "Difícil" -> Color(0xFFFF7043)
                        else -> Color(0xFF29B6F6)
                    }
                    Surface(
                        color = fbColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (fb != null) "Avaliado: $fb" else "Concluído",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = fbColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sequência: ${workout.exercisesSummary}",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onStart,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (workout.isCompleted) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(38.dp)
                ) {
                    Icon(
                        imageVector = if (workout.isCompleted) Icons.Default.Replay else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (workout.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (workout.isCompleted) "Repetir Sessão" else "Iniciar Treino",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (workout.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun WorkoutGeneratorDialog(
    defaultGoal: String,
    defaultLevel: String,
    defaultTime: Int,
    defaultLocation: String,
    defaultEquipment: String,
    onDismiss: () -> Unit,
    onGenerate: (goal: String, level: String, time: Int, location: String, equipment: String) -> Unit
) {
    var selectedGoal by remember { mutableStateOf(defaultGoal) }
    var selectedLevel by remember { mutableStateOf(defaultLevel) }
    var selectedTime by remember { mutableStateOf(defaultTime) }
    var selectedLocation by remember { mutableStateOf(defaultLocation) }
    var selectedEquipment by remember { mutableStateOf(defaultEquipment) }

    val goals = listOf("Condicionamento", "Resistência", "Força", "Mobilidade", "Iniciação")
    val levels = listOf("Iniciante", "Intermediário", "Avançado")
    val times = listOf(5, 10, 15, 20, 30, 45)
    val locations = listOf("Casa", "Academia", "Ar Livre")
    val equipments = listOf("Sem equipamento", "Halteres", "Elástico", "Tapete")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Criar Meu Treino",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Goal Selector
                Text(text = "Objetivo Principal", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)) {
                    items(goals) { g ->
                        FilterChip(
                            selected = selectedGoal == g,
                            onClick = { selectedGoal = g },
                            label = { Text(g, fontSize = 11.sp) }
                        )
                    }
                }

                // Level Selector
                Text(text = "Nível de Experiência", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)) {
                    items(levels) { lvl ->
                        FilterChip(
                            selected = selectedLevel == lvl,
                            onClick = { selectedLevel = lvl },
                            label = { Text(lvl, fontSize = 11.sp) }
                        )
                    }
                }

                // Time Available
                Text(text = "Tempo Disponível (Minutos)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)) {
                    items(times) { t ->
                        FilterChip(
                            selected = selectedTime == t,
                            onClick = { selectedTime = t },
                            label = { Text("${t}m", fontSize = 11.sp) }
                        )
                    }
                }

                // Equipment Available
                Text(text = "Equipamentos Disponíveis", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)) {
                    items(equipments) { eq ->
                        FilterChip(
                            selected = selectedEquipment == eq,
                            onClick = { selectedEquipment = eq },
                            label = { Text(eq, fontSize = 11.sp) }
                        )
                    }
                }

                Button(
                    onClick = {
                        onGenerate(selectedGoal, selectedLevel, selectedTime, selectedLocation, selectedEquipment)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_generate_workout_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gerar Treino Inteligente",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
