@file:OptIn(ExperimentalComposeUiApi::class)

package com.childrenbooks.feature.grownups

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.GrownType
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.components.GrownCard
import com.childrenbooks.core.designsystem.components.GrownDivider
import com.childrenbooks.core.designsystem.components.GrownSectionTitle
import com.childrenbooks.core.designsystem.components.GrownSettingRow
import com.childrenbooks.core.designsystem.components.GrownSummaryBar
import com.childrenbooks.core.designsystem.components.GrownSwitchRow
import com.childrenbooks.core.designsystem.components.MasteryKind
import com.childrenbooks.core.designsystem.components.MasteryLegendItem
import com.childrenbooks.core.designsystem.components.MetricCard
import com.childrenbooks.core.platform.AppLinks
import com.childrenbooks.core.platform.appVersionName
import com.childrenbooks.core.platform.openUri
import com.childrenbooks.core.platform.shareText
import com.childrenbooks.data.content.WorldId
import com.childrenbooks.data.progress.AgeBand
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.TimeZone

// ---------------------------------------------------------------------------------------------
// Report
// ---------------------------------------------------------------------------------------------

@Composable
fun GrownupsReportRoute(
    onHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCard: (WorldId, Int, Int) -> Unit,
    viewModel: GrownupsViewModel = viewModel(factory = GrownupsViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val report = state.report
    val shareTitle = stringResource(R.string.report_share_chooser)
    Column(
        Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .statusBarsPadding(),
    ) {
        GrownTopBar(
            title = stringResource(R.string.grownups_title),
            onBack = onHome,
            backDescription = stringResource(R.string.cd_back_to_home),
            trailing = {
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Rounded.Settings, contentDescription = stringResource(R.string.nav_settings), tint = KidColors.Ink)
                }
            },
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                val who = state.settings.childName.ifBlank { stringResource(R.string.report_your_child) }
                Text(
                    text = stringResource(R.string.report_heading, who),
                    style = GrownType.Title,
                    color = KidColors.Ink,
                    modifier = Modifier.semantics { heading() },
                )
            }
            item {
                SegmentedToggle(
                    options = listOf(
                        ReportRange.Week to stringResource(R.string.report_week),
                        ReportRange.Month to stringResource(R.string.report_month),
                    ),
                    selected = report.range,
                    onSelect = viewModel::setRange,
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        value = report.totalMinutes.toString(),
                        unit = stringResource(R.string.unit_minutes_short),
                        label = stringResource(R.string.report_metric_time),
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        value = report.setsFinished.toString(),
                        unit = "",
                        label = stringResource(R.string.report_metric_sets),
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        value = report.streak.toString(),
                        unit = pluralStringResource(R.plurals.unit_days, report.streak),
                        label = stringResource(R.string.report_metric_streak),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            item {
                GrownCard {
                    GrownSectionTitle(stringResource(R.string.report_minutes_each_day))
                    MinutesChart(bars = report.bars, range = report.range)
                }
            }
            item {
                GrownCard {
                    GrownSectionTitle(stringResource(R.string.report_words_by_world))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        MasteryLegendItem(MasteryKind.Mastered)
                        MasteryLegendItem(MasteryKind.Practising)
                        MasteryLegendItem(MasteryKind.New)
                    }
                    report.worlds.forEach { world -> WorldMasteryRow(world) }
                }
            }
            item {
                GrownCard {
                    GrownSectionTitle(stringResource(R.string.report_needs_practice))
                    if (report.needsPractice.isEmpty()) {
                        Text(stringResource(R.string.report_needs_practice_empty), style = GrownType.Body, color = KidColors.InkVariant)
                    } else {
                        Text(stringResource(R.string.report_needs_practice_hint), style = GrownType.Body, color = KidColors.InkVariant)
                        PracticeChips(words = report.needsPractice, onOpen = { onOpenCard(it.worldId, it.setIndex, it.cardIndex) })
                    }
                }
            }
        }
        GrownSummaryBar(
            caption = stringResource(if (report.range == ReportRange.Week) R.string.report_this_week else R.string.report_this_month),
            summary = stringResource(
                R.string.report_summary,
                pluralStringResource(R.plurals.report_minutes, report.totalMinutes, report.totalMinutes),
                pluralStringResource(R.plurals.report_sets, report.setsFinished, report.setsFinished),
            ),
        ) {
            Button(
                onClick = { context.shareText(viewModel.shareText(), shareTitle) },
                enabled = !state.loading,
                modifier = Modifier.heightIn(min = 48.dp),
            ) {
                Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(8.dp))
                Text(stringResource(R.string.report_share), style = GrownType.Label)
            }
        }
    }
}

@Composable
private fun MinutesChart(bars: List<DayBar>, range: ReportRange) {
    if (bars.isEmpty()) return
    val maxMinutes = bars.maxOf { it.minutes }.coerceAtLeast(10)
    val total = bars.sumOf { it.minutes }
    val description = if (range == ReportRange.Week) {
        bars.joinToString { "${weekdayShort(it.day)} ${it.minutes}" }
    } else {
        pluralStringResource(R.plurals.report_minutes, total, total)
    }
    val gap = if (range == ReportRange.Week) 10.dp else 3.dp
    Column(Modifier.semantics(mergeDescendants = true) { contentDescription = description }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(132.dp),
            horizontalArrangement = Arrangement.spacedBy(gap),
            verticalAlignment = Alignment.Bottom,
        ) {
            bars.forEach { bar ->
                val fraction = (bar.minutes.toFloat() / maxMinutes).coerceIn(0.03f, 1f)
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(fraction)
                        .background(
                            color = if (bar.isToday) KidColors.ChartToday else KidColors.ChartBar,
                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                        ),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            val lastIndex = bars.lastIndex
            bars.forEachIndexed { index, bar ->
                val label = when {
                    range == ReportRange.Week -> weekdayShort(bar.day)
                    (lastIndex - index) % 7 == 0 -> dayOfMonth(bar.day).toString()
                    else -> ""
                }
                Text(
                    text = label,
                    style = GrownType.Caption,
                    color = if (bar.isToday) KidColors.Ink else KidColors.InkVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun WorldMasteryRow(world: WorldMastery) {
    val title = stringResource(world.title)
    val summary = stringResource(R.string.report_world_progress, world.mastered, world.total)
    val description = stringResource(R.string.cd_world_mastery, title, world.mastered, world.practising, world.total)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = description },
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = GrownType.Label, color = KidColors.Ink, modifier = Modifier.weight(1f))
            Text(summary, style = GrownType.Caption, color = KidColors.InkVariant)
        }
        val total = world.total.coerceAtLeast(1).toFloat()
        val masteredFraction = world.mastered / total
        val practisingFraction = world.practising / total
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(10.dp),
        ) {
            val radius = CornerRadius(size.height / 2f)
            drawRoundRect(color = KidColors.Track, cornerRadius = radius)
            val practisingEnd = size.width * (masteredFraction + practisingFraction)
            if (practisingEnd > 0f) {
                drawRoundRect(color = KidColors.ChartBar, size = Size(practisingEnd, size.height), cornerRadius = radius)
            }
            val masteredEnd = size.width * masteredFraction
            if (masteredEnd > 0f) {
                drawRoundRect(color = KidColors.Primary, size = Size(masteredEnd, size.height), cornerRadius = radius)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PracticeChips(words: List<PracticeWord>, onOpen: (PracticeWord) -> Unit) {
    val openLabel = stringResource(R.string.cd_open_lesson)
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        words.forEach { word ->
            Box(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clip(KidShapes.Pill)
                    .background(KidColors.SecondaryContainer)
                    .clickable(onClickLabel = openLabel, role = Role.Button) { onOpen(word) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(word.word, style = GrownType.Label, color = KidColors.OnSecondaryContainer)
            }
        }
    }
}

private fun utcCalendar(day: Long): Calendar =
    Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = day * 86_400_000L }

private fun weekdayShort(day: Long): String =
    DateFormatSymbols.getInstance().shortWeekdays[utcCalendar(day).get(Calendar.DAY_OF_WEEK)]

private fun dayOfMonth(day: Long): Int = utcCalendar(day).get(Calendar.DAY_OF_MONTH)

// ---------------------------------------------------------------------------------------------
// Settings
// ---------------------------------------------------------------------------------------------

@Composable
fun GrownupsSettingsRoute(
    onBack: () -> Unit,
    viewModel: GrownupsViewModel = viewModel(factory = GrownupsViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = state.settings
    val context = LocalContext.current
    val version = remember(context) { context.appVersionName() }
    val shareChooser = stringResource(R.string.share_children_books)
    var editingName by rememberSaveable { mutableStateOf(false) }
    var confirmingReset by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        GrownTopBar(
            title = stringResource(R.string.nav_settings),
            onBack = onBack,
            backDescription = stringResource(R.string.cd_back),
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { SettingsHeader(stringResource(R.string.settings_section_child)) }
            item {
                GrownCard {
                    GrownSettingRow(
                        icon = Icons.Rounded.Face,
                        title = stringResource(R.string.settings_child_name),
                        subtitle = settings.childName.ifBlank { stringResource(R.string.settings_child_name_empty) },
                        onClick = { editingName = true },
                    )
                    GrownDivider()
                    Text(stringResource(R.string.settings_age), style = GrownType.Label, color = KidColors.Ink)
                    SegmentedToggle(
                        options = listOf(
                            AgeBand.Young to stringResource(R.string.age_young_range),
                            AgeBand.Older to stringResource(R.string.age_older_range),
                        ),
                        selected = settings.ageBand,
                        onSelect = viewModel::setAgeBand,
                    )
                    Text(stringResource(R.string.settings_age_hint), style = GrownType.Caption, color = KidColors.InkVariant)
                    GrownDivider()
                    GrownSwitchRow(
                        icon = Icons.Rounded.TextFields,
                        title = stringResource(R.string.settings_show_words),
                        subtitle = stringResource(R.string.settings_show_words_hint),
                        checked = settings.showWords,
                        onCheckedChange = viewModel::setShowWords,
                    )
                    GrownDivider()
                    GrownSwitchRow(
                        icon = Icons.Rounded.Bedtime,
                        title = stringResource(R.string.settings_bedtime),
                        subtitle = stringResource(R.string.settings_bedtime_hint),
                        checked = settings.bedtime,
                        onCheckedChange = viewModel::setBedtime,
                    )
                }
            }
            item { SettingsHeader(stringResource(R.string.settings_section_privacy)) }
            item {
                GrownCard {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Icon(Icons.Rounded.Shield, contentDescription = null, tint = KidColors.Correct, modifier = Modifier.size(24.dp))
                        Text(stringResource(R.string.settings_privacy_summary), style = GrownType.Body, color = KidColors.Ink)
                    }
                    GrownDivider()
                    GrownSettingRow(
                        icon = Icons.Rounded.Policy,
                        title = stringResource(R.string.settings_privacy_policy),
                        onClick = { context.openUri(AppLinks.PrivacyPolicy) },
                    )
                }
            }
            item { SettingsHeader(stringResource(R.string.settings_section_about)) }
            item {
                GrownCard {
                    GrownSettingRow(
                        icon = Icons.Rounded.StarRate,
                        title = stringResource(R.string.settings_rate),
                        onClick = { context.openUri(AppLinks.storePage(context.packageName)) },
                    )
                    GrownDivider()
                    GrownSettingRow(
                        icon = Icons.Rounded.Share,
                        title = stringResource(R.string.settings_share_app),
                        onClick = { context.shareText(AppLinks.storePage(context.packageName), shareChooser) },
                    )
                    GrownDivider()
                    GrownSettingRow(
                        icon = Icons.Rounded.Apps,
                        title = stringResource(R.string.settings_more_apps),
                        onClick = { context.openUri(AppLinks.MoreApps) },
                    )
                    GrownDivider()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = KidColors.InkVariant)
                        Text(stringResource(R.string.settings_version, version), style = GrownType.Body, color = KidColors.InkVariant)
                    }
                }
            }
            item { SettingsHeader(stringResource(R.string.settings_section_progress)) }
            item {
                GrownCard {
                    GrownSettingRow(
                        icon = Icons.Rounded.RestartAlt,
                        title = stringResource(R.string.settings_reset),
                        subtitle = stringResource(R.string.settings_reset_hint),
                        onClick = { confirmingReset = true },
                    )
                }
            }
        }
    }

    if (editingName) {
        ChildNameDialog(
            initial = settings.childName,
            onSave = {
                viewModel.setChildName(it)
                editingName = false
            },
            onDismiss = { editingName = false },
        )
    }
    if (confirmingReset) {
        AlertDialog(
            onDismissRequest = { confirmingReset = false },
            icon = { Icon(Icons.Rounded.RestartAlt, contentDescription = null) },
            title = { Text(stringResource(R.string.settings_reset_confirm_title)) },
            text = { Text(stringResource(R.string.settings_reset_confirm_body)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetProgress()
                        confirmingReset = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KidColors.Error, contentColor = Color.White),
                ) { Text(stringResource(R.string.settings_reset_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmingReset = false }) { Text(stringResource(R.string.action_cancel)) }
            },
        )
    }
}

@Composable
private fun ChildNameDialog(initial: String, onSave: (String) -> Unit, onDismiss: () -> Unit) {
    var draft by rememberSaveable { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_child_name)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.settings_child_name_hint), style = GrownType.Body, color = KidColors.InkVariant)
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it.take(GrownupsViewModel.MaxNameLength) },
                    singleLine = true,
                    label = { Text(stringResource(R.string.settings_child_name)) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                )
            }
        },
        confirmButton = { Button(onClick = { onSave(draft) }) { Text(stringResource(R.string.action_save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) } },
    )
}

// ---------------------------------------------------------------------------------------------
// Shared pieces
// ---------------------------------------------------------------------------------------------

@Composable
private fun GrownTopBar(
    title: String,
    onBack: () -> Unit,
    backDescription: String,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = backDescription, tint = KidColors.Ink)
        }
        Text(
            text = title,
            style = GrownType.Title,
            color = KidColors.Ink,
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .semantics { heading() },
        )
        trailing()
    }
}

@Composable
private fun SettingsHeader(text: String) {
    GrownSectionTitle(
        text = text,
        modifier = Modifier
            .padding(top = 14.dp, start = 4.dp)
            .semantics { heading() },
    )
}

/** Two- or three-way segmented control; each segment is a 48 dp tab. */
@Composable
private fun <T> SegmentedToggle(options: List<Pair<T, String>>, selected: T, onSelect: (T) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KidColors.SurfaceMuted, KidShapes.Pill)
            .padding(4.dp)
            .selectableGroup(),
    ) {
        options.forEach { (value, label) ->
            val isSelected = value == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .clip(KidShapes.Pill)
                    .background(if (isSelected) KidColors.Surface else Color.Transparent)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(value) }),
                contentAlignment = Alignment.Center,
            ) {
                Text(label, style = GrownType.Label, color = if (isSelected) KidColors.Ink else KidColors.InkVariant)
            }
        }
    }
}
