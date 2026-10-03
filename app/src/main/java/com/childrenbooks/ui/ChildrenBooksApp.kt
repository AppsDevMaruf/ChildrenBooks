package com.childrenbooks.ui

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed as lazyItemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.childrenbooks.R
import com.childrenbooks.data.local.LearningCatalog
import com.childrenbooks.domain.model.AlphabetLesson
import com.childrenbooks.domain.model.LessonItem
import com.childrenbooks.domain.model.NumberLesson
import com.childrenbooks.ui.about.AboutScreen
import com.childrenbooks.ui.components.ChevronLeftIcon
import com.childrenbooks.ui.components.ChevronRightIcon
import com.childrenbooks.ui.components.CircleTextButton
import com.childrenbooks.ui.components.PagerControls
import com.childrenbooks.ui.components.ParentGateDialog
import com.childrenbooks.ui.components.PlayIconButton
import com.childrenbooks.ui.components.ResizableText
import com.childrenbooks.ui.components.SoundIconButton
import com.childrenbooks.ui.components.StarProgressBar
import com.childrenbooks.ui.home.HomeBottomBar
import com.childrenbooks.ui.home.HomeScreen
import com.childrenbooks.ui.theme.AppColors
import com.childrenbooks.ui.games.GameHostScreen
import com.childrenbooks.ui.games.MiniGamesScreen
import com.childrenbooks.ui.listen.ListenCategory
import com.childrenbooks.ui.listen.ListenCategoryScreen
import com.childrenbooks.ui.listen.ListenScreen
import com.childrenbooks.ui.picture.AnimalsLearningScreen
import com.childrenbooks.ui.picture.BirdsLearningScreen
import com.childrenbooks.ui.picture.FishesLearningScreen
import com.childrenbooks.ui.picture.FlowersLearningScreen
import com.childrenbooks.ui.picture.FoodsLearningScreen
import com.childrenbooks.ui.picture.FruitsLearningScreen
import com.childrenbooks.ui.picture.FurnitureLearningScreen
import com.childrenbooks.ui.picture.ShapesLearningScreen
import com.childrenbooks.ui.picture.VegetablesLearningScreen
import com.childrenbooks.ui.state.SelectionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildrenBooksApp(
    appViewModel: AppViewModel = viewModel(),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry.appRoute()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isLessonDetail = route.startsWith("${Routes.Alphabet}/") ||
        route.startsWith("${Routes.Numbers}/") ||
        route == Routes.Weeks ||
        route == Routes.Colours ||
        route == Routes.Animals ||
        route == Routes.Fishes ||
        route == Routes.Foods ||
        route == Routes.Flowers ||
        route == Routes.Birds ||
        route == Routes.Furniture ||
        route == Routes.Fruits ||
        route == Routes.Vegetables ||
        route == Routes.Shapes

    val showBottomNavigation = route in Routes.TopLevelRoutes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (!isLessonDetail && route != Routes.Home) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White,
                    ),
                    title = {
                        Text(
                            text = routeTitle(route),
                            fontWeight = FontWeight.ExtraBold,
                        )
                    },
                    navigationIcon = {
                        if (route !in Routes.TopLevelRoutes) {
                            TextButton(onClick = navController::navigateBackOrHome) {
                                ChevronLeftIcon(color = Color.White, size = 26.dp)
                            }
                        }
                    },
                )
            }
        },
        bottomBar = {
            if (showBottomNavigation) {
                HomeBottomBar(
                    currentRoute = route,
                    onOpen = navController::navigateToTopLevel,
                )
            }
        },
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            NavHost(
                navController = navController,
                startDestination = Routes.Home,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable(Routes.Home) {
                    HomeScreen(onOpen = navController::navigateToRoute)
                }
                composable(Routes.Alphabet) {
                    AlphabetGrid { navController.navigateToRoute(Routes.alphabet(it)) }
                }
                composable(
                    route = Routes.AlphabetDetail,
                    arguments = listOf(navArgument(Routes.IndexArgument) { type = NavType.IntType }),
                ) { entry ->
                    val index = entry.intArgument(Routes.IndexArgument).coerceIn(LearningCatalog.alphabet.indices)
                    AlphabetPager(
                        initialPage = index,
                        onBack = navController::navigateBackOrHome,
                    )
                }
                composable(Routes.Numbers) {
                    NumberGrid { navController.navigateToRoute(Routes.number(it)) }
                }
                composable(
                    route = Routes.NumberDetail,
                    arguments = listOf(navArgument(Routes.IndexArgument) { type = NavType.IntType }),
                ) { entry ->
                    val index = entry.intArgument(Routes.IndexArgument).coerceIn(LearningCatalog.numbers.indices)
                    NumberPager(
                        initialPage = index,
                        onBack = navController::navigateBackOrHome,
                    )
                }
                composable(Routes.Weeks) { WeeksScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Months) { LessonList(LearningCatalog.months) }
                composable(Routes.Seasons) { LessonList(LearningCatalog.seasons) }
                composable(Routes.Colours) { ColorsScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Animals) { AnimalsLearningScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Fishes) { FishesLearningScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Foods) { FoodsLearningScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Flowers) { FlowersLearningScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Birds) { BirdsLearningScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Furniture) { FurnitureLearningScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Fruits) { FruitsLearningScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Vegetables) { VegetablesLearningScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Shapes) { ShapesLearningScreen(onBack = navController::navigateBackOrHome) }
                composable(Routes.Games) {
                    MiniGamesScreen { navController.navigateToRoute(Routes.game(it)) }
                }
                composable(
                    route = Routes.GameDetail,
                    arguments = listOf(navArgument(Routes.GameIdArgument) { type = NavType.StringType }),
                ) { entry ->
                    val gameId = entry.stringArgument(Routes.GameIdArgument)
                    if (Routes.isSupportedGame(gameId)) {
                        GameHostScreen(gameId)
                    } else {
                        LaunchedEffect(gameId) { navController.navigateBackOrHome() }
                    }
                }
                composable(Routes.Listen) {
                    ListenScreen { category ->
                        navController.navigateToRoute(Routes.listen(category.routeId))
                    }
                }
                composable(
                    route = Routes.ListenCategoryDetail,
                    arguments = listOf(navArgument(Routes.ListenCategoryArgument) { type = NavType.StringType }),
                ) { entry ->
                    val categoryId = entry.stringArgument(Routes.ListenCategoryArgument)
                    val category = ListenCategory.fromRouteId(categoryId)
                    if (category != null) {
                        ListenCategoryScreen(category)
                    } else {
                        LaunchedEffect(categoryId) { navController.navigateBackOrHome() }
                    }
                }
                composable(Routes.About) {
                    AboutScreen(
                        onMoreApps = { appViewModel.requestExternalAction(ExternalAction.MoreApps) },
                        onPrivacyPolicy = { appViewModel.requestExternalAction(ExternalAction.PrivacyPolicy) },
                        onRateApp = { appViewModel.requestExternalAction(ExternalAction.RateApp) },
                        onShare = { appViewModel.requestExternalAction(ExternalAction.ShareApp) },
                    )
                }
            }
        }
    }

    appUiState.pendingExternalAction?.let {
        ParentGateDialog(
            onApproved = {
                appViewModel.approveParentGate()?.let(context::performExternalAction)
            },
            onDismiss = appViewModel::dismissParentGate,
        )
    }
}

private fun NavBackStackEntry?.appRoute(): String {
    if (this == null) return Routes.Home
    return when (destination.route) {
        Routes.AlphabetDetail -> Routes.alphabet(intArgument(Routes.IndexArgument))
        Routes.NumberDetail -> Routes.number(intArgument(Routes.IndexArgument))
        Routes.GameDetail -> Routes.game(stringArgument(Routes.GameIdArgument))
        Routes.ListenCategoryDetail -> Routes.listen(stringArgument(Routes.ListenCategoryArgument))
        else -> destination.route ?: Routes.Home
    }
}

private fun NavBackStackEntry.intArgument(name: String): Int = arguments?.getInt(name) ?: 0

private fun NavBackStackEntry.stringArgument(name: String): String = arguments?.getString(name).orEmpty()

private fun NavHostController.navigateToRoute(route: String) {
    if (currentBackStackEntry.appRoute() == route) return
    navigate(route) {
        launchSingleTop = true
    }
}

private fun NavHostController.navigateToTopLevel(route: String) {
    if (currentBackStackEntry.appRoute() == route) return
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavHostController.navigateBackOrHome() {
    if (!navigateUp()) {
        navigateToTopLevel(Routes.Home)
    }
}

@Composable
private fun AlphabetGrid(onOpen: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.ScreenBackground),
        contentPadding = PaddingValues(18.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        itemsIndexed(LearningCatalog.alphabet) { index, lesson ->
            val shape = RoundedCornerShape(22.dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .clickable { onOpen(index) },
                shape = shape,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.08f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (index % 2 == 0) AppColors.CategoryBlue else AppColors.CategoryYellow,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(lesson.capitalImage),
                                contentDescription = lesson.letter,
                                modifier = Modifier.weight(1f),
                            )
                            Image(
                                painter = painterResource(lesson.smallImage),
                                contentDescription = lesson.letter.lowercase(),
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.alphabet_grid_letter_format, lesson.letter),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.TextContent,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun NumberGrid(onOpen: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.ScreenBackground),
        contentPadding = PaddingValues(18.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        itemsIndexed(LearningCatalog.numbers) { index, lesson ->
            val shape = RoundedCornerShape(22.dp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .clickable { onOpen(index) },
                shape = shape,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.08f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (index % 2 == 0) AppColors.CategoryBlue else AppColors.CategoryYellow,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(lesson.numberImage),
                            contentDescription = lesson.number,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                        )
                    }
                    Text(
                        text = stringResource(lesson.word),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.TextContent,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun AlphabetPager(initialPage: Int, onBack: () -> Unit) {
    val lessons = LearningCatalog.alphabet
    val pagerState = rememberPagerState(initialPage = initialPage) { lessons.size }
    val scope = rememberCoroutineScope()
    val play = rememberSoundPlayer()
    val currentLesson = lessons[pagerState.currentPage]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AppColors.SoftBackgroundTop, AppColors.SoftBackgroundMiddle, AppColors.SoftBackgroundBottom),
                ),
            )
            .padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LessonDetailHeader(
            title = stringResource(R.string.lesson_title_alphabet),
            progressLabel = stringResource(
                R.string.lesson_progress_format,
                currentLesson.letter,
                pagerState.currentPage + 1,
                lessons.size,
            ),
            instruction = stringResource(R.string.lesson_swipe_instruction),
            progress = (pagerState.currentPage + 1f) / lessons.size,
            onBack = onBack,
            onSound = { play(currentLesson.wordSound) },
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 2.dp),
            pageSpacing = 16.dp,
        ) { page ->
            AlphabetLessonPage(
                lesson = lessons[page],
                showTapHint = page == 0,
                play = play,
            )
        }
        LessonPagerControls(
            previousEnabled = pagerState.currentPage > 0,
            nextEnabled = pagerState.currentPage < lessons.lastIndex,
            onPrevious = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
            onNext = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
        )
    }
}

@Composable
private fun AlphabetLessonPage(
    lesson: AlphabetLesson,
    showTapHint: Boolean,
    play: (Int) -> Unit,
) {
    val description = stringResource(lesson.description)
    val exampleWord = description.substringAfter("for ").ifBlank { description }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceTranslucent),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            AlphabetVectorAccents(lesson = lesson)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AlphabetSoundCard(
                            image = lesson.capitalImage,
                            label = stringResource(R.string.alphabet_listen_to_letter, lesson.letter),
                            backgroundColor = AppColors.LetterCapitalBackground,
                            labelColor = AppColors.LetterCapitalLabel,
                            iconColor = AppColors.BrandBlue,
                            modifier = Modifier.weight(1f),
                            onClick = { play(lesson.letterSound) },
                        )
                        AlphabetSoundCard(
                            image = lesson.smallImage,
                            label = stringResource(R.string.alphabet_lowercase_letter, lesson.letter.lowercase()),
                            backgroundColor = AppColors.LetterLowerBackground,
                            labelColor = AppColors.LetterLowerLabel,
                            iconColor = AppColors.BrandGreen,
                            modifier = Modifier.weight(1f),
                            onClick = { play(lesson.letterSound) },
                        )
                    }
                }
                item {
                    AlphabetExampleHero(
                        image = lesson.exampleImage,
                        word = exampleWord,
                        showTapHint = showTapHint,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        onClick = { play(lesson.wordSound) },
                    )
                }
                item {
                    AlphabetPhrasePill(
                        letter = lesson.letter,
                        word = exampleWord,
                        onClick = { play(lesson.wordSound) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AlphabetSoundCard(
    image: Int,
    label: String,
    backgroundColor: Color,
    labelColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Card(
        modifier = modifier
            .height(140.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(9.dp),
                contentAlignment = Alignment.Center,
            ) {
                MiniSparkles(color = iconColor.copy(alpha = 0.30f))
                Image(
                    painter = painterResource(image),
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(labelColor)
                    .padding(horizontal = 4.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                SoundIconButton(
                    onClick = onClick,
                    contentColor = iconColor,
                    size = 32.dp,
                    iconSize = 16.dp,
                )
                ResizableText(
                    text = label,
                    modifier = Modifier.padding(start = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    minTextSize = 10.sp,
                    maxTextSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun AlphabetExampleHero(
    image: Int,
    word: String,
    showTapHint: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(34.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = AppColors.LetterHeroGlow.copy(alpha = 0.42f),
                radius = size.minDimension * 0.34f,
                center = center,
            )
            val colors = listOf(
                AppColors.BrandYellow,
                AppColors.BrandPink,
                Color(0xFF78D957),
                Color(0xFF74B9FF),
            )
            val points = listOf(
                Offset(size.width * 0.14f, size.height * 0.32f),
                Offset(size.width * 0.84f, size.height * 0.33f),
                Offset(size.width * 0.18f, size.height * 0.78f),
                Offset(size.width * 0.82f, size.height * 0.78f),
                Offset(size.width * 0.50f, size.height * 0.08f),
            )
            points.forEachIndexed { index, point ->
                drawCircle(
                    color = colors[index % colors.size].copy(alpha = 0.58f),
                    radius = size.minDimension * if (index == 4) 0.025f else 0.018f,
                    center = point,
                )
            }
        }
        Image(
            painter = painterResource(image),
            contentDescription = word,
            modifier = Modifier.fillMaxSize(0.88f),
            contentScale = ContentScale.Fit,
        )
        if (showTapHint) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 10.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(AppColors.LetterTapBubble)
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
            Text(
                text = stringResource(R.string.alphabet_tap_me),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = AppColors.LetterTapText,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun AlphabetPhrasePill(
    letter: String,
    word: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = AppColors.LetterPhraseSurface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = letter,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = AppColors.BrandRed,
            )
            Spacer(modifier = Modifier.width(6.dp))
            ResizableText(
                text = stringResource(R.string.alphabet_is_for),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Normal,
                color = AppColors.TextPrimary,
                minTextSize = 16.sp,
                maxTextSize = 20.sp,
            )
            Spacer(modifier = Modifier.width(4.dp))

            ResizableText(
                text = word,
                style = MaterialTheme.typography.displaySmall,
                minTextSize = 16.sp,
                maxTextSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = AppColors.BrandRed,
            )
        }
    }
}

@Composable
private fun MiniSparkles(color: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val sparkle = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.25f)
            lineTo(size.width * 0.21f, size.height * 0.33f)
            lineTo(size.width * 0.29f, size.height * 0.36f)
            lineTo(size.width * 0.21f, size.height * 0.39f)
            lineTo(size.width * 0.18f, size.height * 0.47f)
            lineTo(size.width * 0.15f, size.height * 0.39f)
            lineTo(size.width * 0.07f, size.height * 0.36f)
            lineTo(size.width * 0.15f, size.height * 0.33f)
            close()
        }
        drawPath(sparkle, color)
        drawCircle(
            color = color,
            radius = size.minDimension * 0.035f,
            center = Offset(size.width * 0.86f, size.height * 0.74f),
        )
    }
}

@Composable
private fun AlphabetVectorAccents(lesson: AlphabetLesson) {
    val colors = letterAccentColors(lesson.letter)
    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = colors.first.copy(alpha = 0.14f),
                radius = size.minDimension * 0.18f,
                center = Offset(size.width * 0.12f, size.height * 0.12f),
            )
            drawCircle(
                color = colors.second.copy(alpha = 0.18f),
                radius = size.minDimension * 0.11f,
                center = Offset(size.width * 0.88f, size.height * 0.24f),
            )
            drawRoundRect(
                color = colors.first.copy(alpha = 0.12f),
                topLeft = Offset(size.width * 0.70f, size.height * 0.70f),
                size = Size(size.width * 0.22f, size.width * 0.22f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(28f, 28f),
            )
            val leaf = Path().apply {
                moveTo(size.width * 0.10f, size.height * 0.78f)
                cubicTo(
                    size.width * 0.22f,
                    size.height * 0.66f,
                    size.width * 0.32f,
                    size.height * 0.76f,
                    size.width * 0.14f,
                    size.height * 0.88f,
                )
                cubicTo(
                    size.width * 0.08f,
                    size.height * 0.86f,
                    size.width * 0.07f,
                    size.height * 0.82f,
                    size.width * 0.10f,
                    size.height * 0.78f,
                )
                close()
            }
            drawPath(leaf, colors.second.copy(alpha = 0.18f))
        }
        Image(
            painter = painterResource(lesson.exampleImage),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 10.dp)
                .size(82.dp)
                .alpha(0.10f),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun LetterDetailCardImage(
    image: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
        )
        PlayIconButton(
            onClick = onClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            contentDescription = "Play word sound",
        )
    }
}

private fun letterDetailCard(letter: String): Int = when (letter.uppercase()) {
    "A" -> R.drawable.letter_detail_a
    "B" -> R.drawable.letter_detail_b
    "C" -> R.drawable.letter_detail_c
    "D" -> R.drawable.letter_detail_d
    "E" -> R.drawable.letter_detail_e
    "F" -> R.drawable.letter_detail_f
    "G" -> R.drawable.letter_detail_g
    "H" -> R.drawable.letter_detail_h
    "I" -> R.drawable.letter_detail_i
    "J" -> R.drawable.letter_detail_j
    "K" -> R.drawable.letter_detail_k
    "L" -> R.drawable.letter_detail_l
    "M" -> R.drawable.letter_detail_m
    "N" -> R.drawable.letter_detail_n
    "O" -> R.drawable.letter_detail_o
    "P" -> R.drawable.letter_detail_p
    "Q" -> R.drawable.letter_detail_q
    "R" -> R.drawable.letter_detail_r
    "S" -> R.drawable.letter_detail_s
    "T" -> R.drawable.letter_detail_t
    "U" -> R.drawable.letter_detail_u
    "V" -> R.drawable.letter_detail_v
    "W" -> R.drawable.letter_detail_w
    "X" -> R.drawable.letter_detail_x
    "Y" -> R.drawable.letter_detail_y
    "Z" -> R.drawable.letter_detail_z
    else -> R.drawable.letter_detail_a
}

private fun letterAccentColors(letter: String): Pair<Color, Color> = when (letter.firstOrNull()?.uppercaseChar()) {
    'A', 'O', 'P' -> Color(0xFFFF6B6B) to Color(0xFFFFC247)
    'B', 'F', 'T' -> Color(0xFFFFB84D) to Color(0xFF55C67A)
    'C', 'L', 'S' -> Color(0xFF62C77B) to Color(0xFF7CC8FF)
    'D', 'M', 'R' -> Color(0xFF7B61FF) to Color(0xFFFF7BB0)
    'E', 'I', 'U' -> Color(0xFF42B7FF) to Color(0xFFBBD7FF)
    else -> Color(0xFF6C8CFF) to Color(0xFFFF9BC8)
}
@Composable
private fun NumberPager(initialPage: Int, onBack: () -> Unit) {
    val lessons = LearningCatalog.numbers
    val pagerState = rememberPagerState(initialPage = initialPage) { lessons.size }
    val scope = rememberCoroutineScope()
    val play = rememberSoundPlayer()
    val currentLesson = lessons[pagerState.currentPage]
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AppColors.SoftBackgroundTop, AppColors.SoftBackgroundMiddle, AppColors.SoftBackgroundBottom),
                ),
            )
            .padding(horizontal = 18.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LessonDetailHeader(
            title = stringResource(R.string.lesson_title_numbers),
            progressLabel = stringResource(
                R.string.lesson_progress_format,
                currentLesson.number,
                pagerState.currentPage + 1,
                lessons.size,
            ),
            instruction = stringResource(R.string.lesson_swipe_instruction),
            progress = (pagerState.currentPage + 1f) / lessons.size,
            onBack = onBack,
            onSound = { play(currentLesson.wordSound) },
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 2.dp),
            pageSpacing = 16.dp,
        ) { page ->
            NumberLessonPage(lessons[page], play)
        }
        NumberPagerControls(
            previousEnabled = pagerState.currentPage > 0,
            nextEnabled = pagerState.currentPage < lessons.lastIndex,
            onPrevious = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
            onNext = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
        )
    }
}
@Composable
private fun NumberLessonPage(lesson: NumberLesson, play: (Int) -> Unit) {
    val numberWord = stringResource(lesson.word)
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.SurfaceTranslucent),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            DecorativeNumberBackground(
                value = lesson.number.toIntOrNull() ?: 0,
                numberImage = lesson.numberImage,
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        NumberSoundCard(
                            image = lesson.numberImage,
                            label = stringResource(R.string.number_listen_to_number, lesson.number),
                            backgroundColor = AppColors.LetterCapitalBackground,
                            labelColor = AppColors.LetterCapitalLabel,
                            iconColor = AppColors.BrandBlue,
                            modifier = Modifier.weight(1f),
                            onClick = { play(lesson.numberSound) },
                        )
                        NumberSoundCard(
                            image = lesson.countImage,
                            label = stringResource(R.string.number_count_label, numberWord),
                            backgroundColor = AppColors.LetterLowerBackground,
                            labelColor = AppColors.LetterLowerLabel,
                            iconColor = AppColors.BrandGreen,
                            modifier = Modifier.weight(1f),
                            onClick = { play(lesson.wordSound) },
                        )
                    }
                }
                item {
                    NumberExampleHero(
                        image = lesson.countImage,
                        word = numberWord,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        onClick = { play(lesson.wordSound) },
                    )
                }
                item {
                    NumberPhrasePill(
                        number = lesson.number,
                        word = numberWord,
                        onClick = { play(lesson.wordSound) },
                    )
                }
            }
        }
    }
}
@Composable
private fun NumberSoundCard(
    image: Int,
    label: String,
    backgroundColor: Color,
    labelColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Card(
        modifier = modifier
            .height(140.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(9.dp),
                contentAlignment = Alignment.Center,
            ) {
                MiniSparkles(color = iconColor.copy(alpha = 0.30f))
                Image(
                    painter = painterResource(image),
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(labelColor)
                    .padding(horizontal = 4.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                SoundIconButton(
                    onClick = onClick,
                    contentColor = iconColor,
                    size = 32.dp,
                    iconSize = 16.dp,
                )
                ResizableText(
                    text = label,
                    modifier = Modifier.padding(start = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    minTextSize = 10.sp,
                    maxTextSize = 13.sp,
                )
            }
        }
    }
}
@Composable
private fun NumberExampleHero(
    image: Int,
    word: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(34.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = AppColors.BrandBlue.copy(alpha = 0.10f),
                radius = size.minDimension * 0.42f,
                center = center,
            )
            val colors = listOf(
                AppColors.BrandYellow,
                AppColors.BrandPink,
                Color(0xFF78D957),
                Color(0xFF74B9FF),
            )
            val points = listOf(
                Offset(size.width * 0.15f, size.height * 0.22f),
                Offset(size.width * 0.85f, size.height * 0.24f),
                Offset(size.width * 0.18f, size.height * 0.82f),
                Offset(size.width * 0.82f, size.height * 0.80f),
                Offset(size.width * 0.50f, size.height * 0.08f),
            )
            points.forEachIndexed { index, point ->
                drawCircle(
                    color = colors[index % colors.size].copy(alpha = 0.58f),
                    radius = size.minDimension * if (index == 4) 0.025f else 0.018f,
                    center = point,
                )
            }
        }
        Image(
            painter = painterResource(image),
            contentDescription = word,
            modifier = Modifier.fillMaxSize(0.84f),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun NumberPhrasePill(
    number: String,
    word: String,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = AppColors.LetterPhraseSurface),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = AppColors.BrandBlue,
            )
            Spacer(modifier = Modifier.width(8.dp))
            ResizableText(
                text = stringResource(R.string.number_word_label, word),
                style = MaterialTheme.typography.displaySmall,
                minTextSize = 16.sp,
                maxTextSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun NumberDetailCardImage(
    image: Int,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = label,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
    }
}

private fun numberDetailCard(number: String): Int? = when (number) {
    "0" -> R.drawable.number_detail_0
    "1" -> R.drawable.number_detail_1
    "2" -> R.drawable.number_detail_2
    "3" -> R.drawable.number_detail_3
    "4" -> R.drawable.number_detail_4
    "5" -> R.drawable.number_detail_5
    "6" -> R.drawable.number_detail_6
    "7" -> R.drawable.number_detail_7
    "8" -> R.drawable.number_detail_8
    "9" -> R.drawable.number_detail_9
    "10" -> R.drawable.number_detail_10
    "11" -> R.drawable.number_detail_11
    "12" -> R.drawable.number_detail_12
    "13" -> R.drawable.number_detail_13
    "14" -> R.drawable.number_detail_14
    "15" -> R.drawable.number_detail_15
    "16" -> R.drawable.number_detail_16
    "17" -> R.drawable.number_detail_17
    "18" -> R.drawable.number_detail_18
    "19" -> R.drawable.number_detail_19
    "20" -> R.drawable.number_detail_20
    else -> null
}
@Composable
private fun DecorativeNumberBackground(
    value: Int,
    numberImage: Int,
) {
    val colors = listOf(
        AppColors.BrandPink,
        AppColors.BrandYellow,
        Color(0xFF78D957),
        Color(0xFF74B9FF),
        Color(0xFF9B7BFF),
    )
    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = AppColors.BrandBlue.copy(alpha = 0.07f),
                radius = size.minDimension * 0.30f,
                center = Offset(size.width * 0.52f, size.height * 0.54f),
            )
            val count = value.coerceAtLeast(1)
            val columns = if (count <= 10) 5 else 7
            val dotSize = size.minDimension * if (count <= 10) 0.034f else 0.026f
            val startX = size.width * 0.12f
            val startY = size.height * 0.13f
            val gapX = size.width * 0.11f
            val gapY = size.height * 0.075f
            repeat(count.coerceAtMost(20)) { index ->
                val col = index % columns
                val row = index / columns
                drawCircle(
                    color = colors[index % colors.size].copy(alpha = 0.32f),
                    radius = dotSize,
                    center = Offset(startX + col * gapX, startY + row * gapY),
                )
            }
            if (value == 0) {
                drawCircle(
                    color = AppColors.BrandPink.copy(alpha = 0.28f),
                    radius = size.minDimension * 0.055f,
                    center = Offset(size.width * 0.18f, size.height * 0.18f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = size.minDimension * 0.014f),
                )
            }
            val sparkle = Path().apply {
                moveTo(size.width * 0.84f, size.height * 0.15f)
                lineTo(size.width * 0.88f, size.height * 0.23f)
                lineTo(size.width * 0.96f, size.height * 0.27f)
                lineTo(size.width * 0.88f, size.height * 0.31f)
                lineTo(size.width * 0.84f, size.height * 0.39f)
                lineTo(size.width * 0.80f, size.height * 0.31f)
                lineTo(size.width * 0.72f, size.height * 0.27f)
                lineTo(size.width * 0.80f, size.height * 0.23f)
                close()
            }
            drawPath(sparkle, AppColors.BrandYellow.copy(alpha = 0.32f))
        }
        Image(
            painter = painterResource(numberImage),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 18.dp, top = 18.dp)
                .size(88.dp)
                .alpha(0.10f),
            contentScale = ContentScale.Fit,
        )
    }
}
@Composable
private fun NumberWordBadge(word: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(34.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth(0.76f)
            .height(86.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = AppColors.CategoryPink),
        elevation = CardDefaults.cardElevation(8.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = AppColors.BrandPink,
                textAlign = TextAlign.Center,
            )
        }
    }
}
@Composable
private fun NumberPagerControls(
    previousEnabled: Boolean,
    nextEnabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    PagerControls(
        previousEnabled = previousEnabled,
        nextEnabled = nextEnabled,
        onPrevious = onPrevious,
        onNext = onNext,
        previousContentColor = AppColors.BrandBlue,
        nextContainerColor = AppColors.BrandBlue,
        topPadding = 6.dp,
    )
}
@Composable
private fun LessonDetailHeader(
    title: String,
    progressLabel: String,
    instruction: String,
    progress: Float,
    onBack: () -> Unit,
    onSound: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape),
        ) {
            ChevronLeftIcon(color = AppColors.BrandBlue, size = 28.dp)
        }
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary,
        )
        SoundIconButton(
            onClick = onSound,
            contentDescription = "Play sound",
        )
    }
    ResizableText(
        text = instruction,
        style = MaterialTheme.typography.bodyLarge,
        color = AppColors.TextSecondary,
        fontWeight = FontWeight.Normal,
    )
    StarProgressBar(
        progress = progress,
        label = progressLabel.replace(" of ", "/"),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
    )
}

@Composable
private fun LessonPictureCard(
    image: Int,
    label: String,
    backgroundColor: Color,
    labelColor: Color,
    modifier: Modifier = Modifier,
    showPlay: Boolean = false,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)
    Card(
        modifier = modifier
            .height(210.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(1.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(image),
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
                if (showPlay) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0875DA)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "▶",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(labelColor)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF11194D),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ExtraWordsCard(first: String, second: String) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(
                text = first,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0875DA),
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(34.dp)
                    .background(Color(0xFFE3E6F0)),
            )
            Text(
                text = second,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0875DA),
            )
        }
    }
}

@Composable
private fun LessonPagerControls(
    previousEnabled: Boolean,
    nextEnabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    PagerControls(
        previousEnabled = previousEnabled,
        nextEnabled = nextEnabled,
        onPrevious = onPrevious,
        onNext = onNext,
        previousContainerColor = Color(0xFFEFEAFA),
        previousContentColor = Color(0xFF5C6385),
        previousDisabledContainerColor = Color(0xFFEFEAFA),
        disabledContentColor = Color(0xFF8B91AA),
        nextContainerColor = Color(0xFF0875DA),
        topPadding = 12.dp,
        horizontalSpacing = 18.dp,
        cornerRadius = 22.dp,
    )
}

@Composable
private fun LessonImage(image: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(138.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
    ) {
        Image(
            painterResource(image),
            null,
            Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentScale = ContentScale.Fit,
        )
    }
}
private data class WeekCardStyle(
    val background: Color,
    val wave: Color,
    val text: Color,
)
private val weekCardStyles = listOf(
    WeekCardStyle(Color(0xFFFFE4EC), Color(0xFFFF9DB8), Color(0xFFD83F5E)),
    WeekCardStyle(Color(0xFFE2F4FF), Color(0xFF8AD2FF), Color(0xFF0E6FDB)),
    WeekCardStyle(Color(0xFFFFF4CA), Color(0xFFFFD95F), Color(0xFFFF8900)),
    WeekCardStyle(Color(0xFFEDE0FF), Color(0xFFB989F3), Color(0xFF722AD3)),
    WeekCardStyle(Color(0xFFE4F8D8), Color(0xFF99E66E), Color(0xFF49B72D)),
    WeekCardStyle(Color(0xFFFFE4CC), Color(0xFFFFA150), Color(0xFFF06416)),
    WeekCardStyle(Color(0xFFFFE4F0), Color(0xFFFF92C6), Color(0xFFE52D87)),
)
@Composable
private fun WeeksScreen(
    onBack: () -> Unit,
    selectionViewModel: SelectionViewModel = viewModel(),
) {
    val lessons = LearningCatalog.weeks
    val play = rememberSoundPlayer()
    val storedSelectedIndex by selectionViewModel.selectedIndex.collectAsStateWithLifecycle()
    val selectedIndex = storedSelectedIndex.coerceIn(lessons.indices)
    val selectedLesson = lessons[selectedIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFF7FCFF), Color(0xFFF4FAFF)),
                ),
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WeekHeader(
            selectedIndex = selectedIndex,
            total = lessons.size,
            onBack = onBack,
            onSound = { play(selectedLesson.sound) },
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 12.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            lazyItemsIndexed(lessons.chunked(2)) { rowIndex, rowLessons ->
                val baseIndex = rowIndex * 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                ) {
                    rowLessons.forEachIndexed { offset, lesson ->
                        val index = baseIndex + offset
                        WeekDayCard(
                            lesson = lesson,
                            style = weekCardStyles[index % weekCardStyles.size],
                            selected = selectedIndex == index,
                            modifier = if (rowLessons.size == 1) {
                                Modifier.fillMaxWidth(0.68f)
                            } else {
                                Modifier.weight(1f)
                            },
                            onClick = {
                                selectionViewModel.select(index, lessons.lastIndex)
                                play(lesson.sound)
                            },
                        )
                    }
                }
            }
        }
        CategoryPagerControls(
            previousEnabled = selectedIndex > 0,
            nextEnabled = selectedIndex < lessons.lastIndex,
            onPrevious = {
                val previousIndex = (selectedIndex - 1).coerceAtLeast(0)
                selectionViewModel.select(previousIndex, lessons.lastIndex)
                play(lessons[previousIndex].sound)
            },
            onNext = {
                val nextIndex = (selectedIndex + 1).coerceAtMost(lessons.lastIndex)
                selectionViewModel.select(nextIndex, lessons.lastIndex)
                play(lessons[nextIndex].sound)
            },
        )
    }
}
@Composable
private fun WeekHeader(
    selectedIndex: Int,
    total: Int,
    onBack: () -> Unit,
    onSound: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircleTextButton("‹", onClick = onBack)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Days of the Week",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF11194D),
                textAlign = TextAlign.Center,
            )
        }
        SoundIconButton(onClick = onSound, size = 66.dp, iconSize = 30.dp)
    }
    StarProgressBar(
        progress = (selectedIndex + 1f) / total,
        label = "${selectedIndex + 1}/$total",
        modifier = Modifier.fillMaxWidth(),
    )
    Text(
        text = "Tap on a day to hear it",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4D5A83),
        modifier = Modifier.padding(top = 22.dp),
    )
}
@Composable
private fun WeekDayCard(
    lesson: LessonItem,
    style: WeekCardStyle,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .height(132.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = style.background),
        elevation = CardDefaults.cardElevation(if (selected) 9.dp else 3.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(34.dp)
                    .background(style.wave.copy(alpha = 0.62f)),
            )
            Text(
                text = "★",
                color = Color.White.copy(alpha = 0.55f),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(lesson.image),
                        contentDescription = lesson.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
                Text(
                    text = lesson.title,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = style.text,
                    textAlign = TextAlign.Center,
                )
                SoundIconButton(onClick = onClick, size = 52.dp, iconSize = 26.dp)
            }
        }
    }
}
@Composable
private fun CategoryPagerControls(
    previousEnabled: Boolean,
    nextEnabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    PagerControls(
        previousEnabled = previousEnabled,
        nextEnabled = nextEnabled,
        onPrevious = onPrevious,
        onNext = onNext,
        previousContentColor = Color(0xFF0875DA),
        nextContainerColor = Color(0xFF0875DA),
        disabledContentColor = Color(0xFF8FA9C8),
        topPadding = 8.dp,
    )
}
private data class ColorBarStyle(
    val start: Color,
    val end: Color,
    val sound: Color,
)
private val colorBarStyles = listOf(
    ColorBarStyle(Color(0xFFFF7381), Color(0xFFFF0818), Color(0xFFFF4052)),
    ColorBarStyle(Color(0xFFFFB24A), Color(0xFFFF7A00), Color(0xFFFF7900)),
    ColorBarStyle(Color(0xFFFFF85A), Color(0xFFFFD500), Color(0xFFFFC800)),
    ColorBarStyle(Color(0xFF74F06D), Color(0xFF03C735), Color(0xFF0ACB42)),
    ColorBarStyle(Color(0xFF61C9FF), Color(0xFF0067F3), Color(0xFF0875DA)),
    ColorBarStyle(Color(0xFF8D79E8), Color(0xFF2F2ABF), Color(0xFF4532D6)),
    ColorBarStyle(Color(0xFFE06BFF), Color(0xFFA919EA), Color(0xFFB625EA)),
)
@Composable
private fun ColorsScreen(
    onBack: () -> Unit,
    selectionViewModel: SelectionViewModel = viewModel(),
) {
    val lessons = LearningCatalog.colours
    val play = rememberSoundPlayer()
    val storedSelectedIndex by selectionViewModel.selectedIndex.collectAsStateWithLifecycle()
    val selectedIndex = storedSelectedIndex.coerceIn(lessons.indices)
    val selectedLesson = lessons[selectedIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFFFFF), Color(0xFFF7FCFF), Color(0xFFF5FBFF)),
                ),
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ColorsHeader(
            selectedIndex = selectedIndex,
            total = lessons.size,
            onBack = onBack,
            onSound = { play(selectedLesson.sound) },
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 14.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            lazyItemsIndexed(lessons) { index, lesson ->
                ColorBarCard(
                    lesson = lesson,
                    style = colorBarStyles[index % colorBarStyles.size],
                    selected = selectedIndex == index,
                    onClick = {
                        selectionViewModel.select(index, lessons.lastIndex)
                        play(lesson.sound)
                    },
                )
            }
        }
        CategoryPagerControls(
            previousEnabled = selectedIndex > 0,
            nextEnabled = selectedIndex < lessons.lastIndex,
            onPrevious = {
                val previousIndex = (selectedIndex - 1).coerceAtLeast(0)
                selectionViewModel.select(previousIndex, lessons.lastIndex)
                play(lessons[previousIndex].sound)
            },
            onNext = {
                val nextIndex = (selectedIndex + 1).coerceAtMost(lessons.lastIndex)
                selectionViewModel.select(nextIndex, lessons.lastIndex)
                play(lessons[nextIndex].sound)
            },
        )
    }
}
@Composable
private fun ColorsHeader(
    selectedIndex: Int,
    total: Int,
    onBack: () -> Unit,
    onSound: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        CircleTextButton("‹", onClick = onBack)
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RainbowTitle("Colors")
        }
        SoundIconButton(onClick = onSound, size = 66.dp, iconSize = 30.dp)
    }
    StarProgressBar(
        progress = (selectedIndex + 1f) / total,
        label = "${selectedIndex + 1}/$total",
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
    )
    Text(
        text = "Tap on a color to hear it",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4D5A83),
        modifier = Modifier.padding(top = 22.dp),
    )
}
@Composable
private fun RainbowTitle(title: String) {
    val titleColors = listOf(
        Color(0xFFFF3A1E),
        Color(0xFFFFB61E),
        Color(0xFF9BEB45),
        Color(0xFF19C6C9),
        Color(0xFF1C70F2),
        Color(0xFFA83CFF),
    )
    Row(horizontalArrangement = Arrangement.Center) {
        title.forEachIndexed { index, character ->
            Text(
                text = character.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                color = titleColors[index % titleColors.size],
            )
        }
    }
}
@Composable
private fun ColorBarCard(
    lesson: LessonItem,
    style: ColorBarStyle,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = style.start),
        elevation = CardDefaults.cardElevation(if (selected) 9.dp else 3.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(style.start, style.end))),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(Color.White.copy(alpha = 0.12f)),
            )
            Image(
                painter = painterResource(lesson.image),
                contentDescription = lesson.title,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(86.dp)
                    .padding(start = 14.dp, top = 8.dp, bottom = 8.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                text = lesson.title.uppercase(),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            SoundIconButton(
                onClick = onClick,
                contentColor = style.sound,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 18.dp),
                size = 52.dp,
                iconSize = 28.dp,
            )
        }
    }
}
@Composable
private fun LessonList(lessons: List<LessonItem>) {
    val play = rememberSoundPlayer()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FBFF)),
        contentPadding = PaddingValues(18.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        items(lessons) { lesson ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { play(lesson.sound) },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEAF6FF)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(lesson.image),
                            contentDescription = lesson.title,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentScale = ContentScale.Fit,
                        )
                    }
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF243745),
                        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                    )
                }
            }
        }
    }
}
@Composable
private fun rememberSoundPlayer(): (Int) -> Unit {
    val context = LocalContext.current
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    DisposableEffect(Unit) {
        onDispose {
            player?.release()
            player = null
        }
    }
    return { sound ->
        runCatching {
            player?.release()
            player = MediaPlayer.create(context.applicationContext, sound)?.apply {
                setOnCompletionListener {
                    it.release()
                    player = null
                }
                setOnErrorListener { mediaPlayer, _, _ ->
                    mediaPlayer.release()
                    player = null
                    true
                }
                start()
            }
        }.onFailure {
            runCatching { player?.release() }
            player = null
        }
    }
}
@Composable
private fun routeTitle(route: String): String = when {
    route == Routes.Home -> stringResource(R.string.app_name)
    route.startsWith(Routes.Alphabet) -> stringResource(R.string.lesson_title_alphabet)
    route.startsWith(Routes.Numbers) -> stringResource(R.string.lesson_title_numbers)
    route == Routes.Weeks -> stringResource(R.string.home_category_days)
    route == Routes.Months -> stringResource(R.string.home_category_months)
    route == Routes.Seasons -> stringResource(R.string.home_category_seasons)
    route == Routes.Colours -> stringResource(R.string.route_rainbow_colours)
    route == Routes.Animals -> stringResource(R.string.home_category_animals)
    route == Routes.Fishes -> stringResource(R.string.home_category_fishes)
    route == Routes.Foods -> stringResource(R.string.home_category_foods)
    route == Routes.Flowers -> stringResource(R.string.home_category_flowers)
    route == Routes.Birds -> stringResource(R.string.home_category_birds)
    route == Routes.Furniture -> stringResource(R.string.home_category_furniture)
    route == Routes.Fruits -> stringResource(R.string.home_category_fruits)
    route == Routes.Vegetables -> stringResource(R.string.home_category_vegetables)
    route == Routes.Shapes -> stringResource(R.string.home_category_shapes)
    route == Routes.Games -> stringResource(R.string.nav_mini_games)
    route == Routes.Listen -> stringResource(R.string.nav_listen)
    route.startsWith("${Routes.Listen}/") -> ListenCategory.fromRouteId(route.substringAfter('/'))?.title
        ?: stringResource(R.string.nav_listen)
    route == Routes.game("trace") -> stringResource(R.string.game_trace_letter)
    route == Routes.game("letter") -> stringResource(R.string.game_letter_match)
    route == Routes.game("object-letter") -> stringResource(R.string.game_object_letter)
    route == Routes.game("drag") -> stringResource(R.string.game_drag_drop)
    route == Routes.game("quiz") -> stringResource(R.string.game_quiz_mode)
    route == Routes.game("find") -> stringResource(R.string.game_find_object)
    route == Routes.game("number") -> stringResource(R.string.game_number_match)
    route == Routes.game("color") -> stringResource(R.string.game_color_match)
    route == Routes.game("memory") -> stringResource(R.string.game_memory_match)
    route == Routes.game("picture-match") -> stringResource(R.string.game_picture_match)
    route == Routes.game("guess-word") -> stringResource(R.string.game_guess_word)
    route == Routes.game("word-lock") -> stringResource(R.string.game_word_lock)
    route == Routes.game("word-puzzle") -> stringResource(R.string.game_word_puzzle)
    route == Routes.About -> stringResource(R.string.nav_settings)
    else -> stringResource(R.string.app_name)
}
fun Context.openUri(uri: String) {
    runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri))) }
        .onFailure { Toast.makeText(this, getString(R.string.toast_no_compatible_app), Toast.LENGTH_SHORT).show() }
}
private fun Context.performExternalAction(action: ExternalAction) {
    when (action) {
        ExternalAction.MoreApps -> openUri("https://play.google.com/store/apps/developer?id=Maruf+Alam")
        ExternalAction.PrivacyPolicy -> openUri("https://childrenbooksprivacypolicy.blogspot.com/2026/07/children-books-privacy-policy-effective.html")
        ExternalAction.RateApp -> openUri("https://play.google.com/store/apps/details?id=$packageName")
        ExternalAction.ShareApp -> shareApp()
    }
}
private fun Context.shareApp() {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=$packageName",
        )
    }
    startActivity(Intent.createChooser(intent, getString(R.string.share_children_books)))
}
