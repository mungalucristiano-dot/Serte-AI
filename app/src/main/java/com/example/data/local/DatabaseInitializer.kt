package com.example.data.local

import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {

    suspend fun seedDatabaseIfEmpty(dao: SerteDao) = withContext(Dispatchers.IO) {
        val existingExercisesCount = dao.getExercisesCount()
        if (existingExercisesCount > 0) return@withContext

        // Default User
        dao.insertOrUpdateProfile(
            UserProfileEntity(
                id = 1,
                name = "Atleta Serte",
                ageRange = "25-34",
                fitnessLevel = "Iniciante",
                primaryGoal = "Condicionamento",
                dailyMinutes = 20,
                location = "Casa",
                equipment = "Sem equipamento",
                weeklyDays = 4,
                xp = 180,
                level = 1,
                streakDays = 3,
                lastWorkoutDate = System.currentTimeMillis() - 86400000L,
                completedWorkoutsCount = 2,
                feedbackEasyCount = 0,
                feedbackModerateCount = 2,
                feedbackHardCount = 0,
                isOnboarded = true
            )
        )

        // Preload Exercises across multiple categories
        val exercises = listOf(
            ExerciseEntity(
                name = "Agachamento Livre (Air Squat)",
                category = "Pernas",
                description = "Movimento fundamental de padrão motor para membros inferiores e estabilização de tronco.",
                instructions = "1. Pés na largura dos ombros com pontas levemente para fora.\n2. Inicie projetando o quadril para trás e flexionando os joelhos.\n3. Desça até as coxas ficarem paralelas ao chão.\n4. Mantenha o peito aberto e suba empurrando o chão com os calcanhares.",
                difficulty = "Iniciante",
                equipment = "Sem equipamento",
                defaultDurationSeconds = 45,
                defaultReps = "12 a 15 repetições",
                targetMuscles = "Quadríceps, Glúteos, Isquiotibiais e Core",
                variations = "Agachamento sumô, Agachamento com salto, Agachamento búlgaro",
                alternatives = "Sentar e levantar da cadeira, Leg press",
                commonMistakes = "Girar os joelhos para dentro (valgo dinâmico) ou tirar os calcanhares do chão.",
                safetyTips = "Mantenha a coluna neutra e olhe para frente. Pare se sentir desconforto agudo no joelho."
            ),
            ExerciseEntity(
                name = "Flexão de Braços (Push-Up)",
                category = "Peito",
                description = "Exercício clássico de empurrar para desenvolvimento de força superior e estabilidade escapular.",
                instructions = "1. Apoie as mãos no chão na largura dos ombros.\n2. Corpo em linha reta da cabeça aos calcanhares.\n3. Desça controladamente até o peito quase tocar o chão.\n4. Empurre firmemente de volta à posição inicial sem arquear a lombar.",
                difficulty = "Intermediário",
                equipment = "Sem equipamento",
                defaultDurationSeconds = 40,
                defaultReps = "8 a 12 repetições",
                targetMuscles = "Peitoral Maior, Tríceps braquial e Deltóide Anterior",
                variations = "Flexão com joelhos no chão, Flexão declinada, Flexão diamante",
                alternatives = "Flexão na parede ou banco inclinado",
                commonMistakes = "Deixar o quadril cair ou cotovelos abertos em 90 graus em relação ao tronco.",
                safetyTips = "Mantenha cotovelos em ângulo de aprox. 45 graus com as costelas para proteger os ombros."
            ),
            ExerciseEntity(
                name = "Prancha Isométrica (Forearm Plank)",
                category = "Core",
                description = "Fortalecimento do complexo lombo-pélvico com resistência anti-extensão.",
                instructions = "1. Apoie os antebraços e as pontas dos pés no solo.\n2. Cotovelos diretamente alinhados abaixo dos ombros.\n3. Contraia glúteos e abdômen, mantendo uma linha reta.\n4. Respire de forma contínua sem prender o ar.",
                difficulty = "Iniciante",
                equipment = "Tapete",
                defaultDurationSeconds = 30,
                defaultReps = "30 a 45 segundos de sustentação",
                targetMuscles = "Reto Abdominal, Transverso, Oblíquos e Glúteos",
                variations = "Prancha lateral, Prancha com elevação de perna, Prancha com toque nos ombros",
                alternatives = "Prancha com apoio de joelhos",
                commonMistakes = "Deixar a coluna lombar afundar ou elevar excessivamente os quadris.",
                safetyTips = "Se sentir dor lombar, desça os joelhos ao chão imediatamente para realinhar a bacia."
            ),
            ExerciseEntity(
                name = "Polichinelos (Jumping Jacks)",
                category = "Cardio",
                description = "Excelente ativador neuromuscular de aquecimento e elevação de frequência cardíaca.",
                instructions = "1. Fique de pé com pés juntos e braços ao lado do corpo.\n2. Salte abrindo as pernas e levantando os braços acima da cabeça.\n3. Retorne suavemente à posição inicial num ritmo constante.",
                difficulty = "Iniciante",
                equipment = "Sem equipamento",
                defaultDurationSeconds = 40,
                defaultReps = "30 a 50 segundos contínuos",
                targetMuscles = "Gêmeos, Quadríceps, Ombros e Sistema Cardiovascular",
                variations = "Polichinelo sem salto (baixo impacto), Cross jacks",
                alternatives = "Caminhada rápida no lugar, Step touch",
                commonMistakes = "Pousar com os joelhos rígidos gerando impacto no solo.",
                safetyTips = "Amorteça a aterrissagem na ponta dos pés com joelhos levemente flexionados."
            ),
            ExerciseEntity(
                name = "Afundo / Passada (Lunge)",
                category = "Pernas",
                description = "Trabalho unilateral de pernas para simetria de força e equilíbrio dinâmico.",
                instructions = "1. Dê um passo largo para frente com uma das pernas.\n2. Desça ambos os joelhos até formarem ângulos de 90 graus.\n3. O joelho traseiro aproxima-se do chão sem bater.\n4. Empurre com a perna da frente para retornar.",
                difficulty = "Intermediário",
                equipment = "Sem equipamento",
                defaultDurationSeconds = 45,
                defaultReps = "10 a 12 reps cada lado",
                targetMuscles = "Quadríceps, Glúteo Máximo e Panturrilhas",
                variations = "Afundo reverso, Passada caminhando, Afundo com halteres",
                alternatives = "Subida no degrau (Step-up)",
                commonMistakes = "Dar um passo muito curto e sobrecarregar o joelho dianteiro.",
                safetyTips = "Mantenha o tronco ereto e o olhar firme no horizonte para manter o equilíbrio."
            ),
            ExerciseEntity(
                name = "Gato e Vaca (Cat-Cow)",
                category = "Mobilidade",
                description = "Sequência clássica de mobilização segmentar da coluna vertebral e relaxamento.",
                instructions = "1. Posição de quatro apoios com punhos sob ombros e joelhos sob quadris.\n2. Inspire, arqueie as costas para baixo e olhe suavemente para cima (Vaca).\n3. Expire, arredonde as costas para cima em direção ao teto (Gato).\n4. Flua no ritmo da respiração.",
                difficulty = "Iniciante",
                equipment = "Tapete",
                defaultDurationSeconds = 60,
                defaultReps = "8 a 10 ciclos respiratórios",
                targetMuscles = "Paravertebrais, Trapézio, Mobilidade Torácica e Cervical",
                variations = "Adicionar torção torácica com abertura de braço",
                alternatives = "Alongamento da criança (Child's Pose)",
                commonMistakes = "Forçar demais o pescoço ou mover rápido sem sincronizar a respiração.",
                safetyTips = "Movimento suave e controlado sem sentir pontadas agudas."
            ),
            ExerciseEntity(
                name = "Remada Curvada com Halteres (Dumbbell Bent-Over Row)",
                category = "Costas",
                description = "Fortalecimento dos dorsais e estabilizadores posturais da cintura escapular.",
                instructions = "1. Segure os halteres, flexione levemente os joelhos e incline o tronco a 45 graus.\n2. Mantenha as costas retas e o core ativado.\n3. Puxe os halteres em direção ao quadril aproximando as escápulas.\n4. Desça de forma controlada.",
                difficulty = "Intermediário",
                equipment = "Halteres",
                defaultDurationSeconds = 45,
                defaultReps = "10 a 12 repetições",
                targetMuscles = "Latíssimo do Dorso, Romboides, Trapézio Médio e Bíceps",
                variations = "Remada unilateral com apoio em banco, Remada com elástico",
                alternatives = "Remada com toalha na porta, Remada invertida",
                commonMistakes = "Arredondar a região lombar durante a puxada.",
                safetyTips = "Trave o abdômen para proteger a coluna. Se a lombar cansar, reduza a carga."
            ),
            ExerciseEntity(
                name = "Tríceps no Banco ou Cadeira (Bench Dip)",
                category = "Braços",
                description = "Exercício de peso corporal focado na cadeia posterior do braço.",
                instructions = "1. Sente-se na borda de um banco ou cadeira firme com as mãos ao lado do quadril.\n2. Avance os pés e projete o quadril para fora do banco.\n3. Flexione os cotovelos até aprox. 90 graus mantendo as costas perto do banco.\n4. Estenda os braços retornando ao topo.",
                difficulty = "Iniciante",
                equipment = "Banco",
                defaultDurationSeconds = 40,
                defaultReps = "10 a 15 repetições",
                targetMuscles = "Tríceps Braquial e Deltóide Anterior",
                variations = "Tríceps banco com pernas estendidas, Tríceps com pés elevados",
                alternatives = "Extensão de tríceps acima da cabeça com halter ou elástico",
                commonMistakes = "Afastar as costas para muito longe da cadeira, forçando a cápsula anterior do ombro.",
                safetyTips = "Mantenha os ombros abaixados e longe das orelhas."
            ),
            ExerciseEntity(
                name = "Mountain Climbers (Escaladores)",
                category = "Condicionamento",
                description = "Exercício de alta queima calórica combinando prancha dinâmica e cardio.",
                instructions = "1. Comece na posição de prancha alta com as mãos no chão.\n2. Puxe um joelho em direção ao peito com rapidez e controle.\n3. Alterne as pernas rapidamente como se estivesse correndo na horizontal.",
                difficulty = "Intermediário",
                equipment = "Sem equipamento",
                defaultDurationSeconds = 30,
                defaultReps = "30 a 45 segundos dinâmicos",
                targetMuscles = "Core, Flexores de Quadril, Ombros e Cardiovascular",
                variations = "Climber lento focado em contração, Climber cruzado",
                alternatives = "Elevação de joelho em pé",
                commonMistakes = "Subir o bumbum muito alto ou bater o pé com força no solo.",
                safetyTips = "Mantenha os ombros firmemente estabilizados sobre os punhos."
            ),
            ExerciseEntity(
                name = "Ponte de Glúteos (Glute Bridge)",
                category = "Pernas",
                description = "Ativação profunda de glúteos e cadeia posterior sem compressão axial.",
                instructions = "1. Deite-se de barriga para cima com joelhos flexionados e pés apoiados no chão.\n2. Pressione os calcanhares no chão e eleve o quadril até alinhar com os joelhos.\n3. Aperte bem os glúteos no ponto mais alto por 2 segundos.\n4. Desça suavemente sem relaxar totalmente.",
                difficulty = "Iniciante",
                equipment = "Tapete",
                defaultDurationSeconds = 45,
                defaultReps = "15 repetições",
                targetMuscles = "Glúteo Máximo, Isquiotibiais e Eretores da Espinha",
                variations = "Ponte unilateral (uma perna só), Ponte com elástico nos joelhos",
                alternatives = "Hip Thrust com peso",
                commonMistakes = "Hiperestender a coluna lombar ao invés de contrair o glúteo.",
                safetyTips = "Não jogue a força no pescoço; mantenha a pressão distribuída entre calcanhares e escápulas."
            ),
            ExerciseEntity(
                name = "Alongamento da Criança (Child's Pose)",
                category = "Flexibilidade",
                description = "Postura restaurativa para descompressão lombar e relaxamento neuromuscular.",
                instructions = "1. Ajoelhe-se no chão, una os dedões dos pés e afaste os joelhos.\n2. Sente-se sobre os calcanhares e deslize os braços à frente no tapete.\n3. Apoie a testa suavemente no chão e respire fundo.",
                difficulty = "Iniciante",
                equipment = "Tapete",
                defaultDurationSeconds = 60,
                defaultReps = "1 a 2 minutos de respiração relaxante",
                targetMuscles = "Lombar, Dorsais, Quadris e Ombros",
                variations = "Child's Pose lateral caminhando com as mãos para o lado",
                alternatives = "Abraçar os joelhos deitado de costas",
                commonMistakes = "Tensionar o pescoço ou forçar os joelhos além do limite confortável.",
                safetyTips = "Use uma almofada sob o quadril se sentir rigidez nas coxas ou tornozelos."
            ),
            ExerciseEntity(
                name = "Elevação Lateral com Halteres / Elástico",
                category = "Ombros",
                description = "Isolamento dos deltóides laterais para largura de ombros e postura ereta.",
                instructions = "1. Fique de pé segurando halteres ao lado das coxas com leve flexão de cotovelos.\n2. Eleve os braços lateralmente até a altura dos ombros.\n3. Pause no topo por 1 segundo e desça de maneira controlada.",
                difficulty = "Iniciante",
                equipment = "Halteres",
                defaultDurationSeconds = 45,
                defaultReps = "12 a 15 repetições",
                targetMuscles = "Deltóide Lateral e Trapézio Superior",
                variations = "Elevação lateral com faixa elástica, Elevação sentado",
                alternatives = "Elevação lateral sem peso com isometria",
                commonMistakes = "Usar impulso com o corpo ou erguer acima da linha dos ombros.",
                safetyTips = "Use carga moderada; o deltóide responde melhor à forma técnica do que ao excesso de peso."
            ),
            ExerciseEntity(
                name = "Bird-Dog (Perdigueiro)",
                category = "Equilíbrio",
                description = "Exercício de estabilização cruzada fundamental para saúde da coluna e controle motor.",
                instructions = "1. Em 4 apoios, alinhe ombros sobre as mãos e quadris sobre joelhos.\n2. Estenda simultaneamente o braço direito para frente e a perna esquerda para trás.\n3. Mantenha o tronco e o quadril perfeitamente nivelados.\n4. Retorne e alterne os lados com controle.",
                difficulty = "Iniciante",
                equipment = "Tapete",
                defaultDurationSeconds = 45,
                defaultReps = "10 repetições de cada lado",
                targetMuscles = "Core, Glúteos, Multifídios e Deltóides",
                variations = "Bird-dog com isometria de 5 segundos, Bird-dog tocando cotovelo no joelho",
                alternatives = "Dead bug deitado de costas",
                commonMistakes = "Girar o quadril para o lado ou balançar o corpo descontroladamente.",
                safetyTips = "Imagine que há um copo d'água na sua lombar e você não pode derramar uma gota."
            ),
            ExerciseEntity(
                name = "Burpee Adaptado (Sem Salto)",
                category = "Corpo Inteiro",
                description = "Condicionamento físico global de baixo impacto e alto retorno metabólico.",
                instructions = "1. Em pé, flexione os joelhos e apoie as mãos no chão.\n2. Dê um passo atrás com uma perna e depois a outra em posição de prancha.\n3. Dê um passo à frente com cada pé e retorne à posição de pé estendendo o quadril.",
                difficulty = "Iniciante",
                equipment = "Sem equipamento",
                defaultDurationSeconds = 45,
                defaultReps = "8 a 10 repetições com cadência",
                targetMuscles = "Corpo inteiro, Cadeia anterior, Posterior e Pulmões",
                variations = "Burpee completo com salto, Burpee com flexão de peito",
                alternatives = "Agachamento com soco à frente",
                commonMistakes = "Despencar com o quadril na posição de prancha.",
                safetyTips = "Mantenha o core firme durante todo o ciclo do movimento."
            )
        )
        dao.insertExercises(exercises)

        // Seed Challenges
        val challenges = listOf(
            ChallengeEntity(
                title = "7 Dias de Movimento",
                subtitle = "Desperte seu corpo com 10 a 15 min diários",
                durationDays = 7,
                category = "Movimento",
                description = "Uma jornada progressiva de 7 dias para criar o hábito de se mover todos os dias sem sobrecarregar.",
                xpReward = 50,
                isActive = true,
                currentDay = 3,
                isCompleted = false
            ),
            ChallengeEntity(
                title = "7 Dias de Mobilidade",
                subtitle = "Liberte articulações e alivie tensões",
                durationDays = 7,
                category = "Mobilidade",
                description = "Foco em tornozelos, quadris e coluna para melhorar sua flexibilidade e qualidade de vida.",
                xpReward = 60,
                isActive = false,
                currentDay = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                title = "14 Dias Sem Equipamento",
                subtitle = "Construa força com o próprio peso corporal",
                durationDays = 14,
                category = "Sem Equipamento",
                description = "Treinos funcionais usando calistenia adaptada para fazer na sala de casa ou no parque.",
                xpReward = 100,
                isActive = false,
                currentDay = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                title = "21 Dias de Hábito Ativo",
                subtitle = "O tempo comprovado para consolidar uma rotina",
                durationDays = 21,
                category = "Consistência",
                description = "Consistência é o verdadeiro segredo da evolução física sustentável.",
                xpReward = 150,
                isActive = false,
                currentDay = 0,
                isCompleted = false
            ),
            ChallengeEntity(
                title = "30 Dias de Consistência Serte",
                subtitle = "A transformação completa do seu bem-estar",
                durationDays = 30,
                category = "Consistência",
                description = "Desafio principal da comunidade Serte AI para transformar sua saúde com inteligência e moderação.",
                xpReward = 250,
                isActive = false,
                currentDay = 0,
                isCompleted = false
            )
        )
        dao.insertChallenges(challenges)

        // Seed Habits
        val habits = listOf(
            HabitEntity(
                title = "Beber 2L de Água",
                description = "Mantenha músculos e articulações bem hidratados",
                iconName = "water",
                target = "2000 ml",
                isCompletedToday = true,
                streak = 4,
                xpReward = 5
            ),
            HabitEntity(
                title = "15 min de Movimento Diário",
                description = "Qualquer movimento intencional conta",
                iconName = "fitness",
                target = "15 min",
                isCompletedToday = true,
                streak = 3,
                xpReward = 10
            ),
            HabitEntity(
                title = "Alongar ao Acordar",
                description = "5 minutos de descompressão matinal",
                iconName = "morning",
                target = "5 min",
                isCompletedToday = false,
                streak = 2,
                xpReward = 5
            ),
            HabitEntity(
                title = "Dormir 7 a 8 Horas",
                description = "O pilar número 1 da recuperação muscular",
                iconName = "sleep",
                target = "8 horas",
                isCompletedToday = false,
                streak = 5,
                xpReward = 5
            )
        )
        dao.insertHabits(habits)

        // Seed Achievements
        val achievements = listOf(
            AchievementEntity(
                code = "FIRST_WORKOUT",
                title = "Primeiro Treino",
                description = "Completou sua primeira sessão de movimento com a Serte AI.",
                iconName = "flag",
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis() - 172800000L,
                xpValue = 20
            ),
            AchievementEntity(
                code = "STREAK_3",
                title = "3 Dias Seguidos",
                description = "Manteve o ritmo por 3 dias consecutivos.",
                iconName = "fire",
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis() - 3600000L,
                xpValue = 30
            ),
            AchievementEntity(
                code = "STREAK_7",
                title = "7 Dias de Foco",
                description = "Uma semana completa com o corpo em movimento!",
                iconName = "bolt",
                isUnlocked = false,
                unlockedAt = null,
                xpValue = 50
            ),
            AchievementEntity(
                code = "MOBILITY_MASTER",
                title = "Mestre da Mobilidade",
                description = "Concluiu 5 sessões de mobilidade articular.",
                iconName = "sparkles",
                isUnlocked = false,
                unlockedAt = null,
                xpValue = 40
            ),
            AchievementEntity(
                code = "CHALLENGE_CHAMP",
                title = "Campeão de Desafio",
                description = "Finalizou com sucesso um desafio da comunidade.",
                iconName = "trophy",
                isUnlocked = false,
                unlockedAt = null,
                xpValue = 100
            )
        )
        dao.insertAchievements(achievements)

        // Seed Starter Community Posts
        val communityPosts = listOf(
            CommunityPostEntity(
                authorName = "Mariana S.",
                authorBadge = "Nível 3 • 14 Dias",
                timeAgo = "há 2 horas",
                content = "Acabei de fazer a sessão de 15 minutos de mobilidade da Serte AI antes do trabalho. Sensação incrível de alívio nas costas e no pescoço! 💪",
                workoutTag = "Mobilidade Matinal 15m",
                likesCount = 14,
                isLikedByMe = true,
                commentsCount = 3
            ),
            CommunityPostEntity(
                authorName = "Rodrigo Lima",
                authorBadge = "Nível 5 • 30 Dias",
                timeAgo = "há 5 horas",
                content = "Dia 3 do desafio de movimento concluído! O que mais gosto é que a IA adapta se você disser que achou difícil no dia anterior. Nada de loucura, apenas evolução consistente.",
                workoutTag = "Desafio 7 Dias de Movimento",
                likesCount = 28,
                isLikedByMe = false,
                commentsCount = 5
            ),
            CommunityPostEntity(
                authorName = "Camila Duarte",
                authorBadge = "Iniciante • 5 Dias",
                timeAgo = "ontem",
                content = "Primeira vez na vida que consigo treinar 5 dias seguidos sem desanimar. A dica do Serte AI Assistant sobre respiração no agachamento mudou tudo pra mim!",
                workoutTag = "Treino Casa Iniciante 20m",
                likesCount = 39,
                isLikedByMe = true,
                commentsCount = 7
            )
        )
        dao.insertCommunityPosts(communityPosts)

        // Seed Initial Chat Welcome Message
        dao.insertChatMessage(
            ChatMessageEntity(
                isFromUser = false,
                message = "Olá! Eu sou a Serte AI. Como posso ajudar no seu movimento hoje?",
                detectedIntent = "SAUDACAO",
                relatedWorkoutJson = null,
                timestamp = System.currentTimeMillis()
            )
        )

        // Initial Sample Workout
        dao.insertWorkout(
            WorkoutEntity(
                title = "Treino Serte AI de Iniciação",
                goal = "Condicionamento",
                level = "Iniciante",
                durationMinutes = 20,
                location = "Casa",
                equipment = "Sem equipamento",
                exercisesSummary = "Polichinelos, Agachamento Livre, Flexão Adaptada, Prancha, Alongamento",
                structuredStagesJson = """[
                    {"stageName": "Aquecimento", "duration": "3 min", "exercises": ["Polichinelos", "Gato e Vaca"]},
                    {"stageName": "Bloco Principal", "duration": "14 min", "exercises": ["Agachamento Livre (Air Squat)", "Flexão de Braços (Push-Up)", "Ponte de Glúteos", "Prancha Isométrica"]},
                    {"stageName": "Finalização", "duration": "3 min", "exercises": ["Alongamento da Criança"]}
                ]""",
                generatedByAi = true,
                isCompleted = false,
                caloriesEstimated = 135
            )
        )

        // Seed Educational Knowledge Articles & Video Guides
        val articles = listOf(
            EducationalArticleEntity(
                title = "Benefícios do Treino de Força: Longevidade e Metabolismo",
                category = "Força",
                contentType = "Artigo",
                summary = "Entenda como o estímulo resistido preserva massa óssea, melhora a sensibilidade à insulina e retarda o envelhecimento celular.",
                fullContent = "O treino de força é um dos pilares mais estudados da medicina preventiva e longevidade.\n\n" +
                        "1. Proteção Óssea e Articular: Ao aplicar tensão mecânica progressiva nos ossos, os osteoblastos são estimulados a mineralizar o tecido ósseo, prevenindo osteopenia e osteoporose.\n\n" +
                        "2. Taxa Metabólica Basal: Músculos consomem mais energia em repouso do que tecido adiposo. Desenvolver força aumenta o gasto calórico diário de forma passiva.\n\n" +
                        "3. Sensibilidade à Insulina: O músculo esquelético é o maior consumidor de glicose do corpo. Treinar força facilita a captação de açúcar no sangue sem sobrecarregar o pâncreas.\n\n" +
                        "4. Autonomia Funcional: Erguer sacolas, subir escadas e manter equilíbrio na terceira idade dependem diretamente da potência muscular construída hoje.",
                readTimeMinutes = 4,
                keyTakeaways = "• Treinar força beneficia o metabolismo 24h por dia\n• Fortalece ligamentos, tendões e ossos\n• 2 a 3 sessões semanais geram adaptações profundas",
                tags = "força, hipertrofia, ossos, longevidade, metabolismo"
            ),
            EducationalArticleEntity(
                title = "Cardio Inteligente: VO2 Máximo e Saúde do Coração",
                category = "Cardio",
                contentType = "Vídeo Guia",
                summary = "Guia audiovisual demonstrando como balancear cardio contínuo leve com picos de frequência sem sobrecarregar articulações.",
                fullContent = "Muitas pessoas associam cardio a exaustão extrema ou corrida desgastante. No entanto, o cardio moderno baseia-se em zonas de treino inteligentes:\n\n" +
                        "• Zona 2 (Conversacional): Caminhada acelerada ou bicicleta onde você consegue manter uma conversa sem perder o fôlego. É aqui que ocorre a biogênese mitocondrial e queima eficiente de gordura como substrato.\n\n" +
                        "• Picos de Intensidade (HIIT): Séries de 30 a 45 segundos para desafiar o bombeamento ventricular e elevar a capacidade pulmonar.\n\n" +
                        "Dica Serte: Alternar 15 minutos de cardio suave com 1 sessão intervalada semanal promove saúde cardiovascular exemplar sem desgastar seus joelhos.",
                readTimeMinutes = 3,
                videoDuration = "04:30",
                keyTakeaways = "• Cardio não precisa ser exaustivo para ser eficaz\n• A 'Zona 2' constrói a base aeróbica mais saudável\n• 15 minutos diários de caminhada reduzem mortalidade geral em até 20%",
                tags = "cardio, coração, vo2, caminhada, fôlego"
            ),
            EducationalArticleEntity(
                title = "Mobilidade vs Flexibilidade: Liberdade Articular",
                category = "Mobilidade",
                contentType = "Artigo",
                summary = "Qual a diferença real entre alongar passivamente e ter controle motor ativo sobre suas articulações no dia a dia.",
                fullContent = "Flexibilidade é a capacidade passiva de um músculo se esticar (ex: encostar as mãos nos pés). Já a MOBILIDADE é a capacidade de produzir força e controle em toda a amplitude de movimento articular.\n\n" +
                        "Por que priorizar mobilidade na SERTE AI?\n" +
                        "• Redução drástica de dores na lombar causadas por quadris encurtados.\n" +
                        "• Alívio de tensões nos ombros e pescoço provocadas por horas em frente a telas.\n" +
                        "• Movimentos cotidianos mais leves, soltos e prazerosos.\n\n" +
                        "Rotina básica recomendada: 5 minutos diários de postura do Gato-Vaca, círculos de quadril e torções torácicas suaves ao despertar.",
                readTimeMinutes = 3,
                keyTakeaways = "• Flexibilidade é passiva; mobilidade é controle motor ativo\n• Quadris móveis protegem a coluna lombar\n• 5 a 10 min diários desfazem o impacto do sedentarismo",
                tags = "mobilidade, flexibilidade, postura, articulações, lombar"
            ),
            EducationalArticleEntity(
                title = "Nutrição Funcional Básica: O Combustível do Seu Movimento",
                category = "Nutrição",
                contentType = "Artigo",
                summary = "Princípios descomplicados de hidratação, proteínas de qualidade e densidade nutricional sem dietas restritivas malucas.",
                fullContent = "Comida é energia, informação e material de construção celular. A SERTE AI apoia uma relação saudável e intuitiva com a comida:\n\n" +
                        "1. Hidratação Constante: Seus músculos são formados por mais de 70% de água. Desidratação leve de apenas 2% reduz o rendimento físico e a clareza mental em 15%.\n\n" +
                        "2. Proteína em Cada Refeição: Fornece aminoácidos essenciais para recuperar fibras musculares (ovos, leguminosas, peixes, laticínios magros ou opções vegetais).\n\n" +
                        "3. Comida de Verdade: Priorize alimentos desembalados da natureza (vegetais, frutas, grãos integrais, raízes). Evite ultraprocessados com excesso de açúcar refinado.\n\n" +
                        "Lembre-se: Para planos alimentares específicos, consulte sempre um(a) nutricionista credenciado(a)!",
                readTimeMinutes = 4,
                keyTakeaways = "• Beba 35ml de água por quilo de peso\n• Distribua proteínas ao longo do dia para reparo contínuo\n• Simplicidade supera qualquer dieta da moda",
                tags = "nutrição, hidratação, proteínas, energia, alimentação saudável"
            ),
            EducationalArticleEntity(
                title = "Sono e Recuperação: Onde a Verdadeira Transformação Acontece",
                category = "Sono & Recuperação",
                contentType = "Vídeo Guia",
                summary = "Como o sono profundo repara tecidos, regula o apetite e otimiza o sistema imunológico pós-exercício.",
                fullContent = "Treinar gera um estímulo catabólico de microlesões controladas. A recuperação e o fortalecimento só ocorrem durante o sono reparador.\n\n" +
                        "• Hormônio do Crescimento (GH): Mais de 70% da liberação diária de GH ocorre durante os estágios de sono de ondas lentas (sono profundo).\n\n" +
                        "• Regulação de Grelina e Leptina: Dormir mal aumenta o hormônio da fome (grelina) e diminui a saciedade (leptina), tornando o dia seguinte muito mais difícil.\n\n" +
                        "Higiene do Sono Serte:\n" +
                        "- Diminua luzes artificiais e telas 45 minutos antes de deitar.\n" +
                        "- Mantenha o quarto ventilado e escuro.\n" +
                        "- Evite cafeína após as 14h.",
                readTimeMinutes = 4,
                videoDuration = "05:15",
                keyTakeaways = "• Sem sono reparador, nenhum treino atinge o ápice\n• Sono profundo estimula a síntese de colágeno e tecidos\n• Ritual de desaceleração melhora a qualidade do descanso",
                tags = "sono, descanso, recuperação, gh, higiene do sono"
            ),
            EducationalArticleEntity(
                title = "A Psicologia da Consistência: 15 Minutos Valem Mais que 2 Horas",
                category = "Consistência",
                contentType = "Artigo",
                summary = "Por que começar pequeno e nunca quebrar a sequência transforma sua identidade e garante resultados duradouros.",
                fullContent = "O maior erro cometido por quem decide começar a treinar é a sobrecarga inicial: querer treinar 6 dias por semana durante 2 horas logo na primeira semana.\n\n" +
                        "O cérebro humano interpreta mudanças drásticas como ameaça de gasto de energia e responde com fadiga e procrastinação.\n\n" +
                        "A Regra dos 15 Minutos:\n" +
                        "• É fácil convencer seu cérebro a fazer 15 minutos, mesmo após um dia cansativo.\n" +
                        "• 15 minutos por dia = 105 minutos na semana = mais de 90 horas de treino ativo no ano!\n" +
                        "• Você cria a identidade de 'uma pessoa que cuida do corpo todos os dias'.\n\n" +
                        "O lema da SERTE AI: 'Seu movimento. Sua evolução. Sua IA.' A evolução é gradual, elegante e definitiva.",
                readTimeMinutes = 3,
                keyTakeaways = "• Consistência diária é superior à intensidade esporádica\n• O hábito solidifica a identidade atlética saudável\n• Foque em não quebrar a sequência, mesmo nos dias difíceis",
                tags = "consistência, motivação, hábitos, disciplina, rotina"
            )
        )
        dao.insertArticles(articles)
    }
}
