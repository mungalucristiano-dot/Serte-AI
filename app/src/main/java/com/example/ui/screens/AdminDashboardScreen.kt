package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.ExerciseEntity
import com.example.ui.SerteViewModel
import com.example.ui.theme.SerteGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: SerteViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val analytics by viewModel.aiAnalytics.collectAsState()
    val exercises by viewModel.allExercises.collectAsState()
    val workouts by viewModel.allWorkouts.collectAsState()

    var showSuccessSnackbar by remember { mutableStateOf(false) }

    // Add exercise form fields
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Corpo Inteiro") }
    var difficulty by remember { mutableStateOf("Iniciante") }
    var equipment by remember { mutableStateOf("Sem equipamento") }
    var targetMuscles by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var safetyTips by remember { mutableStateOf("") }

    val categories = listOf("Corpo Inteiro", "Pernas", "Braços", "Peito", "Costas", "Core", "Mobilidade", "Cardio")
    val difficulties = listOf("Iniciante", "Intermediário", "Avançado")
    val equipments = listOf("Sem equipamento", "Halteres", "Elástico", "Tapete", "Academia")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Painel Administrativo Serte AI", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = {
            if (showSuccessSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = { showSuccessSnackbar = false }) {
                            Text("OK", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Exercício adicionado com sucesso ao banco!")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Live AI System Analytics
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Métricas do Motor de IA & Engajamento",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AdminMetricTile(
                                label = "Perguntas IA",
                                value = "${analytics?.totalQueries ?: 18}",
                                color = Color(0xFF00E5FF),
                                modifier = Modifier.weight(1f)
                            )
                            AdminMetricTile(
                                label = "Treinos Criados",
                                value = "${workouts.size}",
                                color = SerteGreenPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            AdminMetricTile(
                                label = "Conclusão",
                                value = "${analytics?.completionRatePercentage ?: 78}%",
                                color = Color(0xFFFFB300),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Intenção Mais Detectada:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = analytics?.topIntent ?: "TREINO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total de Exercícios no Catálogo:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${exercises.size} ativos",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Add Exercise Form
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Cadastrar Novo Exercício",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Expanda a base de dados com variações e novas instruções técnicas.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nome do Exercício") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_exercise_name"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Dropdown / Selector
                        Text("Categoria", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        ScrollableRowOptions(options = categories, selected = category, onSelect = { category = it })

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Dificuldade", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        ScrollableRowOptions(options = difficulties, selected = difficulty, onSelect = { difficulty = it })

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Equipamento", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        ScrollableRowOptions(options = equipments, selected = equipment, onSelect = { equipment = it })

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = targetMuscles,
                            onValueChange = { targetMuscles = it },
                            label = { Text("Músculos Envolvidos (ex: Peito, Tríceps)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = instructions,
                            onValueChange = { instructions = it },
                            label = { Text("Instruções Passo a Passo") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = safetyTips,
                            onValueChange = { safetyTips = it },
                            label = { Text("Cuidados de Segurança & Alertas") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val newEx = ExerciseEntity(
                                        name = name.trim(),
                                        category = category,
                                        description = "Exercício cadastrado via painel administrativo.",
                                        instructions = instructions.ifBlank { "Execute com movimento controlado." },
                                        difficulty = difficulty,
                                        equipment = equipment,
                                        defaultDurationSeconds = 45,
                                        defaultReps = "10 a 12 reps",
                                        targetMuscles = targetMuscles.ifBlank { "Membros principais" },
                                        variations = "Variações padrão",
                                        alternatives = "Variação adaptada",
                                        commonMistakes = "Perda de alinhamento postural",
                                        safetyTips = safetyTips.ifBlank { "Respeite a amplitude confortável." }
                                    )
                                    viewModel.adminAddExercise(newEx) {
                                        name = ""
                                        targetMuscles = ""
                                        instructions = ""
                                        safetyTips = ""
                                        showSuccessSnackbar = true
                                    }
                                }
                            },
                            enabled = name.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("admin_save_exercise_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = "Salvar Exercício no Catálogo",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricTile(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ScrollableRowOptions(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.take(4).forEach { opt ->
            FilterChip(
                selected = selected == opt,
                onClick = { onSelect(opt) },
                label = { Text(opt, fontSize = 11.sp) }
            )
        }
    }
}
