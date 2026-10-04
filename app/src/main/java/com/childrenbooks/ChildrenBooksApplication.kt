package com.childrenbooks

import android.app.Application
import android.content.res.Resources
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.childrenbooks.core.audio.AndroidNarrator
import com.childrenbooks.core.audio.Narrator
import com.childrenbooks.data.content.ContentRepository
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ChildrenBooksApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

/** Manual dependency container: the app is small enough that a DI framework adds no value. */
class AppContainer(application: Application) {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val resources: Resources = application.resources

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { application.preferencesDataStoreFile("children_books") },
    )

    val progressRepository = ProgressRepository(dataStore)

    val contentRepository = ContentRepository(application.resources)

    val narrator: Narrator by lazy { AndroidNarrator(application) }
}

/** Builds a ViewModel factory that receives the [AppContainer]. */
inline fun <reified VM : ViewModel> appViewModelFactory(
    crossinline create: CreationExtras.(AppContainer) -> VM,
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ChildrenBooksApplication
        create(application.container)
    }
}
