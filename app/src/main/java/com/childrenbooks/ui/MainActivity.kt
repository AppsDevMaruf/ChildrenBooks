package com.childrenbooks.ui

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.childrenbooks.ChildrenBooksApplication
import com.childrenbooks.appViewModelFactory
import com.childrenbooks.core.designsystem.ChildrenBooksTheme
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

/** Picks the first screen once; later settings changes must not rebuild the nav graph. */
class MainViewModel(progressRepository: ProgressRepository) : ViewModel() {
    val startDestination: StateFlow<String?> = progressRepository.settings
        .map { if (it.onboardingDone) Destinations.Home else Destinations.Onboarding }
        .take(1)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    companion object {
        val Factory = appViewModelFactory { container -> MainViewModel(container.progressRepository) }
    }
}

// Keep this class name and package: it is the launcher activity, and renaming it can remove
// the app's icon from users' home screens after an update.
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }
    private val container by lazy { (application as ChildrenBooksApplication).container }
    private var startedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition { viewModel.startDestination.value == null }
        enableEdgeToEdge()
        setContent {
            ChildrenBooksTheme {
                val start by viewModel.startDestination.collectAsStateWithLifecycle()
                start?.let { AppNavHost(startDestination = it) }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        startedAt = SystemClock.elapsedRealtime()
    }

    override fun onStop() {
        val seconds = (SystemClock.elapsedRealtime() - startedAt) / 1_000L
        container.applicationScope.launch { container.progressRepository.addActiveSeconds(seconds) }
        container.narrator.stop()
        super.onStop()
    }
}
