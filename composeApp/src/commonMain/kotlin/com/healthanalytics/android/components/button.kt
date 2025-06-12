package com.healthanalytics.android.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.healthanalytics.android.presentation.theme.AppTextStyles

@Composable
fun PrimaryButton(
    isEnable: Boolean,
    onclick: () -> Unit,
    buttonName: String
) {
    Button(
        onClick = onclick,
        enabled = isEnable,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnable)
                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                alpha = 0.3f
            ),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Text(
            text = buttonName,
            style = AppTextStyles.buttonText,
            fontSize = 16.sp
        )
    }
}