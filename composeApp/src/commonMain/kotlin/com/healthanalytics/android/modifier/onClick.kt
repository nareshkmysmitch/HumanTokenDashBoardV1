package com.healthanalytics.android.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.healthanalytics.android.presentation.theme.AppColors

@Composable
fun Modifier.onRowClick(enable: Boolean = false, onClick: () -> Unit): Modifier {
    return this.clickable(onClick = { onClick() },
        indication = null,
        interactionSource = remember { MutableInteractionSource() })
}

@Composable
fun Modifier.onColumnClick(onClick: () -> Unit): Modifier {
    return this.clickable(onClick = { onClick() },
        indication = null,
        interactionSource = remember { MutableInteractionSource() })
}

@Composable
fun Modifier.onBoxClick(onClick: () -> Unit): Modifier {
    return this.clickable(onClick = { onClick() },
        indication = null,
        interactionSource = remember { MutableInteractionSource() })
}

@Composable
fun Modifier.onIconClick(onClick: () -> Unit): Modifier {
    return this.clickable(onClick = { onClick() },
        indication = null,
        interactionSource = remember { MutableInteractionSource() })
}

@Composable
fun Modifier.onCardClick(enable: Boolean = false, onClick: () -> Unit): Modifier {
    return this.clickable(onClick = { onClick() },
        indication = null,
        interactionSource = remember { MutableInteractionSource() })
}

@Composable
fun Modifier.onTextClick(rippleEffect: Boolean = false, onClick: () -> Unit): Modifier {
    return this.clickable(onClick = { onClick() }, indication = if (rippleEffect) {
        ripple(
            bounded = true,
            color = AppColors.textPrimary
        )
    } else {
        null
    }, interactionSource = remember { MutableInteractionSource() })
}

@Composable
fun Modifier.onImageClick(enable: Boolean = false, onClick: () -> Unit): Modifier {
    return this.clickable(onClick = { onClick() },
        indication = null,
        interactionSource = remember { MutableInteractionSource() })
}