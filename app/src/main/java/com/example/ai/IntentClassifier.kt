package com.example.ai

import java.text.Normalizer
import java.util.Locale

enum class SerteIntent(val label: String, val category: String) {
    GERAR_TREINO("Gerar Treino", "TREINO"),
    EXERCICIO_CASA("Treino em Casa", "TREINO"),
    SEM_EQUIPAMENTO("Sem Equipamento", "EQUIPAMENTO"),
    INICIANTE("Para Iniciantes", "NIVEL"),
    TEMPO_CURTO("Tempo Curto (10-15m)", "TEMPO"),
    MOBILIDADE("Mobilidade & Articulações", "OBJETIVOS"),
    RESISTENCIA("Resistência & Cardio", "OBJETIVOS"),
    FORCA("Força & Hipertrofia", "OBJETIVOS"),
    MOTIVACAO("Motivação & Hábito", "MOTIVACAO"),
    TECNICA_EXECUCAO("Técnica & Execução", "TECNICA"),
    RECUPERACAO("Recuperação & Sono", "RECUPERACAO"),
    SEGURANCA("Aviso de Saúde / Lesão", "SEGURANCA"),
    GERAL("Conversa Geral", "ASSISTENTE")
}

data class IntentResult(
    val primaryIntent: SerteIntent,
    val matchedKeywords: List<String>,
    val targetTimeMinutes: Int?,
    val detectedLevel: String?,
    val detectedEquipment: String?,
    val detectedMuscleOrType: String?,
    val safetyWarningNeeded: Boolean
)

object IntentClassifier {

    private fun normalize(text: String): String {
        val n = Normalizer.normalize(text.lowercase(Locale.ROOT), Normalizer.Form.NFD)
        return n.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    fun classify(userMessage: String): IntentResult {
        val clean = normalize(userMessage)
        val matched = mutableListOf<String>()

        // 1. Safety check
        val safetyKeywords = listOf("dor aguda", "dor forte", "machuquei", "lesao", "coluna travou", "cirurgia", "remedio", "doenca", "pressao alta")
        for (kw in safetyKeywords) {
            if (clean.contains(kw)) {
                matched.add(kw)
                return IntentResult(
                    primaryIntent = SerteIntent.SEGURANCA,
                    matchedKeywords = matched,
                    targetTimeMinutes = null,
                    detectedLevel = null,
                    detectedEquipment = null,
                    detectedMuscleOrType = null,
                    safetyWarningNeeded = true
                )
            }
        }

        // 2. Detect Time
        var timeMinutes: Int? = null
        val timeRegex = Regex("(\\d+)\\s*(min|minuto|m)")
        val matchResult = timeRegex.find(clean)
        if (matchResult != null) {
            timeMinutes = matchResult.groupValues[1].toIntOrNull()
            matched.add("${timeMinutes}m")
        } else if (clean.contains("pouco tempo") || clean.contains("rapido")) {
            timeMinutes = 15
            matched.add("tempo_curto")
        }

        // 3. Detect Level
        var level: String? = null
        if (clean.contains("iniciante") || clean.contains("comecar") || clean.contains("primeira vez") || clean.contains("zero")) {
            level = "Iniciante"
            matched.add("iniciante")
        } else if (clean.contains("avancado") || clean.contains("pesado") || clean.contains("intenso")) {
            level = "Avançado"
            matched.add("avancado")
        } else if (clean.contains("intermediario")) {
            level = "Intermediário"
            matched.add("intermediario")
        }

        // 4. Detect Equipment
        var equipment: String? = null
        if (clean.contains("sem equipamento") || clean.contains("nenhum equipamento") || clean.contains("apenas peso corporal") || clean.contains("corpo livre")) {
            equipment = "Sem equipamento"
            matched.add("sem_equipamento")
        } else if (clean.contains("halter") || clean.contains("pesinho")) {
            equipment = "Halteres"
            matched.add("halteres")
        } else if (clean.contains("elastico") || clean.contains("band")) {
            equipment = "Elástico"
            matched.add("elastico")
        } else if (clean.contains("academia") || clean.contains("ginasio")) {
            equipment = "Academia"
            matched.add("academia")
        }

        // 5. Detect Target Muscle / Type
        var muscleType: String? = null
        val muscleKeywords = mapOf(
            "perna" to "Pernas",
            "gluteo" to "Pernas",
            "braco" to "Braços",
            "biceps" to "Braços",
            "triceps" to "Braços",
            "peito" to "Peito",
            "flexao" to "Peito",
            "costas" to "Costas",
            "ombro" to "Ombros",
            "abd" to "Core",
            "core" to "Core",
            "prancha" to "Core",
            "mobilidade" to "Mobilidade",
            "alongamento" to "Flexibilidade",
            "cardio" to "Cardio"
        )
        for ((k, v) in muscleKeywords) {
            if (clean.contains(k)) {
                muscleType = v
                matched.add(k)
                break
            }
        }

        // 6. Primary Intent Matching
        val primaryIntent = when {
            clean.contains("motivacao") || clean.contains("desanim") || clean.contains("preguica") || clean.contains("cansad") || clean.contains("sem animo") -> {
                SerteIntent.MOTIVACAO
            }
            clean.contains("como fazer") || clean.contains("postura") || clean.contains("execucao") || clean.contains("corretamente") || clean.contains("agachamento") -> {
                SerteIntent.TECNICA_EXECUCAO
            }
            clean.contains("mobilidade") || clean.contains("alongar") || clean.contains("coluna") || clean.contains("rigido") -> {
                SerteIntent.MOBILIDADE
            }
            clean.contains("resistencia") || clean.contains("cardio") || clean.contains("folego") -> {
                SerteIntent.RESISTENCIA
            }
            clean.contains("forca") || clean.contains("musculo") || clean.contains("hipertrofia") -> {
                SerteIntent.FORCA
            }
            clean.contains("recuperacao") || clean.contains("sono") || clean.contains("descanso") || clean.contains("dor muscular tardia") -> {
                SerteIntent.RECUPERACAO
            }
            clean.contains("sem equipamento") -> {
                SerteIntent.SEM_EQUIPAMENTO
            }
            clean.contains("iniciante") || clean.contains("comecar a treinar") -> {
                SerteIntent.INICIANTE
            }
            timeMinutes != null && timeMinutes <= 15 -> {
                SerteIntent.TEMPO_CURTO
            }
            clean.contains("casa") -> {
                SerteIntent.EXERCICIO_CASA
            }
            clean.contains("treino") || clean.contains("exercicio") || clean.contains("sessao") || clean.contains("criar meu treino") -> {
                SerteIntent.GERAR_TREINO
            }
            else -> SerteIntent.GERAL
        }

        return IntentResult(
            primaryIntent = primaryIntent,
            matchedKeywords = matched,
            targetTimeMinutes = timeMinutes,
            detectedLevel = level,
            detectedEquipment = equipment,
            detectedMuscleOrType = muscleType,
            safetyWarningNeeded = false
        )
    }
}
