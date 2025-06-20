import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.call_support.domain.EmergencyCall
import com.example.call_support.myui.home.MainScreen

fun NavGraphBuilder.mainScreenNavGraph(
    navController: NavHostController,
    acceptedCalls: SnapshotStateList<EmergencyCall>
) {
    composable("MainScreen/{tab}") { backStackEntry ->
        val tab = backStackEntry.arguments?.getString("tab") ?: "home"

        MainScreen(
            navController = navController,
            acceptedCalls = acceptedCalls,
            initialTab = tab
        )
    }
}
