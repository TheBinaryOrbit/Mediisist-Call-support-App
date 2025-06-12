import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.call_support.CallOverlayData
import com.example.call_support.MainScreen

fun NavGraphBuilder.mainScreenNavGraph(
    navController: NavHostController,
    acceptedCalls: SnapshotStateList<CallOverlayData>, // ✅ direct type, no need to cast
    onCancelCall: (CallOverlayData) -> Unit
) {
    composable("MainScreen/{tab}") { backStackEntry ->
        val tab = backStackEntry.arguments?.getString("tab") ?: "home"

        MainScreen(
            navController = navController,
            acceptedCalls = acceptedCalls, // ✅ already correct type
            onCancelCall = onCancelCall,
            initialTab = tab
        )
    }
}
