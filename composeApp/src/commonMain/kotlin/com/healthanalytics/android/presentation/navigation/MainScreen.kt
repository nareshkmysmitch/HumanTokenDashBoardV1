package com.healthanalytics.android.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.key.Key.Companion.R
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import co.touchlab.kermit.Logger
import com.healthanalytics.android.presentation.theme.AppColors

class MainScreen : Screen {

    @Composable
    override fun Content() {

        val bottomNavScreens = listOf(
            BottomNavScreen.Health,
            BottomNavScreen.Recommendations,
            BottomNavScreen.Marketplace
        )


        TabNavigator(bottomNavScreens.first()) { tabNavigator ->

            val currentTab = tabNavigator.current

            Scaffold(
                containerColor = AppColors.Black, bottomBar = {
                    NavigationBar(
                        containerColor = AppColors.Black, contentColor = Color.White
                    ) {
                        bottomNavScreens.forEach { screen ->

                            Logger.e("screen: $screen")

                            NavigationBarItem(
                                selected = currentTab == screen,
                                onClick = { tabNavigator.current = screen },

                                icon = {
                                    screen?.options?.icon?.let { icon ->
                                        Icon(painter = icon, contentDescription = null)
                                    } ?: Icon(
                                        painter = rememberVectorPainter(Icons.Default.Help),
                                        contentDescription = "Fallback"
                                    )
                                },

                                label = {
                                    val title = screen?.options?.title ?: "Untitled"
                                    Text(title)
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = AppColors.Pink,
                                    selectedTextColor = AppColors.Pink,
                                    indicatorColor = AppColors.Black,
                                    unselectedIconColor = Color.White,
                                    unselectedTextColor = Color.White
                                )
                            )
                        }
                    }
                }) { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize().background(AppColors.Black)
                        .padding(paddingValues)
                ) {
                    CurrentTab()
                }
            }
        }
    }
}
