package com.childrenbooks.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FiberManualRecord
import androidx.compose.material.icons.rounded.Timelapse
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.GrownType
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes

/** Calm white card for the grown-ups zone. */
@Composable
fun GrownCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(KidColors.Surface, KidShapes.GrownCard)
            .border(BorderStroke(1.dp, KidColors.Divider), KidShapes.GrownCard)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}

@Composable
fun MetricCard(
    value: String,
    unit: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(KidColors.Surface, KidShapes.GrownCard)
            .border(BorderStroke(1.dp, KidColors.Divider), KidShapes.GrownCard)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) {},
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = GrownType.Metric, color = KidColors.Ink)
            Text(
                text = unit,
                style = GrownType.Label,
                color = KidColors.Ink,
                modifier = Modifier.padding(start = 3.dp, bottom = 4.dp),
            )
        }
        Text(label, style = GrownType.Label.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold), color = KidColors.InkVariant)
    }
}

enum class MasteryKind { Mastered, Practising, New }

/** Mastery status is always a word plus an icon; colour is never the only signal. */
@Composable
fun MasteryLegendItem(kind: MasteryKind, modifier: Modifier = Modifier) {
    val (icon, tint, label) = when (kind) {
        MasteryKind.Mastered -> Triple(Icons.Rounded.Check, KidColors.Primary, R.string.mastery_mastered)
        MasteryKind.Practising -> Triple(Icons.Rounded.Timelapse, KidColors.ChartBar, R.string.mastery_practising)
        MasteryKind.New -> Triple(Icons.Rounded.FiberManualRecord, KidColors.Outline, R.string.mastery_new)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
        Text(stringResource(label), style = GrownType.Caption, color = KidColors.InkVariant)
    }
}

@Composable
fun GrownSectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = GrownType.Section, color = KidColors.Ink, modifier = modifier)
}

/** A tappable settings row with an icon, title, optional subtitle and a chevron. */
@Composable
fun GrownSettingRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        RowIcon(icon)
        Column(Modifier.weight(1f)) {
            Text(title, style = GrownType.Body.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = KidColors.Ink)
            if (subtitle != null) Text(subtitle, style = GrownType.Label.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Normal), color = KidColors.InkVariant)
        }
        Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = KidColors.Outline)
    }
}

/** A settings row with a switch; the whole row toggles. */
@Composable
fun GrownSwitchRow(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(role = Role.Switch) { onCheckedChange(!checked) }
            .semantics(mergeDescendants = true) {
                toggleableState = ToggleableState(checked)
            }
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        RowIcon(icon)
        Column(Modifier.weight(1f)) {
            Text(title, style = GrownType.Body.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold), color = KidColors.Ink)
            if (subtitle != null) Text(subtitle, style = GrownType.Label.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Normal), color = KidColors.InkVariant)
        }
        Switch(checked = checked, onCheckedChange = null)
    }
}

@Composable
private fun RowIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(KidColors.PrimaryContainer, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = KidColors.Primary, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun GrownDivider() {
    HorizontalDivider(color = KidColors.Divider)
}

/** Sticky summary bar for the grown-ups report: a range total and one action. */
@Composable
fun GrownSummaryBar(
    caption: String,
    summary: String,
    modifier: Modifier = Modifier,
    action: @Composable RowScope.() -> Unit,
) {
    Column(modifier.background(KidColors.Surface)) {
        HorizontalDivider(color = KidColors.Divider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .semantics(mergeDescendants = true) { contentDescription = "$caption, $summary" },
            ) {
                Text(caption, style = GrownType.Label.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold), color = KidColors.InkVariant)
                Text(summary, style = GrownType.Metric.copy(fontSize = GrownType.Title.fontSize, lineHeight = GrownType.Title.lineHeight), color = KidColors.Ink)
            }
            action()
        }
    }
}
