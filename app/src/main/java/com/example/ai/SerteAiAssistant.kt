package com.example.ai

import com.example.data.model.ExerciseEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WorkoutEntity

data class SerteAiResponse(
    val replyText: String,
    val detectedIntent: String,
    val suggestedWorkout: WorkoutEntity? = null,
    val isSafetyAlert: Boolean = false,
    val quickFollowUps: List<String> = emptyList()
)

object SerteAiAssistant {

    suspend fun processMessageWithApi(
        userMessage: String,
        profile: UserProfileEntity?,
        exercises: List<ExerciseEntity>
    ): SerteAiResponse {
        val fallback = processMessage(userMessage, profile, exercises)

        if (fallback.isSafetyAlert) return fallback

        val systemPrompt = "Você é a Serte AI Assistant, o assistente pessoal de fitness, movimento e bem-estar da SERTE AI. " +
                "Slogan: 'Seu movimento. Sua evolução. Sua IA.' " +
                "Atleta atual: ${profile?.name ?: "Atleta"}, nível: ${profile?.fitnessLevel ?: "Iniciante"}, objetivo: ${profile?.primaryGoal ?: "Condicionamento"}, tempo disponível: ${profile?.dailyMinutes ?: 20} minutos. " +
                "Diretrizes: Tom acolhedor, profissional e direto ao ponto. Sempre enfatize a consistência diária e a respiração correta. " +
                "Não prescreva tratamentos médicos nem dietas extremas."

        val apiText = com.example.data.api.GeminiApiClient.callGemini(userMessage, systemPrompt)
        return if (!apiText.isNullOrBlank()) {
            fallback.copy(replyText = apiText)
        } else {
            fallback
        }
    }

    fun processMessage(
        userMessage: String,
        profile: UserProfileEntity?,
        exercises: List<ExerciseEntity>
    ): SerteAiResponse {
        val intentResult = IntentClassifier.classify(userMessage)

        // 1. Safety check
        if (intentResult.safetyWarningNeeded || intentResult.primaryIntent == SerteIntent.SEGURANCA) {
            return SerteAiResponse(
                replyText = "⚠️ **Aviso de Segurança & Saúde Serte AI**\n\n" +
                        "A Serte AI é uma plataforma de movimento e bem-estar, **não realizando diagnósticos médicos nem substituindo médicos, fisioterapeutas ou outros profissionais de saúde**.\n\n" +
                        "Se você está sentindo dor aguda, pontadas articulares, suspeita de lesão ou desconforto fora do padrão de cansaço muscular, recomendamos que **interrompa o exercício imediatamente e consulte um profissional de saúde credenciado**.\n\n" +
                        "Sua segurança e longevidade física estão sempre em primeiro lugar!",
                detectedIntent = "SEGURANCA",
                isSafetyAlert = true,
                quickFollowUps = listOf("Exercícios suaves de mobilidade", "Como descansar adequadamente", "Dicas de respiração")
            )
        }

        val userName = profile?.name ?: "Atleta"
        val userLevel = profile?.fitnessLevel ?: "Iniciante"
        val userGoal = profile?.primaryGoal ?: "Condicionamento"
        val easy = profile?.feedbackEasyCount ?: 0
        val hard = profile?.feedbackHardCount ?: 0

        val adaptiveContext = when {
            hard > easy -> "Notei que suas últimas sessões foram exigentes, então preparei sugestões com cadência mais suave e maior tempo de recuperação."
            easy > hard + 1 -> "Considerando que você avaliou seus últimos treinos como fáceis, podemos aumentar ligeiramente a intensidade e o número de repetições!"
            else -> "Mantendo o equilíbrio ideal para evolução contínua e sem sobrecarga."
        }

        return when (intentResult.primaryIntent) {
            SerteIntent.INICIANTE -> {
                val workout = WorkoutGeneratorEngine.generateWorkout(
                    goal = "Iniciação",
                    level = "Iniciante",
                    timeMinutes = intentResult.targetTimeMinutes ?: 15,
                    location = "Casa",
                    equipment = "Sem equipamento",
                    profile = profile,
                    availableExercises = exercises
                )
                SerteAiResponse(
                    replyText = "Excelente decisão, $userName! Começar a treinar não precisa ser intimidador nem exaustivo. O segredo no início é criar a **conexão mente-músculo** e a consistência do hábito.\n\n" +
                            "💡 **Regras de Ouro para Iniciantes:**\n" +
                            "• Movimento antes de intensidade: foque na técnica correta.\n" +
                            "• Respire ritmadamente (nunca prenda o ar).\n" +
                            "• $adaptiveContext\n\n" +
                            "Montei uma sessão perfeita de iniciação de ${workout.durationMinutes} minutos para você fazer agora mesmo sem precisar de nenhum equipamento!",
                    detectedIntent = "INICIANTE",
                    suggestedWorkout = workout,
                    quickFollowUps = listOf("Como fazer agachamento", "Treino de 10 minutos", "Exercícios de mobilidade")
                )
            }

            SerteIntent.SEM_EQUIPAMENTO, SerteIntent.EXERCICIO_CASA -> {
                val workout = WorkoutGeneratorEngine.generateWorkout(
                    goal = userGoal,
                    level = userLevel,
                    timeMinutes = intentResult.targetTimeMinutes ?: 20,
                    location = "Casa",
                    equipment = "Sem equipamento",
                    profile = profile,
                    availableExercises = exercises
                )
                SerteAiResponse(
                    replyText = "Você não precisa de academia nem equipamentos para construir um corpo forte e saudável! O peso corporal (calistenia funcional) oferece estímulos incríveis.\n\n" +
                            "🏋️ **Pilares do Treino em Casa:**\n" +
                            "1. **Padrão de Agachar:** Agachamento livre e afundo.\n" +
                            "2. **Padrão de Empurrar:** Flexões (com joelhos se necessário) ou tríceps na cadeira.\n" +
                            "3. **Estabilidade de Core:** Prancha isométrica e ponte de glúteos.\n\n" +
                            "Gerei um treino estruturado de ${workout.durationMinutes} minutos usando apenas o solo da sua casa!",
                    detectedIntent = "SEM_EQUIPAMENTO",
                    suggestedWorkout = workout,
                    quickFollowUps = listOf("Tenho apenas 10 minutos", "Treino de mobilidade", "Exercícios para pernas")
                )
            }

            SerteIntent.TEMPO_CURTO -> {
                val minutes = intentResult.targetTimeMinutes ?: 15
                val workout = WorkoutGeneratorEngine.generateWorkout(
                    goal = "Eficiência",
                    level = userLevel,
                    timeMinutes = minutes,
                    location = "Casa",
                    equipment = "Sem equipamento",
                    profile = profile,
                    availableExercises = exercises
                )
                SerteAiResponse(
                    replyText = "Ter pouco tempo não é impedimento — é uma oportunidade para um treino objetivo e focado! 15 minutos de movimento consciente ativam a circulação, liberam endorfina e mantêm sua sequência ativa.\n\n" +
                            "⏱️ **Estrutura Express Serte AI:**\n" +
                            "• 2 min de aquecimento articular\n" +
                            "• Bloco contínuo com transições rápidas\n" +
                            "• 1 min de respiração final\n\n" +
                            "Sua sessão rápida de $minutes minutos está pronta para iniciar:",
                    detectedIntent = "TEMPO_CURTO",
                    suggestedWorkout = workout,
                    quickFollowUps = listOf("Iniciar este treino", "Como melhorar fôlego", "Alongamento rápido")
                )
            }

            SerteIntent.MOTIVACAO -> {
                SerteAiResponse(
                    replyText = "É completamente normal ter dias em que a motivação não aparece, $userName. A verdade é que a **motivação é passageira, mas o hábito é construído naqueles dias em que você faz apenas o mínimo**.\n\n" +
                            "✨ **Dica Serte AI para hoje:**\n" +
                            "Faça o compromisso de se movimentar por **apenas 5 minutos**. Se depois de 5 minutos ainda quiser parar, você é livre para parar — mas o corpo e a mente já estarão aquecidos!\n\n" +
                            "Que tal uma sessão ultra leve de mobilidade ou uma caminhada suave hoje?",
                    detectedIntent = "MOTIVACAO",
                    quickFollowUps = listOf("Treino de 5 minutos", "Alongamento relaxante", "Ver desafios ativos")
                )
            }

            SerteIntent.TECNICA_EXECUCAO -> {
                val squat = exercises.find { it.name.contains("Agachamento", ignoreCase = true) }
                val instructions = squat?.instructions ?: "1. Pés na largura dos ombros.\n2. Projete o quadril para trás.\n3. Joelhos alinhados com a ponta dos pés."
                val mistakes = squat?.commonMistakes ?: "Girar joelhos para dentro ou tirar calcanhares do solo."

                SerteAiResponse(
                    replyText = "🎯 **Técnica Perfeita do Agachamento Livre (Air Squat):**\n\n" +
                            "$instructions\n\n" +
                            "⚠️ **Erros Mais Comuns para Evitar:**\n$mistakes\n\n" +
                            "🛡️ **Dica de Segurança Serte:**\n" +
                            "Pense em 'abrir o chão com os pés' para manter os joelhos estáveis e acionar os glúteos.",
                    detectedIntent = "TECNICA",
                    quickFollowUps = listOf("Como fazer flexão correta", "Como fazer prancha", "Ver catálogo de exercícios")
                )
            }

            SerteIntent.MOBILIDADE -> {
                val workout = WorkoutGeneratorEngine.generateWorkout(
                    goal = "Mobilidade",
                    level = "Iniciante",
                    timeMinutes = intentResult.targetTimeMinutes ?: 15,
                    location = "Casa",
                    equipment = "Tapete",
                    profile = profile,
                    availableExercises = exercises
                )
                SerteAiResponse(
                    replyText = "Mobilidade é a chave para o movimento livre de dores e articulações saudáveis! Ela melhora a circulação do líquido sinovial e alivia tensões acumuladas da postura sentada.\n\n" +
                            "🧘 **Foco de Hoje:** Descompressão de coluna torácica, abertura de quadris e flexores.\n\n" +
                            "Estruturei uma rotina de mobilidade articular fluida de ${workout.durationMinutes} minutos:",
                    detectedIntent = "MOBILIDADE",
                    suggestedWorkout = workout,
                    quickFollowUps = listOf("Alongamento para costas", "Mobilidade de quadril", "Exercícios de respiração")
                )
            }

            SerteIntent.RESISTENCIA -> {
                val workout = WorkoutGeneratorEngine.generateWorkout(
                    goal = "Resistência",
                    level = userLevel,
                    timeMinutes = intentResult.targetTimeMinutes ?: 20,
                    location = profile?.location ?: "Casa",
                    equipment = profile?.equipment ?: "Sem equipamento",
                    profile = profile,
                    availableExercises = exercises
                )
                SerteAiResponse(
                    replyText = "Para evoluir a resistência cardiovascular e muscular, alternamos intervalos de esforço controlado com pausas curtas ativas.\n\n" +
                            "🔥 **Benefícios:** Aumento do VO2 estimado, eficiência mitocondrial e disposição para o dia todo.\n\n" +
                            "Aqui está seu treino de resistência cardiovascular de ${workout.durationMinutes} minutos:",
                    detectedIntent = "RESISTENCIA",
                    suggestedWorkout = workout,
                    quickFollowUps = listOf("Treino de cardio em casa", "Como controlar a respiração", "Sessão de pernas")
                )
            }

            SerteIntent.FORCA -> {
                val workout = WorkoutGeneratorEngine.generateWorkout(
                    goal = "Força",
                    level = userLevel,
                    timeMinutes = intentResult.targetTimeMinutes ?: 25,
                    location = profile?.location ?: "Casa",
                    equipment = profile?.equipment ?: "Sem equipamento",
                    profile = profile,
                    availableExercises = exercises
                )
                SerteAiResponse(
                    replyText = "Treinar força fortalece a densidade óssea, melhora a sensibilidade à insulina e protege suas articulações contra desgastes futuros.\n\n" +
                            "💪 **Diretriz Técnica:** Controle a fase excêntrica (descida) em 2 a 3 segundos para maximizar o tempo sob tensão.\n\n" +
                            "Sua sessão de força de ${workout.durationMinutes} minutos está calculada:",
                    detectedIntent = "FORCA",
                    suggestedWorkout = workout,
                    quickFollowUps = listOf("Técnica de flexão", "Treino de costas", "Treino de braços")
                )
            }

            SerteIntent.RECUPERACAO -> {
                SerteAiResponse(
                    replyText = "A recuperação é 50% dos seus resultados! O corpo não evolui durante o treino, mas sim durante a recuperação dos estímulos.\n\n" +
                            "💤 **Os 3 Pilares da Recuperação Serte:**\n" +
                            "1. **Sono:** 7 a 8 horas de sono de qualidade para liberação de hormônios regenerativos.\n" +
                            "2. **Hidratação:** 35ml de água por kg de peso corporal ao longo do dia.\n" +
                            "3. **Recuperação Ativa:** Caminhadas leves e mobilidade suave diminuem a rigidez muscular muito mais rápido do que ficar 100% parado.",
                    detectedIntent = "RECUPERACAO",
                    quickFollowUps = listOf("Sessão de relaxamento", "Alongar antes de dormir", "Dicas de hidratação")
                )
            }

            SerteIntent.GERAR_TREINO, SerteIntent.GERAL -> {
                val workout = WorkoutGeneratorEngine.generateWorkout(
                    goal = userGoal,
                    level = userLevel,
                    timeMinutes = intentResult.targetTimeMinutes ?: profile?.dailyMinutes ?: 20,
                    location = profile?.location ?: "Casa",
                    equipment = intentResult.detectedEquipment ?: profile?.equipment ?: "Sem equipamento",
                    profile = profile,
                    availableExercises = exercises
                )
                SerteAiResponse(
                    replyText = "Compreendido, $userName! Analisei seu perfil ($userLevel • $userGoal) e seu histórico de treinos.\n\n" +
                            "$adaptiveContext\n\n" +
                            "Aqui está uma sessão completa adaptada aos seus objetivos de movimento:",
                    detectedIntent = "TREINO",
                    suggestedWorkout = workout,
                    quickFollowUps = listOf("Mudar tempo para 15m", "Fazer sem equipamento", "Exercícios de mobilidade")
                )
            }

            else -> {
                SerteAiResponse(
                    replyText = "Estou aqui para ajudar com seu movimento, treinos e saúde preventiva, $userName! Você pode me pedir para criar um treino, tirar dúvidas sobre execução de exercícios ou pedir recomendações.",
                    detectedIntent = "GERAL",
                    quickFollowUps = listOf("Criar treino de 15m", "Exercícios para iniciantes", "Dicas de mobilidade")
                )
            }
        }
    }
}
