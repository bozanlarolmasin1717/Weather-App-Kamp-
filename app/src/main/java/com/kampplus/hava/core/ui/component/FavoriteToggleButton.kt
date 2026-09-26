package com.kampplus.hava.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R

@Composable
fun FavoriteToggleButton(isFavorite: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (isFavorite) Color(0x33EF4444) else Color.White.copy(alpha = 0.16f))
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = stringResource(if (isFavorite) R.string.action_remove_favorite else R.string.action_add_favorite),
            tint = if (isFavorite) Color(0xFFEF4444) else Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}
