package com.childrenbooks.feature.grownups

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.childrenbooks.R
import com.childrenbooks.appViewModelFactory
import com.childrenbooks.data.content.ContentRepository
import com.childrenbooks.data.progress.AgeBand
import com.childrenbooks.data.progress.AppSettings
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GrownupsUiState(
    val settings: AppSettings = AppSettings(),
    val report: GrownReport = GrownReport(),
    val loading: Boolean = true,
)

/** Shared by the report and settings screens; each destination gets its own instance. */
class GrownupsViewModel(
    private val progressRepository: ProgressRepository,
    private val content: ContentRepository,
    private val resources: Resources,
) : ViewModel() {
    private val range = MutableStateFlow(ReportRange.Week)

    val uiState: StateFlow<GrownupsUiState> = combine(
        progressRepository.progress,
        progressRepository.settings,
        range,
    ) { progress, settings, selectedRange ->
        GrownupsUiState(
            settings = settings,
            report = ReportBuilder.build(progress, content.worlds, progressRepository.today(), selectedRange),
            loading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GrownupsUiState())

    fun setRange(value: ReportRange) {
        range.value = value
    }

    fun setChildName(name: String) = updateSettings { it.copy(childName = name.trim().take(MaxNameLength)) }

    fun setAgeBand(band: AgeBand) = updateSettings { it.copy(ageBand = band) }

    fun setShowWords(enabled: Boolean) = updateSettings { it.copy(showWords = enabled) }

    fun setBedtime(enabled: Boolean) = updateSettings { it.copy(bedtime = enabled) }

    fun resetProgress() {
        viewModelScope.launch { progressRepository.resetProgress() }
    }

    /** Plain-text summary a grown-up can send to another carer. Nothing is uploaded. */
    fun shareText(): String {
        val state = uiState.value
        val report = state.report
        val period = resources.getString(
            if (report.range == ReportRange.Week) R.string.report_this_week else R.string.report_this_month,
        )
        val who = state.settings.childName.ifBlank { resources.getString(R.string.report_your_child) }
        return buildString {
            appendLine(resources.getString(R.string.report_share_title, who, period))
            appendLine(resources.getString(R.string.report_share_minutes, minutes(report.totalMinutes)))
            appendLine(resources.getString(R.string.report_share_sets, report.setsFinished))
            appendLine(resources.getString(R.string.report_share_streak, days(report.streak)))
            append(resources.getString(R.string.report_share_mastered, report.masteredWords))
            if (report.needsPractice.isNotEmpty()) {
                appendLine()
                append(resources.getString(R.string.report_share_practice, report.needsPractice.joinToString { it.word }))
            }
        }
    }

    private fun minutes(count: Int) = resources.getQuantityString(R.plurals.report_minutes, count, count)

    private fun days(count: Int) = resources.getQuantityString(R.plurals.report_days, count, count)

    private fun updateSettings(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch { progressRepository.updateSettings(transform) }
    }

    companion object {
        const val MaxNameLength = 20

        val Factory = appViewModelFactory { container ->
            GrownupsViewModel(container.progressRepository, container.contentRepository, container.resources)
        }
    }
}
