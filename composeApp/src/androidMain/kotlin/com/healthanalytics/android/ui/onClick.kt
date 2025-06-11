package com.healthanalytics.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.healthanalytics.android.presentation.theme.AppColors


@Composable
fun Modifier.onTextClick(rippleEffect: Boolean = false, onClick: () -> Unit): Modifier {
    return this.clickable(
        onClick = { onClick() }, indication = if (rippleEffect) {
            ripple(
                bounded = true,
                color = AppColors.PurpleBackground,
            )
        } else {
            null
        }, interactionSource = remember { MutableInteractionSource() })
}

