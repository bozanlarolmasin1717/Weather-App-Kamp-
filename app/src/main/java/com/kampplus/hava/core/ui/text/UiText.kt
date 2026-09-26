package com.kampplus.hava.core.ui.text

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {

    data class Dynamic(
        val value: String
    ) : UiText

    class Resource(
        @param:StringRes
        val resId: Int,
        vararg val args: Any
    ) : UiText {

        override fun equals(
            other: Any?
        ): Boolean =
            other is Resource &&
                other.resId == resId &&
                other.args.contentEquals(
                    args
                )

        override fun hashCode():
            Int =
            31 * resId +
                args.contentHashCode()
    }

    @Composable
    fun asString(): String =
        when (this) {

            is Dynamic ->
                value

            is Resource ->
                stringResource(
                    resId,
                    *args
                )
        }

    fun asString(
        resources: Resources
    ): String =
        when (this) {

            is Dynamic ->
                value

            is Resource ->
                resources.getString(
                    resId,
                    *args
                )
        }
}
