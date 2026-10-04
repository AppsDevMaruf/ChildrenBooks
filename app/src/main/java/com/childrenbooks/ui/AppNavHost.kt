package com.childrenbooks.ui

import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.childrenbooks.core.designsystem.components.ParentGateDialog
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.feature.grownups.GrownupsReportRoute
import com.childrenbooks.feature.grownups.GrownupsSettingsRoute
import com.childrenbooks.feature.home.HomeRoute
import com.childrenbooks.feature.lesson.LessonRoute
import com.childrenbooks.feature.lesson.LessonViewModel
import com.childrenbooks.feature.listen.ListenHomeRoute
import com.childrenbooks.feature.listen.ListenShelfRoute
import com.childrenbooks.feature.listen.ListenViewModel
import com.childrenbooks.feature.onboarding.OnboardingRoute
import com.childrenbooks.feature.play.GameRoute
import com.childrenbooks.feature.play.PlayHubRoute
import com.childrenbooks.feature.practice.PracticeRoute
import com.childrenbooks.feature.practice.PracticeViewModel
import com.childrenbooks.feature.reward.RewardRoute
import com.childrenbooks.feature.reward.RewardViewModel
import com.childrenbooks.feature.stickers.StickersRoute
import com.childrenbooks.feature.world.WorldRoute
import com.childrenbooks.feature.world.WorldViewModel

/** Route patterns and builders. Argument names match the ViewModels' SavedStateHandle keys. */
object Destinations {
    // Declared before Game: a const val initializer cannot forward-reference a later const.
    const val GameIdArg = "gameId"

    const val Onboarding = "onboarding"
    const val Home = "home"
    const val World = "world/{${WorldViewModel.WorldIdArg}}"
    const val Lesson = "lesson/{${LessonViewModel.WorldIdArg}}/{${LessonViewModel.SetIndexArg}}?${LessonViewModel.CardArg}={${LessonViewModel.CardArg}}"
    const val Practice = "practice/{${PracticeViewModel.WorldIdArg}}/{${PracticeViewModel.SetIndexArg}}"
    const val Reward = "reward/{${RewardViewModel.WorldIdArg}}/{${RewardViewModel.SetIndexArg}}/{${RewardViewModel.StarsArg}}?${RewardViewModel.StickerArg}={${RewardViewModel.StickerArg}}"
    const val Stickers = "stickers"
    const val Play = "play"
    const val Game = "game/{$GameIdArg}"
    const val Listen = "listen"
    const val ListenShelf = "listen/{${ListenViewModel.ShelfArg}}"
    const val Grownups = "grownups"
    const val GrownupsSettings = "grownups/settings"

    fun world(id: WorldId) = "world/${id.key}"
    fun lesson(id: WorldId, setIndex: Int, card: Int = 0) = "lesson/${id.key}/$setIndex?${LessonViewModel.CardArg}=$card"
    fun practice(id: WorldId, setIndex: Int) = "practice/${id.key}/$setIndex"
    fun reward(id: WorldId, setIndex: Int, stars: Int, sticker: String?): String {
        val base = "reward/${id.key}/$setIndex/$stars"
        return if (sticker == null) base else "$base?${RewardViewModel.StickerArg}=${Uri.encode(sticker)}"
    }
    fun game(id: String) = "game/${Uri.encode(id)}"
    fun listenShelf(routeId: String) = "listen/${Uri.encode(routeId)}"
}

private val enter = fadeIn(tween(220))
private val exit = fadeOut(tween(160))

@Composable
fun AppNavHost(startDestination: String) {
    val nav = rememberNavController()
    var showGate by rememberSaveable { mutableStateOf(false) }

    NavHost(
        navController = nav,
        startDestination = startDestination,
        enterTransition = { enter },
        exitTransition = { exit },
        popEnterTransition = { enter },
        popExitTransition = { exit },
    ) {
        composable(Destinations.Onboarding) {
            OnboardingRoute(
                onFinished = {
                    nav.navigate(Destinations.Home) {
                        popUpTo(Destinations.Onboarding) { inclusive = true }
                    }
                },
            )
        }
        composable(Destinations.Home) { entry ->
            HomeRoute(
                onContinue = { item -> entry.ifResumed { nav.go(Destinations.lesson(item.worldId, item.setIndex, item.cardIndex)) } },
                onOpenWorld = { id -> entry.ifResumed { nav.go(Destinations.world(id)) } },
                onOpenPlay = { entry.ifResumed { nav.go(Destinations.Play) } },
                onOpenListen = { entry.ifResumed { nav.go(Destinations.Listen) } },
                onOpenStickers = { entry.ifResumed { nav.go(Destinations.Stickers) } },
                onOpenGrownups = { entry.ifResumed { showGate = true } },
            )
        }
        composable(
            Destinations.World,
            arguments = listOf(navArgument(WorldViewModel.WorldIdArg) { type = NavType.StringType }),
        ) { entry ->
            WorldRoute(
                onHome = { nav.popToHome() },
                onOpenSet = { id, setIndex -> entry.ifResumed { nav.go(Destinations.lesson(id, setIndex)) } },
            )
        }
        composable(
            Destinations.Lesson,
            arguments = listOf(
                navArgument(LessonViewModel.WorldIdArg) { type = NavType.StringType },
                navArgument(LessonViewModel.SetIndexArg) { type = NavType.IntType },
                navArgument(LessonViewModel.CardArg) {
                    type = NavType.IntType
                    defaultValue = 0
                },
            ),
        ) {
            LessonRoute(
                onHome = { nav.popToHome() },
                onOpenPractice = { id, setIndex -> nav.go(Destinations.practice(id, setIndex)) },
            )
        }
        composable(
            Destinations.Practice,
            arguments = listOf(
                navArgument(PracticeViewModel.WorldIdArg) { type = NavType.StringType },
                navArgument(PracticeViewModel.SetIndexArg) { type = NavType.IntType },
            ),
        ) {
            PracticeRoute(
                onHome = { nav.popToHome() },
                onFinished = { id, setIndex, stars, sticker ->
                    // The reward replaces lesson + practice so Back from it returns Home.
                    nav.navigate(Destinations.reward(id, setIndex, stars, sticker)) {
                        popUpTo(Destinations.Home)
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(
            Destinations.Reward,
            arguments = listOf(
                navArgument(RewardViewModel.WorldIdArg) { type = NavType.StringType },
                navArgument(RewardViewModel.SetIndexArg) { type = NavType.IntType },
                navArgument(RewardViewModel.StarsArg) { type = NavType.IntType },
                navArgument(RewardViewModel.StickerArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { entry ->
            RewardRoute(
                onNextSet = { id, setIndex -> entry.ifResumed { nav.goFromHome(Destinations.lesson(id, setIndex)) } },
                onPlayAgain = { id, setIndex -> entry.ifResumed { nav.goFromHome(Destinations.practice(id, setIndex)) } },
                onHome = { nav.popToHome() },
            )
        }
        composable(Destinations.Stickers) {
            StickersRoute(onHome = { nav.popToHome() })
        }
        composable(Destinations.Play) { entry ->
            PlayHubRoute(
                onHome = { nav.popToHome() },
                onOpenGame = { id -> entry.ifResumed { nav.go(Destinations.game(id)) } },
            )
        }
        composable(
            Destinations.Game,
            arguments = listOf(navArgument(Destinations.GameIdArg) { type = NavType.StringType }),
        ) { entry ->
            GameRoute(
                gameId = entry.arguments?.getString(Destinations.GameIdArg).orEmpty(),
                onBack = { entry.ifResumed { nav.popBackStack() } },
            )
        }
        listenGraph(nav)
        composable(Destinations.Grownups) { entry ->
            GrownupsReportRoute(
                onHome = { nav.popToHome() },
                onOpenSettings = { entry.ifResumed { nav.go(Destinations.GrownupsSettings) } },
                // Leaving to a kid lesson closes the grown-ups area, so Back can't skip the gate.
                onOpenCard = { id, setIndex, card -> entry.ifResumed { nav.goFromHome(Destinations.lesson(id, setIndex, card)) } },
            )
        }
        composable(Destinations.GrownupsSettings) { entry ->
            GrownupsSettingsRoute(onBack = { entry.ifResumed { nav.popBackStack() } })
        }
    }

    if (showGate) {
        ParentGateDialog(
            onPassed = {
                showGate = false
                nav.go(Destinations.Grownups)
            },
            onDismiss = { showGate = false },
        )
    }
}

private fun NavGraphBuilder.listenGraph(nav: NavController) {
    composable(Destinations.Listen) { entry ->
        ListenHomeRoute(
            onHome = { nav.popToHome() },
            onOpenShelf = { shelf -> entry.ifResumed { nav.go(Destinations.listenShelf(shelf.routeId)) } },
        )
    }
    composable(
        Destinations.ListenShelf,
        arguments = listOf(navArgument(ListenViewModel.ShelfArg) { type = NavType.StringType }),
    ) { entry ->
        ListenShelfRoute(onBack = { entry.ifResumed { nav.popBackStack() } })
    }
}

/** Ignores taps that land while a screen is still animating in or out (prevents double pushes). */
private inline fun NavBackStackEntry.ifResumed(action: () -> Unit) {
    if (lifecycle.currentState == Lifecycle.State.RESUMED) action()
}

private fun NavController.go(route: String) = navigate(route) { launchSingleTop = true }

/** Opens [route] with only Home beneath it. */
private fun NavController.goFromHome(route: String) = navigate(route) {
    popUpTo(Destinations.Home)
    launchSingleTop = true
}

private fun NavController.popToHome() {
    if (!popBackStack(Destinations.Home, inclusive = false)) {
        navigate(Destinations.Home) {
            popUpTo(graph.id) { inclusive = true }
        }
    }
}
