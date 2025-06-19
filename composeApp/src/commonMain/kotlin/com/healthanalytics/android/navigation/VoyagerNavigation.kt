package com.healthanalytics.android.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class PostListScreen : Screen {

    @Composable
    override fun Content() {

    }
}

data class PostDetailsScreen(val postId: Long) : Screen {

    @Composable
    override fun Content() {

    }
}
