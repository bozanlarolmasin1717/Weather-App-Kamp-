package com.kampplus.hava.core.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.kampplus.hava.core.ui.theme.HavaColors

@Composable
fun BottomBar(currentDestination: NavDestination?, onNavigate: (TopLevelDestination) -> Unit, modifier: Modifier = Modifier) {
    NavigationBar(
        modifier = modifier,
        containerColor = HavaColors.Midnight.copy(alpha = 0.96f),
        tonalElevation = 0.dp
    ) {
        TopLevelDestination
            .entries
            .forEach { destination ->

                NavigationBarItem(
                    selected =
                    currentDestination
                        .isOn(
                            destination
                        ),
                    onClick = {
                        onNavigate(
                            destination
                        )
                    },
                    icon = {
                        Icon(
                            destination.icon,
                            contentDescription =
                            null
                        )
                    },
                    label = {
                        Text(
                            stringResource(
                                destination
                                    .labelRes
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HavaColors.Ink,
                        selectedTextColor = HavaColors.TextPrimary,
                        indicatorColor = HavaColors.Ice,
                        unselectedIconColor = HavaColors.TextSecondary,
                        unselectedTextColor = HavaColors.TextSecondary
                    )
                )
            }
    }
}

fun NavDestination?.isOn(destination: TopLevelDestination): Boolean = this
    ?.hierarchy
    ?.any {
        it.hasRoute(
            destination
                .routeClass
        )
    } == true
