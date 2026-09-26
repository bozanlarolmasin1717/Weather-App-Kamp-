package com.kampplus.hava.feature.weather.presentation.detail.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kampplus.hava.R

@Composable
fun ShareButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            Icons.Filled.Share,
            contentDescription =
                stringResource(
                    R.string.action_share
                )
        )
    }
}
