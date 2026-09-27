package com.kampplus.hava.feature.weather.presentation.list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.kampplus.hava.R
import com.kampplus.hava.core.ui.theme.HavaColors

@Composable
fun CitySearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    val shape = RoundedCornerShape(22.dp)

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = HavaColors.Glass,
                shape = shape
            ),
        placeholder = {
            Text(stringResource(R.string.search_hint))
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onQueryChange("")
                    }
                ) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = stringResource(R.string.action_clear)
                    )
                }
            }
        },
        singleLine = true,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            unfocusedBorderColor = HavaColors.GlassBorder,
            focusedContainerColor = HavaColors.GlassStrong,
            unfocusedContainerColor = HavaColors.Glass
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        )
    )
}
