package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfileEntity
import com.example.ui.SerteViewModel

@Composable
fun OnboardingScreen(
    currentProfile: UserProfileEntity?,
    onComplete: (name: String, age: String, level: String, goal: String, minutes: Int, loc: String, eq: String, days: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(currentProfile?.name ?: "Atleta Serte") }
    var ageRange by remember { mutableStateOf(currentProfile?.ageRange ?: "25-34") }
    var level by remember { mutableStateOf(currentProfile?.fitnessLevel ?: "Iniciante") }
    var goal by remember { mutableStateOf(currentProfile?.primaryGoal ?: "Condicionamento") }
    var dailyMinutes by remember { mutableStateOf(currentProfile?.dailyMinutes ?: 20) }
    var location by remember { mutableStateOf(currentProfile?.location ?: "Casa") }
    var equipment by remember { mutableStateOf(currentProfile?.equipment ?: "Sem equipamento") }
    var weeklyDays by remember { mutableStateOf(currentProfile?.weeklyDays ?: 4) }

    val ageRanges = listOf("18-24", "25-34", "35-44", "45-54", "55+")
    val levels = listOf("Iniciante", "Intermediário", "Avançado")
    val goals = listOf("Condicionamento", "Resistência", "Força", "Mobilidade", "Hábito Ativo")
    val times = listOf(10, 15, 20, 30, 45)
    val locations = listOf("Casa", "Academia", "Ar Livre", "Híbrido")
    val equipments = listOf("Sem equipamento", "Halteres", "Elástico", "Completo")
    val daysOptions = listOf(2, 3, 4, 5, 6)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Symbol & Title
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Bem-vindo à SERTE AI",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "“Seu movimento. Sua evolução. Sua IA.”",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = "Vamos personalizar seu assistente de acordo com sua rotina e preferências.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Como gostaria de ser chamado(a)?") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_name_input"),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Faixa Etária
            SectionLabel(title = "Faixa Etária")
            ChipSelector(options = ageRanges, selected = ageRange, onSelect = { ageRange = it })

            Spacer(modifier = Modifier.height(14.dp))

            // Nível de Experiência
            SectionLabel(title = "Nível de Experiência Atual")
            ChipSelector(options = levels, selected = level, onSelect = { level = it })

            Spacer(modifier = Modifier.height(14.dp))

            // Objetivo Principal
            SectionLabel(title = "Qual é o Seu Objetivo Principal?")
            ChipSelector(options = goals, selected = goal, onSelect = { goal = it })

            Spacer(modifier = Modifier.height(14.dp))

            // Tempo Diário
            SectionLabel(title = "Quanto tempo você tem disponível por dia?")
            ChipSelector(options = times.map { "${it}m" }, selected = "${dailyMinutes}m", onSelect = {
                dailyMinutes = it.replace("m", "").toIntOrNull() ?: 20
            })

            Spacer(modifier = Modifier.height(14.dp))

            // Local
            SectionLabel(title = "Onde prefere treinar?")
            ChipSelector(options = locations, selected = location, onSelect = { location = it })

            Spacer(modifier = Modifier.height(14.dp))

            // Equipamento
            SectionLabel(title = "Equipamentos que possui em mãos:")
            ChipSelector(options = equipments, selected = equipment, onSelect = { equipment = it })

            Spacer(modifier = Modifier.height(14.dp))

            // Dias por Semana
            SectionLabel(title = "Quantos dias por semana pretende se movimentar?")
            ChipSelector(options = daysOptions.map { "$it dias" }, selected = "$weeklyDays dias", onSelect = {
                weeklyDays = it.split(" ").firstOrNull()?.toIntOrNull() ?: 4
            })

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onComplete(name.trim(), ageRange, level, goal, dailyMinutes, location, equipment, weeklyDays)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("complete_onboarding_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Começar Minha Evolução com a Serte AI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}

@Composable
private fun ChipSelector(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(options) { opt ->
            FilterChip(
                selected = selected == opt,
                onClick = { onSelect(opt) },
                label = { Text(opt, fontSize = 11.sp) }
            )
        }
    }
}
