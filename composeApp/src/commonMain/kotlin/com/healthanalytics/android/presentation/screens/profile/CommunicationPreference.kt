package com.healthanalytics.android.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.healthanalytics.android.data.models.RecommendationIcon
import com.healthanalytics.android.presentation.screens.marketplace.CommunicationPreferenceType
import com.healthanalytics.android.presentation.screens.marketplace.MarketPlaceViewModel
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import com.healthanalytics.android.ui.PrimaryButton
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.biohacker_description
import humantokendashboardv1.composeapp.generated.resources.biohacker_example
import humantokendashboardv1.composeapp.generated.resources.biohacker_title
import humantokendashboardv1.composeapp.generated.resources.close_friend_description
import humantokendashboardv1.composeapp.generated.resources.close_friend_example
import humantokendashboardv1.composeapp.generated.resources.close_friend_title
import humantokendashboardv1.composeapp.generated.resources.communication_preferences
import humantokendashboardv1.composeapp.generated.resources.communication_preferences_subtitle
import humantokendashboardv1.composeapp.generated.resources.doctor_description
import humantokendashboardv1.composeapp.generated.resources.doctor_example
import humantokendashboardv1.composeapp.generated.resources.doctor_title
import humantokendashboardv1.composeapp.generated.resources.ic_stethoscope
import humantokendashboardv1.composeapp.generated.resources.ic_vital_signs
import humantokendashboardv1.composeapp.generated.resources.information_delivery_style
import humantokendashboardv1.composeapp.generated.resources.information_delivery_style_subtitle
import humantokendashboardv1.composeapp.generated.resources.save_preferences
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

sealed class CommunicationUIData(
    val titleRes: StringResource,
    val descriptionRes: StringResource,
    val exampleRes: StringResource,
    val type: String,
) {
    data object Biohacker : CommunicationUIData(
        titleRes = Res.string.biohacker_title,
        descriptionRes = Res.string.biohacker_description,
        exampleRes = Res.string.biohacker_example,
        type = CommunicationPreferenceType.Biohacker.type
    )

    data object Doctor : CommunicationUIData(
        titleRes = Res.string.doctor_title,
        descriptionRes = Res.string.doctor_description,
        exampleRes = Res.string.doctor_example,
        type = CommunicationPreferenceType.Doctor.type
    )

    data object CloseFriend : CommunicationUIData(
        titleRes = Res.string.close_friend_title,
        descriptionRes = Res.string.close_friend_description,
        exampleRes = Res.string.close_friend_example,
        type = CommunicationPreferenceType.CloseFriend.type
    )
}


@Composable
fun CommunicationPreference(
    onStyleSelected: (CommunicationUIData) -> Unit,
    onSaveClicked: (selectedPreference: CommunicationUIData?) -> Unit,
    viewModel: MarketPlaceViewModel,
) {
    val accessToken by viewModel.accessToken.collectAsStateWithLifecycle()
    val uiState by viewModel.uiCommunicationPreference.collectAsState()

    LaunchedEffect(accessToken) {
        if (accessToken != null) {
            viewModel.loadCommunicationPreference(accessToken)
        }
    }

    val selectedPreference by viewModel.communicationSelected.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
            .background(AppColors.BlueBackground, shape = RoundedCornerShape(Dimensions.size12dp))
            .padding(Dimensions.size16dp)
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(Dimensions.size180dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Text(
                text = stringResource(Res.string.communication_preferences),
                fontSize = FontSize.textSize18sp,
                fontFamily = FontFamily.medium(),
                color = AppColors.White
            )

            Spacer(modifier = Modifier.height(Dimensions.size6dp))

            Text(
                text = stringResource(Res.string.communication_preferences_subtitle),
                fontSize = FontSize.textSize14sp,
                fontFamily = FontFamily.medium(),
                color = AppColors.descriptionColor
            )

            Spacer(modifier = Modifier.height(Dimensions.size24dp))

            Text(
                text = stringResource(Res.string.information_delivery_style),
                fontSize = FontSize.textSize14sp,
                fontFamily = FontFamily.medium(),
                color = AppColors.White
            )

            Spacer(modifier = Modifier.height(Dimensions.size12dp))

            Text(
                text = stringResource(Res.string.information_delivery_style_subtitle),
                fontSize = FontSize.textSize14sp,
                fontFamily = FontFamily.regular(),
                color = AppColors.White
            )

            Spacer(modifier = Modifier.height(Dimensions.size12dp))

            CommunicationStyleCard(
                style = CommunicationUIData.Biohacker,
                selected = selectedPreference == CommunicationUIData.Biohacker,
                onClick = { onStyleSelected(CommunicationUIData.Biohacker) },
                icon = RecommendationIcon.Painter(Res.drawable.ic_vital_signs)
            )

            Spacer(modifier = Modifier.height(Dimensions.size12dp))

            CommunicationStyleCard(
                style = CommunicationUIData.Doctor,
                selected = selectedPreference == CommunicationUIData.Doctor,
                onClick = { onStyleSelected(CommunicationUIData.Doctor) },
                icon = RecommendationIcon.Painter(Res.drawable.ic_stethoscope)
            )

            Spacer(modifier = Modifier.height(Dimensions.size12dp))

            CommunicationStyleCard(
                style = CommunicationUIData.CloseFriend,
                selected = selectedPreference == CommunicationUIData.CloseFriend,
                onClick = { onStyleSelected(CommunicationUIData.CloseFriend) },
                icon = RecommendationIcon.Vector(Icons.Default.ChatBubble)
            )


            Spacer(modifier = Modifier.height(Dimensions.size28dp))


            val isEnabled = viewModel.initialPreferenceValue != selectedPreference
            val buttonColor =
                if (isEnabled) AppColors.Pink else AppColors.Pink.copy(alpha = 0.2f)


            PrimaryButton(
                modifier = Modifier.wrapContentWidth(),
                txt = stringResource(Res.string.save_preferences),
                buttonColor = buttonColor,
                onClick = {
                    onSaveClicked(selectedPreference)
                }
            )
        }
    }
}

@Composable
fun CommunicationStyleCard(
    style: CommunicationUIData,
    selected: Boolean,
    onClick: () -> Unit,
    icon: RecommendationIcon,
) {
    val borderColor = if (selected) AppColors.darkPink else AppColors.borderBlue
    val backgroundColor = if (selected) AppColors.semiTransparentPink else AppColors.BlueBackground
    val iconBackgroundColor = if (selected) AppColors.iconSemiTransparentPink else AppColors.Teal

    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(Dimensions.size12dp))
            .border(Dimensions.size1dp, borderColor, RoundedCornerShape(Dimensions.size12dp))
            .background(backgroundColor)
            .clickable { onClick() }.padding(Dimensions.size16dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(Dimensions.size32dp).background(
                    color = iconBackgroundColor, RoundedCornerShape(50)
                ),
                contentAlignment = Alignment.Center
            ) {
                when (icon) {
                    is RecommendationIcon.Vector -> {
                        Icon(
                            imageVector = icon.imageVector,
                            contentDescription = null,
                            tint = if (selected) AppColors.White else AppColors.iconGrey,
                            modifier = Modifier.size(Dimensions.size16dp)
                        )
                    }

                    is RecommendationIcon.Painter -> {
                        Icon(
                            painter = painterResource(icon.resource),
                            contentDescription = null,
                            tint = if (selected) AppColors.White else AppColors.iconGrey,
                            modifier = Modifier.size(Dimensions.size16dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(Dimensions.size12dp))

            Text(
                text = stringResource(style.titleRes),
                fontSize = FontSize.textSize16sp,
                fontFamily = FontFamily.medium(),
                color = AppColors.textPrimary
            )

            Spacer(modifier = Modifier.width(Dimensions.size12dp))

            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = AppColors.darkPink
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimensions.size8dp))

        Text(
            text = stringResource(style.descriptionRes),
            fontSize = FontSize.textSize14sp,
            fontFamily = FontFamily.regular(),
            color = AppColors.White
        )

        Spacer(modifier = Modifier.height(Dimensions.size8dp))

        Text(
            text = stringResource(style.exampleRes),
            fontSize = FontSize.textSize12sp,
            fontFamily = FontFamily.regular(),
            color = AppColors.txtBlue,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.background(color = AppColors.backgroundBlue)
                .padding(PaddingValues(Dimensions.size12dp))
        )
    }
}
