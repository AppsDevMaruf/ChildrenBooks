package com.childrenbooks.ui.home

import androidx.compose.foundation.Image
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.R
import com.childrenbooks.ui.Routes
import com.childrenbooks.ui.components.AppRoundIconButton
import com.childrenbooks.ui.components.ChevronRightIcon
import com.childrenbooks.ui.components.ResizableText
import com.childrenbooks.ui.theme.AppColors
import kotlinx.coroutines.delay

private data class HomeCategory(
    @param:StringRes val titleRes: Int,
    val route: String,
    val image: Int,
    val color: Color,
)

private val categories = listOf(
    HomeCategory(R.string.home_category_abc_letters, Routes.Alphabet, R.drawable.home_abc, AppColors.CategoryBlue),
    HomeCategory(R.string.home_category_numbers, Routes.Numbers, R.drawable.home_numbers, AppColors.CategoryYellow),
    HomeCategory(R.string.home_category_days, Routes.Weeks, R.drawable.home_days, AppColors.CategoryPurple),
    HomeCategory(R.string.home_category_months, Routes.Months, R.drawable.home_months, AppColors.CategoryGreen),
    HomeCategory(R.string.home_category_seasons, Routes.Seasons, R.drawable.home_seasons, AppColors.CategoryRed),
    HomeCategory(R.string.home_category_rainbow, Routes.Colours, R.drawable.home_rainbow, AppColors.CategoryBlue),
    HomeCategory(R.string.home_category_animals, Routes.Animals, R.drawable.home_animals, AppColors.CategoryYellow),
    HomeCategory(R.string.home_category_fishes, Routes.Fishes, R.drawable.home_fishes, AppColors.CategorySky),
    HomeCategory(R.string.home_category_birds, Routes.Birds, R.drawable.home_birds, AppColors.CategoryPurple),
    HomeCategory(R.string.home_category_flowers, Routes.Flowers, R.drawable.home_flowers, AppColors.CategoryPink),
    HomeCategory(R.string.home_category_foods, Routes.Foods, R.drawable.home_foods, AppColors.CategoryOrange),
    HomeCategory(R.string.home_category_fruits, Routes.Fruits, R.drawable.home_fruits, AppColors.CategoryOrange),
    HomeCategory(R.string.home_category_vegetables, Routes.Vegetables, R.drawable.home_vegetables, AppColors.CategoryGreen),
    HomeCategory(R.string.home_category_furniture, Routes.Furniture, R.drawable.home_furniture, AppColors.CategoryFurniture),
    HomeCategory(R.string.home_category_shapes, Routes.Shapes, R.drawable.home_shapes, AppColors.CategoryPurple),
)

private val homeSliderImages = listOf(
    R.drawable.home_slider_1,
    R.drawable.home_slider_2,
    R.drawable.home_slider_3,
)

private const val HomeSliderAspectRatio = 1234f / 488f

private data class HomeNavDestination(
    val icon: ImageVector,
    @param:StringRes val labelRes: Int,
    val route: String,
)

private val homeNavDestinations = listOf(
    HomeNavDestination(Icons.Rounded.Home, R.string.nav_home, Routes.Home),
    HomeNavDestination(Icons.Rounded.AutoStories, R.string.nav_lessons, Routes.Alphabet),
    HomeNavDestination(Icons.Rounded.SportsEsports, R.string.nav_play, Routes.Games),
    HomeNavDestination(Icons.Rounded.LibraryMusic, R.string.nav_listen, Routes.Listen),
    HomeNavDestination(Icons.Rounded.Settings, R.string.nav_settings, Routes.About),
)

@Composable
fun HomeScreen(
    onOpen: (String) -> Unit,
    homeViewModel: HomeViewModel = viewModel(),
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    HomeTopBar(
        onOpenSearch = homeViewModel::openSearch,
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.ScreenBackground),
        contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 0.dp, bottom = 112.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {

        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            WelcomeBanner()
        }
        item(span = {GridItemSpan(maxLineSpan) }) {
            SectionHeader(onViewAll = { onOpen(Routes.Alphabet) })
        }
        itemsIndexed(categories) { _, category ->
            CategoryCard(category = category, onClick = { onOpen(category.route) })
        }
        item {
            CategoryCard(
                category = HomeCategory(
                    titleRes = R.string.nav_play,
                    route = Routes.Games,
                    image = R.drawable.home_play,
                    color = AppColors.CategoryPurple,
                ),
                onClick = { onOpen(Routes.Games) },
            )
        }
    }

    if (uiState.isSearchVisible) {
        HomeSearchDialog(
            query = uiState.searchQuery,
            onQueryChange = homeViewModel::updateSearchQuery,
            onDismiss = homeViewModel::closeSearch,
            onOpen = { route ->
                homeViewModel.closeSearch()
                onOpen(route)
            },
        )
    }
}

@Composable
private fun HomeTopBar(
    onOpenSearch: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ResizableText(
            text = stringResource(R.string.app_name),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = AppColors.TextHomeTitle,
        )
        AppRoundIconButton(
            icon = Icons.Rounded.Search,
            contentDescription = stringResource(R.string.home_search_title),
            onClick = onOpenSearch,
        )
    }
}

@Composable
private fun HomeSearchDialog(
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onOpen: (String) -> Unit,
) {
    val searchableItems = categories + listOf(
        HomeCategory(R.string.home_category_play_learn, Routes.Games, R.drawable.play_and_learn, AppColors.CategoryPurple),
        HomeCategory(R.string.nav_listen_enjoy, Routes.Listen, R.drawable.play_and_learn, AppColors.CategoryYellow),
        HomeCategory(R.string.nav_settings, Routes.About, R.drawable.ic_info, AppColors.CategoryBlue),
    )
    val filteredItems = searchableItems.filter {
        query.isBlank() || stringResource(it.titleRes).contains(query.trim(), ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.home_search_title), fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    label = { Text(stringResource(R.string.home_search_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                LazyColumn(
                    modifier = Modifier.height(330.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(filteredItems, key = { it.route }) { item ->
                        SearchResultRow(item = item, onClick = { onOpen(item.route) })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
            }
        },
    )
}

@Composable
private fun SearchResultRow(item: HomeCategory, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(item.color)
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(item.image),
            contentDescription = stringResource(item.titleRes),
            modifier = Modifier.size(44.dp),
            contentScale = ContentScale.Fit,
        )
        Text(
            text = stringResource(item.titleRes),
            color = AppColors.TextHomeTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(1f),
        )
        ChevronRightIcon(color = AppColors.BrandPurple, size = 18.dp)
    }
}

@Composable
private fun WelcomeBanner() {
    val pagerState = rememberPagerState { homeSliderImages.size }

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3500)
            if (!pagerState.isScrollInProgress) {
                val nextPage = (pagerState.settledPage + 1) % homeSliderImages.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(HomeSliderAspectRatio)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        HorizontalPager(state = pagerState) { page ->
            Image(
                painter = painterResource(homeSliderImages[page]),
                contentDescription = "Home banner ${page + 1}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds,
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            repeat(3) { index ->
                Box(
                    Modifier
                        .size(if (pagerState.settledPage == index) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = if (pagerState.settledPage == index) 0.95f else 0.35f)),
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(onViewAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.home_section_learning),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = AppColors.TextHomeTitle,
            modifier = Modifier.weight(1f),
        )
        Card(
            onClick = onViewAll,
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.home_view_all), color = AppColors.BrandPurple, fontWeight = FontWeight.Bold)
                ChevronRightIcon(
                    modifier = Modifier.padding(start = 6.dp),
                    color = AppColors.BrandPurple,
                    size = 18.dp,
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(category: HomeCategory, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(98.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(category.color),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(category.image),
                    contentDescription = stringResource(category.titleRes),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Fit,
                )
            }
            Text(
                text = stringResource(category.titleRes),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 7.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AppColors.TextHomeTitle,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
fun HomeBottomBar(
    currentRoute: String,
    onOpen: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(74.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(10.dp),
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            homeNavDestinations.forEach { destination ->
                HomeNavItem(
                    icon = destination.icon,
                    label = stringResource(destination.labelRes),
                    selected = destination.isSelected(currentRoute),
                    modifier = Modifier.weight(1f),
                    onClick = { onOpen(destination.route) },
                )
            }
        }
    }
}

private fun HomeNavDestination.isSelected(currentRoute: String): Boolean = when (route) {
    Routes.Home -> currentRoute == Routes.Home
    Routes.Alphabet -> currentRoute == Routes.Alphabet
    Routes.Games -> currentRoute == Routes.Games || currentRoute.startsWith("${Routes.GamePrefix}/")
    Routes.Listen -> currentRoute == Routes.Listen || currentRoute.startsWith("${Routes.Listen}/")
    Routes.About -> currentRoute == Routes.About
    else -> currentRoute == route
}

@Composable
private fun HomeNavItem(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit,
) {
    val activeColor = AppColors.BrandPurple
    val inactiveColor = AppColors.TextMuted
    val contentColor = if (selected) activeColor else inactiveColor

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(if (selected) AppColors.CategoryPurple else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(23.dp),
        )
        Text(
            label,
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
            modifier = Modifier.padding(top = 3.dp),
            maxLines = 1,
        )
    }
}
