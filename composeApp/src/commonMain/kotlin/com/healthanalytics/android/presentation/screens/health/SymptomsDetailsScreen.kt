package com.healthanalytics.android.presentation.screens.health

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.healthanalytics.android.BackHandler
import com.healthanalytics.android.data.models.home.Biomarker
import com.healthanalytics.android.data.models.home.SymptomsData
import com.healthanalytics.android.presentation.theme.AppColors
import com.healthanalytics.android.presentation.theme.Dimensions
import com.healthanalytics.android.presentation.theme.Dimensions.size12dp
import com.healthanalytics.android.presentation.theme.Dimensions.size16dp
import com.healthanalytics.android.presentation.theme.Dimensions.size4dp
import com.healthanalytics.android.presentation.theme.Dimensions.size8dp
import com.healthanalytics.android.presentation.theme.FontFamily
import com.healthanalytics.android.presentation.theme.FontSize
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.associated_biomarkers
import humantokendashboardv1.composeapp.generated.resources.ic_vital_signs
import humantokendashboardv1.composeapp.generated.resources.symptom_details
import humantokendashboardv1.composeapp.generated.resources.times_plural
import humantokendashboardv1.composeapp.generated.resources.times_reported
import humantokendashboardv1.composeapp.generated.resources.times_singular
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsDetailsScreen(
    symptomsData: SymptomsData?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(enabled = true, onBack = onNavigateBack)
    val count = symptomsData?.count
    val name = symptomsData?.name
    val category = symptomsData?.category
    val lastReported = symptomsData?.last_reported
    val bioMarkerList = symptomsData?.biomarkers

    val countString = GetReportedTimes(count)

    println("countString-->${countString}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.symptom_details),
                        color = AppColors.White,
                        fontFamily = FontFamily.bold()
                    )
                }, navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.White
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.AppBackgroundColor,
                    navigationIconContentColor = AppColors.White,
                    titleContentColor = AppColors.White
                )
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = modifier.fillMaxSize().padding(paddingValues)
                .padding(PaddingValues(size16dp))
        ) {
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.size(Dimensions.size36dp)
                            .background(
                                AppColors.tagOrange.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_vital_signs),
                            contentDescription = null,
                            tint = AppColors.tagOrange,
                            modifier = Modifier.size(size16dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(size16dp))

                    Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        Text(
                            text = name ?: "",
                            fontSize = FontSize.textSize16sp,
                            fontFamily = FontFamily.medium(),
                            color = AppColors.textPrimary,
                        )

                        Text(
                            text = category ?: "",
                            fontSize = FontSize.textSize14sp,
                            fontFamily = FontFamily.regular(),
                            color = AppColors.textSecondary,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(size16dp))

                Text(
                    text = stringResource(Res.string.times_reported),
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.regular(),
                    color = AppColors.textSecondary,
                )

                Text(
                    text = countString,
                    color = AppColors.textPrimary,
                )

                Spacer(modifier = Modifier.height(size16dp))

                Text(
                    text = "Last reported: ${formatDate(lastReported ?: "")}",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.regular(),
                    color = AppColors.White,
                )

            }

            if (bioMarkerList?.isNotEmpty() == true) {
                item {
                    Text(
                        text = stringResource(Res.string.associated_biomarkers),
                        fontSize = FontSize.textSize16sp,
                        fontFamily = FontFamily.medium(),
                        color = AppColors.textSecondary,
                    )
                }

                items(bioMarkerList) { bioMarker ->
                    Spacer(modifier = Modifier.height(size16dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = AppColors.CardGrey
                        ),
                        shape = RoundedCornerShape(Dimensions.cornerRadiusLarge)
                    ) {
                        SymptomsBioMarker(bioMarker)
                    }
                }
            }
        }
    }
}

@Composable
fun SymptomsBioMarker(bioMarker: Biomarker?) {
    val description = bioMarker?.description
    val metric = bioMarker?.metric
    val inference = bioMarker?.inference

    val value = bioMarker?.value
    val unit = bioMarker?.unit

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(PaddingValues(size12dp))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = metric ?: "",
                maxLines = 2,
                fontSize = FontSize.textSize14sp,
                fontFamily = FontFamily.medium(),
                color = AppColors.White,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Surface(
                color = AppColors.HighColor, shape = MaterialTheme.shapes.small
            ) { //TODO @puvi need to discuss regarding the inference
                Text(
                    text = inference ?: "",
                    modifier = Modifier.wrapContentWidth()
                        .padding(horizontal = size8dp, vertical = size4dp),
                    fontSize = FontSize.textSize12sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.Black
                )
            }
        }
        Spacer(modifier = Modifier.height(size16dp))

        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$value",
                    fontSize = FontSize.textSize16sp,
                    fontFamily = FontFamily.medium(),
                    color = AppColors.White,
                )
                Spacer(modifier = Modifier.width(size4dp))
                Text(
                    text = " $unit",
                    fontSize = FontSize.textSize14sp,
                    fontFamily = FontFamily.regular(),
                    color = AppColors.TextGrey,
                )
            }
        }

        Spacer(modifier = Modifier.height(size16dp))
        Text(
            text = description ?: "",
            fontSize = FontSize.textSize12sp,
            fontFamily = FontFamily.regular(),
            color = AppColors.textSecondary,
        )
    }
}

@Composable
fun GetReportedTimes(count: Int?): AnnotatedString {
    val timesString = if (count == 1) {
        stringResource(Res.string.times_singular)
    } else {
        stringResource(Res.string.times_plural)
    }
    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = FontSize.textSize20sp,
                fontFamily = FontFamily.medium()
            )
        ) {
            append("$count")
        }
        append(" ")
        withStyle(
            style = SpanStyle(
                fontSize = FontSize.textSize14sp,
                fontFamily = FontFamily.regular()
            )
        ) {
            append(timesString)
        }
    }

}
