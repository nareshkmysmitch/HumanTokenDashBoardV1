package com.healthanalytics.android.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.healthanalytics.android.presentation.theme.AppColors

class MainScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(BottomNavScreen.Health) { tabNavigator ->
            Scaffold(
                containerColor = AppColors.Black,
                bottomBar = {
                    NavigationBar(
                        containerColor = AppColors.Black,
                        contentColor = Color.White
                    ) {
                        val currentTab by tabNavigator.currentTab
                        BottomNavScreen.entries.forEach { screen ->
                            NavigationBarItem(
                                selected = currentTab == screen,
                                onClick = { tabNavigator.current = screen },
                                icon = { screen.options.icon },
                                label = { Text(screen.options.title) },
                                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                    selectedIconColor = AppColors.Pink,
                                    selectedTextColor = AppColors.Pink,
                                    indicatorColor = AppColors.Black,
                                    unselectedIconColor = Color.White,
                                    unselectedTextColor = Color.White
                                )
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppColors.Black)
                        .padding(paddingValues)
                ) {
                    CurrentTab()
                }
            }
        }
    }
} 