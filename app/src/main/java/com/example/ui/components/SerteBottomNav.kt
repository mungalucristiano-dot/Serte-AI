package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag

enum class SerteNavDestination(
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    INICIO("Início", Icons.Default.Home, "nav_item_home"),
    TREINOS("Treinos", Icons.Default.FitnessCenter, "nav_item_workouts"),
    EXERCICIOS("Exercícios", Icons.Default.SportsGymnastics, "nav_item_exercises"),
    CONHECIMENTO("Saber", Icons.Default.AutoStories, "nav_item_knowledge"),
    COMUNIDADE("Social", Icons.Default.People, "nav_item_community"),
    PERFIL("Perfil", Icons.Default.Person, "nav_item_profile")
}

@Composable
fun SerteBottomNav(
    currentDestination: SerteNavDestination,
    onNavigate: (SerteNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        SerteNavDestination.values().forEach { destination ->
            val isSelected = destination == currentDestination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title
                    )
                },
                label = { Text(text = destination.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(destination.testTag)
            )
        }
    }
}
