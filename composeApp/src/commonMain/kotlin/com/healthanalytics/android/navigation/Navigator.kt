
expect class PlatformNavigator() {
    fun navigate(route: String)
    fun goBack()
    fun canGoBack(): Boolean
}

data class NavigationState(
    val currentScreen: Screen = Screen.DASHBOARD,
    val canGoBack: Boolean = false
)

class CommonNavigator {
    private val platformNavigator = PlatformNavigator()
    
    fun navigateToScreen(screen: Screen) {
        platformNavigator.navigate(screen.name)
    }
    
    fun goBack() {
        if (platformNavigator.canGoBack()) {
            platformNavigator.goBack()
        }
    }
}
