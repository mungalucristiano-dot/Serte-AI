package com.example.ai

import com.example.data.model.UserProfileEntity

data class DailyRecommendation(
    val title: String,
    val headline: String,
    val explanation: String,
    val suggestedActionText: String,
    val adaptiveBadge: String,
    val educationTip: String
)

object RecommendationEngine {

    fun generateDailyRecommendation(profile: UserProfileEntity?): DailyRecommendation {
        val easy = profile?.feedbackEasyCount ?: 0
        val mod = profile?.feedbackModerateCount ?: 0
        val hard = profile?.feedbackHardCount ?: 0
        val goal = profile?.primaryGoal ?: "Condicionamento"
        val time = profile?.dailyMinutes ?: 20

        return when {
            hard > easy -> {
                DailyRecommendation(
                    title = "Ajuste Inteligente: Recuperação Ativa",
                    headline = "Hoje vamos focar em mobilidade e menor impacto",
                    explanation = "Notamos pelo seu feedback anterior que as últimas sessões foram exigentes. A Serte AI reduziu o volume em 20% e aumentou os tempos de descanso para favorecer sua recuperação sem perder o hábito.",
                    suggestedActionText = "Iniciar Sessão de Mobilidade (15 min)",
                    adaptiveBadge = "Modo Regenerativo Ativo",
                    educationTip = "O descanso adequado e a hidratação são os momentos reais em que seus músculos reparam fibras e evoluem. Não sinta culpa em pegar mais leve!"
                )
            }
            easy > hard + 1 -> {
                DailyRecommendation(
                    title = "Evolução Detectada: Estímulo Progressivo",
                    headline = "Seu corpo se adaptou rápido! Hora de avançar",
                    explanation = "Você avaliou suas sessões recentes como 'Fácil'. A Serte AI ajustou a cadência para 45s de estímulo e 15s de pausa, introduzindo variações mais dinâmicas para manter sua evolução contínua.",
                    suggestedActionText = "Treino Progressivo $goal ($time min)",
                    adaptiveBadge = "Progressão de Carga & Ritmo",
                    educationTip = "Sobrecarga progressiva não significa apenas pesos mais pesados: diminuir pausas ou melhorar a amplitude do movimento gera excelentes ganhos."
                )
            }
            else -> {
                DailyRecommendation(
                    title = "Ritmo Ideal Serte: Consistência",
                    headline = "Treino equilibrado sob medida para seu objetivo",
                    explanation = "Seu feedback 'Moderado' indica que você está na zona metabólica ideal: desafiador o bastante para gerar adaptação, seguro o bastante para evitar exaustão.",
                    suggestedActionText = "Treino $goal ($time min)",
                    adaptiveBadge = "Zona de Equilíbrio Perfeito",
                    educationTip = "A consistência diária de 15 a 20 minutos gera muito mais benefícios para o coração e mente do que treinar exaustivamente apenas 1 vez por semana."
                )
            }
        }
    }
}
