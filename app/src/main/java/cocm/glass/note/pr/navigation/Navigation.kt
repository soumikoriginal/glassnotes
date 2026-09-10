package cocm.glass.note.pr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cocm.glass.note.pr.di.AppContainer
import cocm.glass.note.pr.presentation.archive.ArchiveScreen
import cocm.glass.note.pr.presentation.archive.ArchiveViewModel
import cocm.glass.note.pr.presentation.editor.EditorScreen
import cocm.glass.note.pr.presentation.editor.EditorViewModel
import cocm.glass.note.pr.presentation.home.HomeScreen
import cocm.glass.note.pr.presentation.home.HomeViewModel
import cocm.glass.note.pr.presentation.labels.LabelsScreen
import cocm.glass.note.pr.presentation.labels.LabelsViewModel
import cocm.glass.note.pr.presentation.search.SearchScreen
import cocm.glass.note.pr.presentation.search.SearchViewModel
import cocm.glass.note.pr.presentation.settings.SettingsScreen
import cocm.glass.note.pr.presentation.settings.SettingsViewModel
import cocm.glass.note.pr.presentation.trash.TrashScreen
import cocm.glass.note.pr.presentation.trash.TrashViewModel

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Editor : Screen("editor?noteId={noteId}") {
        fun createRoute(noteId: Long?) = if (noteId != null) "editor?noteId=$noteId" else "editor"
    }
    object Search : Screen("search")
    object Labels : Screen("labels")
    object Archive : Screen("archive")
    object Trash : Screen("trash")
    object Settings : Screen("settings")
}

@Composable
fun GlassNotesNavigation(
    container: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            cocm.glass.note.pr.presentation.onboarding.OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = container.viewModelFactory
            )
            HomeScreen(
                viewModel = viewModel,
                onNavigateToEditor = { noteId ->
                    navController.navigate(Screen.Editor.createRoute(noteId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Editor.route,
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    nullable = true
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId")?.takeIf { it != -1L }
            val viewModel: EditorViewModel = viewModel(
                factory = container.viewModelFactory
            )
            EditorScreen(
                viewModel = viewModel,
                noteId = noteId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                container = container
            )
        }

        composable(Screen.Search.route) {
            val viewModel: SearchViewModel = viewModel(
                factory = container.viewModelFactory
            )
            SearchScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNoteClick = { noteId ->
                    navController.navigate(Screen.Editor.createRoute(noteId))
                }
            )
        }

        composable(Screen.Labels.route) {
            val viewModel: LabelsViewModel = viewModel(
                factory = container.viewModelFactory
            )
            LabelsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLabelClick = { label ->
                    // Navigate to filtered notes view (could be implemented later)
                }
            )
        }

        composable(Screen.Archive.route) {
            val viewModel: ArchiveViewModel = viewModel(
                factory = container.viewModelFactory
            )
            ArchiveScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNoteClick = { noteId ->
                    navController.navigate(Screen.Editor.createRoute(noteId))
                }
            )
        }

        composable(Screen.Trash.route) {
            val viewModel: TrashViewModel = viewModel(
                factory = container.viewModelFactory
            )
            TrashScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNoteClick = { noteId ->
                    navController.navigate(Screen.Editor.createRoute(noteId))
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel(
                factory = container.viewModelFactory
            )
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onBackupClick = {
                    // TODO: Implement backup
                },
                onRestoreClick = {
                    // TODO: Implement restore
                }
            )
        }
    }
}

