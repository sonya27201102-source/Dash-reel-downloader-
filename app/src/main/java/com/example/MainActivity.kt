package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.downloader.ReelDownloader
import com.example.data.local.AppDatabase
import com.example.data.repository.ReelRepository
import com.example.ui.components.ReelPlayerDialog
import com.example.ui.navigation.DashBottomNavigationBar
import com.example.ui.navigation.DashTab
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DashReelTheme
import com.example.ui.viewmodel.ReelViewModel
import com.example.ui.viewmodel.ReelViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ReelViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val repository = ReelRepository(database.reelDao())
        val downloader = ReelDownloader(this)
        val factory = ReelViewModelFactory(repository, downloader)
        viewModel = ViewModelProvider(this, factory)[ReelViewModel::class.java]

        // Handle shared URL from other apps (e.g., Instagram Share -> Dash Reel)
        handleIntent(intent)

        setContent {
            DashReelTheme {
                DashApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank()) {
                val extractedUrl = extractUrl(sharedText)
                if (extractedUrl.isNotBlank()) {
                    viewModel.onUrlChange(extractedUrl)
                }
            }
        }
    }

    private fun extractUrl(text: String): String {
        val parts = text.split("\\s+".toRegex())
        return parts.firstOrNull { it.startsWith("http://") || it.startsWith("https://") } ?: text.trim()
    }
}

@Composable
fun DashApp(viewModel: ReelViewModel) {
    var currentTab by remember { mutableStateOf(DashTab.HOME) }
    val nowPlayingReel by viewModel.nowPlayingReel.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle back button behavior
    BackHandler(enabled = currentTab != DashTab.HOME || nowPlayingReel != null) {
        if (nowPlayingReel != null) {
            viewModel.closePlayer()
        } else if (currentTab != DashTab.HOME) {
            currentTab = DashTab.HOME
        }
    }

    // Display snackbar messages
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            DashBottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                DashTab.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToLibrary = { currentTab = DashTab.LIBRARY }
                )
                DashTab.LIBRARY -> LibraryScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { currentTab = DashTab.HOME }
                )
                DashTab.SETTINGS -> SettingsScreen(
                    viewModel = viewModel
                )
            }
        }

        // Full screen video player dialog
        if (nowPlayingReel != null) {
            ReelPlayerDialog(
                reel = nowPlayingReel!!,
                onDismiss = { viewModel.closePlayer() },
                onToggleFavorite = { reel -> viewModel.toggleFavorite(reel) }
            )
        }
    }
}
