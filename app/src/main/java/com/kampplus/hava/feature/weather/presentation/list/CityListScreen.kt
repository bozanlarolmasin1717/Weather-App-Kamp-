package com.kampplus.hava.feature.weather.presentation.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.component.EmptyView
import com.kampplus.hava.core.ui.component.ErrorView
import com.kampplus.hava.core.ui.component.ShimmerList
import com.kampplus.hava.core.ui.state.UiState
import com.kampplus.hava.core.ui.text.UiText
import com.kampplus.hava.core.ui.theme.HavaTheme
import com.kampplus.hava.feature.weather.domain.policy.WeatherCondition
import com.kampplus.hava.feature.weather.presentation.list.component.CitySearchField
import com.kampplus.hava.feature.weather.presentation.list.component.CityWeatherHeroCard
import com.kampplus.hava.feature.weather.presentation.model.CityWeatherUiModel
import com.kampplus.hava.feature.weather.presentation.model.weatherGradientColors
import com.kampplus.hava.feature.weather.presentation.outfit.WeatherAssistantAvatar
import com.kampplus.hava.feature.weather.presentation.sound.WeatherSoundManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    uiState: CityListUiState,
    onQueryChange: (String) -> Unit,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    onFavoritesClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val soundManager = remember { WeatherSoundManager(context) }
    var isSoundEnabled by remember { mutableStateOf(soundManager.isEnabled) }

    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

    val currentItems = (uiState.content as? UiState.Success)?.data.orEmpty()
    val initialCondition = currentItems.firstOrNull()?.condition ?: WeatherCondition.Unknown
    var activeCondition by remember { mutableStateOf(initialCondition) }

    LaunchedEffect(currentItems) {
        if (currentItems.isNotEmpty() && activeCondition == WeatherCondition.Unknown) {
            activeCondition = currentItems.first().condition
        }
    }

    LaunchedEffect(activeCondition, isSoundEnabled) {
        if (isSoundEnabled) {
            soundManager.play(activeCondition)
        } else {
            soundManager.stop()
        }
    }

    val targetColors = weatherGradientColors(activeCondition)
    val topColor by animateColorAsState(
        targetValue = targetColors.topColor,
        animationSpec = tween(durationMillis = 600),
        label = "bgTopColor"
    )
    val bottomColor by animateColorAsState(
        targetValue = targetColors.bottomColor,
        animationSpec = tween(durationMillis = 600),
        label = "bgBottomColor"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(topColor, bottomColor)))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.list_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                isSoundEnabled = soundManager.toggleSound(activeCondition)
                            }
                        ) {
                            Text(
                                text = if (isSoundEnabled) "🔊" else "🔇",
                                fontSize = 20.sp
                            )
                        }

                        IconButton(onClick = onFavoritesClick) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = "Favori Şehirler",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                CitySearchField(
                    query = uiState.query,
                    onQueryChange = onQueryChange,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    ListContent(
                        uiState = uiState,
                        onCityClick = onCityClick,
                        onFavoriteClick = onFavoriteClick,
                        onRetry = onRetry,
                        onPageChange = { condition ->
                            activeCondition = condition
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ListContent(
    uiState: CityListUiState,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onRetry: () -> Unit,
    onPageChange: (WeatherCondition) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val content = uiState.content) {
            UiState.Loading -> ShimmerList()
            UiState.Empty -> EmptyView(
                icon = Icons.Filled.Search,
                title = stringResource(R.string.list_empty_title),
                message = if (uiState.isSearching) {
                    stringResource(R.string.search_empty_message, uiState.query.trim())
                } else {
                    stringResource(R.string.list_empty_message)
                }
            )
            is UiState.Error -> ErrorView(message = content.message.asString(), onRetry = onRetry)
            is UiState.Success -> CityPagerContent(
                items = content.data,
                onCityClick = onCityClick,
                onFavoriteClick = onFavoriteClick,
                onPageChange = onPageChange
            )
        }
    }
}

@Composable
private fun CityPagerContent(
    items: List<CityWeatherUiModel>,
    onCityClick: (Long) -> Unit,
    onFavoriteClick: (Long) -> Unit,
    onPageChange: (WeatherCondition) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    val currentItem = items.getOrNull(pagerState.currentPage)

    LaunchedEffect(pagerState.currentPage, items) {
        val condition = currentItem?.condition ?: WeatherCondition.Unknown
        onPageChange(condition)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${items.size}",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.White),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                if (currentItem?.isFavorite == true) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0x33EF4444)
                    ) {
                        Text(
                            text = "❤️ Favori",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 24.dp),
                pageSpacing = 16.dp
            ) { page ->
                val cityItem = items.getOrNull(page)
                if (cityItem != null) {
                    CityWeatherHeroCard(
                        item = cityItem,
                        onClick = { onCityClick(cityItem.cityId) },
                        onFavoriteClick = { onFavoriteClick(cityItem.cityId) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val windowSize = 8
                val start = (pagerState.currentPage - windowSize / 2).coerceAtLeast(0)
                val end = (start + windowSize).coerceAtMost(items.size)

                for (index in start until end) {
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color.White else Color.White.copy(alpha = 0.35f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(items, key = { _, city -> city.cityId }) { index, city ->
                    val isSelected = pagerState.currentPage == index
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (city.isFavorite) {
                                Text(text = "⭐", fontSize = 11.sp)
                            }
                            Text(
                                text = city.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        currentItem?.let { item ->
            WeatherAssistantAvatar(
                condition = item.condition,
                temperatureC = item.temperatureC,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CityListScreenPreview() {
    HavaTheme {
        CityListScreen(
            uiState = CityListUiState(
                content = UiState.Success(
                    List(5) { index ->
                        CityWeatherUiModel(
                            cityId = index.toLong(),
                            title = "İstanbul",
                            subtitle = "İstanbul, Türkiye",
                            temperatureText = "2$index°",
                            temperatureC = 20.0 + index,
                            conditionEmoji = "⛅",
                            conditionLabel = UiText.Dynamic("Parçalı bulutlu")
                        )
                    }
                )
            ),
            onQueryChange = {},
            onCityClick = {},
            onFavoriteClick = {},
            onRetry = {},
            onRefresh = {},
            onFavoritesClick = {}
        )
    }
}
