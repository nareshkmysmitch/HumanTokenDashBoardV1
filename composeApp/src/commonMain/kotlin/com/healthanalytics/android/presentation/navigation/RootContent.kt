package com.healthanalytics.android.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

import com.healthanalytics.android.presentation.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootContent(component: RootComponent, modifier: Modifier = Modifier) {
    val childStack by component.childStack.subscribeAsState()
    val activeComponent = childStack.active.instance

    when (val child = activeComponent) {
        is RootComponent.Child.Main -> {
            MainContent(
                component = child.component,
                modifier = modifier
            )
        }
        
        is RootComponent.Child.Symptoms -> {
            SymptomsContent(
                component = child.component,
                modifier = modifier
            )
        }
        
        is RootComponent.Child.ConversationList -> {
            ConversationListContent(
                component = child.component,
                modifier = modifier
            )
        }
        
        is RootComponent.Child.Profile -> {
            ProfileContent(
                component = child.component,
                modifier = modifier
            )
        }
        
        is RootComponent.Child.Cart -> {
            CartContent(
                component = child.component,
                modifier = modifier
            )
        }
        
        is RootComponent.Child.BiomarkerDetail -> {
            BiomarkerDetailContent(
                component = child.component,
                modifier = modifier
            )
        }
        
        is RootComponent.Child.BioMarkerFullReport -> {
            BioMarkerFullReportContent(
                component = child.component,
                modifier = modifier
            )
        }
        
        is RootComponent.Child.ProductDetail -> {
            ProductDetailContent(
                component = child.component,
                modifier = modifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(component: MainComponent, modifier: Modifier = Modifier) {
    val childStack by component.childStack.subscribeAsState()
    val activeComponent = childStack.active.instance
    
    val tabType = when (activeComponent) {
        is MainComponent.Child.Health -> MainComponent.TabType.Health
        is MainComponent.Child.Recommendations -> MainComponent.TabType.Recommendations
        is MainComponent.Child.Marketplace -> MainComponent.TabType.Marketplace
    }

    Scaffold(
        containerColor = AppColors.Black,
        topBar = {
            TopAppBar(
                title = { Text("Human Token") },
                actions = {
                    when (tabType) {
                        MainComponent.TabType.Health -> {
                            IconButton(onClick = { 
                                // Navigate to symptoms
                                (activeComponent as? MainComponent.Child.Health)?.component?.onNavigateToSymptoms?.invoke()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "symptoms",
                                    tint = AppColors.White
                                )
                            }
                            IconButton(onClick = { 
                                // Navigate to chat
                                (activeComponent as? MainComponent.Child.Health)?.component?.onNavigateToConversationList?.invoke()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "Chat",
                                    tint = AppColors.White
                                )
                            }
                        }
                        
                        MainComponent.TabType.Recommendations -> {
                            IconButton(onClick = { 
                                // Navigate to profile
                                (activeComponent as? MainComponent.Child.Recommendations)?.component?.onNavigateToProfile?.invoke()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Profile",
                                    tint = AppColors.White
                                )
                            }
                        }
                        
                        MainComponent.TabType.Marketplace -> {
                            IconButton(onClick = { 
                                // Navigate to cart
                                (activeComponent as? MainComponent.Child.Marketplace)?.component?.onNavigateToCart?.invoke()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = AppColors.White
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.Black,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = AppColors.Black,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = tabType == MainComponent.TabType.Health,
                    onClick = { component.onTabSelected(MainComponent.TabType.Health) },
                    icon = {
                        Icon(
                            painter = rememberVectorPainter(Icons.Default.Add),
                            contentDescription = "Health"
                        )
                    },
                    label = { Text("Health") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AppColors.Pink,
                        selectedTextColor = AppColors.Pink,
                        indicatorColor = AppColors.Black,
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White
                    )
                )
                
                NavigationBarItem(
                    selected = tabType == MainComponent.TabType.Recommendations,
                    onClick = { component.onTabSelected(MainComponent.TabType.Recommendations) },
                    icon = {
                        Icon(
                            painter = rememberVectorPainter(Icons.Default.AccountCircle),
                            contentDescription = "Recommendations"
                        )
                    },
                    label = { Text("Recommendations") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AppColors.Pink,
                        selectedTextColor = AppColors.Pink,
                        indicatorColor = AppColors.Black,
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White
                    )
                )
                
                NavigationBarItem(
                    selected = tabType == MainComponent.TabType.Marketplace,
                    onClick = { component.onTabSelected(MainComponent.TabType.Marketplace) },
                    icon = {
                        Icon(
                            painter = rememberVectorPainter(Icons.Default.ShoppingCart),
                            contentDescription = "Marketplace"
                        )
                    },
                    label = { Text("Marketplace") },
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
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppColors.Black)
                .padding(paddingValues)
        ) {
            Children(
                stack = component.childStack,
                animation = stackAnimation(fade()),
            ) { child ->
                when (val instance = child.instance) {
                    is MainComponent.Child.Health -> {
                        HealthContent(component = instance.component)
                    }
                    
                    is MainComponent.Child.Recommendations -> {
                        RecommendationsContent(component = instance.component)
                    }
                    
                    is MainComponent.Child.Marketplace -> {
                        MarketplaceContent(component = instance.component)
                    }
                }
            }
        }
    }
} 