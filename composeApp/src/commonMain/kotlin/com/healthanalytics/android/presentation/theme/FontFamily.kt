package com.healthanalytics.android.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import humantokendashboardv1.composeapp.generated.resources.Res
import humantokendashboardv1.composeapp.generated.resources.ft_bold
import humantokendashboardv1.composeapp.generated.resources.ft_medium
import humantokendashboardv1.composeapp.generated.resources.ft_pil_bold
import humantokendashboardv1.composeapp.generated.resources.ft_pil_medium
import humantokendashboardv1.composeapp.generated.resources.ft_pil_regular
import humantokendashboardv1.composeapp.generated.resources.ft_pil_semi_bold
import humantokendashboardv1.composeapp.generated.resources.ft_regular
import humantokendashboardv1.composeapp.generated.resources.ft_semi_bold

object FontFamily{

    @Composable
    fun regular(): FontFamily {
        return FontFamily(org.jetbrains.compose.resources.Font(Res.font.ft_regular))
    }

    @Composable
    fun medium(): FontFamily {
        return FontFamily(org.jetbrains.compose.resources.Font(Res.font.ft_medium))
    }

    @Composable
    fun semiBold(): FontFamily {
        return FontFamily(org.jetbrains.compose.resources.Font(Res.font.ft_semi_bold))
    }

    @Composable
    fun bold(): FontFamily {
        return FontFamily(org.jetbrains.compose.resources.Font(Res.font.ft_bold))
    }

    @Composable
    fun pilRegular(): FontFamily {
        return FontFamily(org.jetbrains.compose.resources.Font(Res.font.ft_pil_regular))
    }

    @Composable
    fun pilMedium(): FontFamily {
        return FontFamily(org.jetbrains.compose.resources.Font(Res.font.ft_pil_medium))
    }

    @Composable
    fun pilSemiBold(): FontFamily {
        return FontFamily(org.jetbrains.compose.resources.Font(Res.font.ft_pil_semi_bold))
    }

    @Composable
    fun pilBold(): FontFamily {
        return FontFamily(org.jetbrains.compose.resources.Font(Res.font.ft_pil_bold))
    }

}






