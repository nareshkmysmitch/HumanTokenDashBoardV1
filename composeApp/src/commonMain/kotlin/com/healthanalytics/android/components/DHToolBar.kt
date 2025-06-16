package com.healthanalytics.android.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.modifier.onBoxClick
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.FontFamily

@Composable
fun DHToolBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    showBackArrow: Boolean = true,
    onBackClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackArrow) {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(32.dp)
                    .background(
                        color = AppColors.White,
                        shape = CircleShape
                    )
                    .onBoxClick(
                        onClick = {
                            onBackClick()
                        }
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize(0.85f)
                )
            }
        }

        if (title != null) {
            Text(
                text = title,
                fontFamily = FontFamily.semiBold(),
                fontSize = 22.sp,
                color = AppColors.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}