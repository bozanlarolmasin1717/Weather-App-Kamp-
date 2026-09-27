package com.kampplus.hava.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.kampplus.hava.R
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val route: Any,
    val routeClass: KClass<*>,
    val icon: ImageVector,
    @param:StringRes
    val labelRes: Int
) {
    List(
        ListDestination,
        ListDestination::class,
        Icons.Filled.Home,
        R.string.nav_weather
    ),

    Favorites(
        FavoritesDestination,
        FavoritesDestination::class,
        Icons.Filled.Favorite,
        R.string.nav_favorites
    )
}
