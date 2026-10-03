package com.childrenbooks.ui.about

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.R
import com.childrenbooks.ui.components.AppActionRowButton
import com.childrenbooks.ui.components.AppPillButton
import com.childrenbooks.ui.theme.AppColors

private data class SettingsAction(
    @param:StringRes val titleRes: Int,
    val icon: ImageVector,
    val accent: Color,
    val onClick: () -> Unit,
)

@Composable
fun AboutScreen(
    onMoreApps: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onRateApp: () -> Unit,
    onShare: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val actions = listOf(
        SettingsAction(R.string.drawer_more_apps, Icons.Rounded.Apps, AppColors.BrandPurple, onMoreApps),
        SettingsAction(
            R.string.drawer_privacy_policy,
            Icons.Rounded.Policy,
            AppColors.BrandBlue,
            settingsViewModel::togglePrivacySummary,
        ),
        SettingsAction(R.string.drawer_rate_this_app, Icons.Rounded.StarRate, AppColors.BrandPink, onRateApp),
        SettingsAction(R.string.drawer_share, Icons.Rounded.Share, Color(0xFFFF8A00), onShare),
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.ScreenBackground),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SettingsHeader()
        }
        items(actions, key = { it.titleRes }) { action ->
            SettingsActionRow(action)
            if (action.titleRes == R.string.drawer_privacy_policy && uiState.isPrivacySummaryVisible) {
                PrivacySafetyCard(onOpenFullPolicy = onPrivacyPolicy)
            }
        }
    }
}

@Composable
private fun SettingsHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(AppColors.BrandPurple, AppColors.BrandBlue),
                    ),
                )
                .padding(22.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.nav_settings),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
                Text(
                    text = stringResource(R.string.settings_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.88f),
                )
            }
        }
    }
}

@Composable
private fun SettingsActionRow(action: SettingsAction) {
    AppActionRowButton(
        title = stringResource(action.titleRes),
        icon = action.icon,
        accent = action.accent,
        onClick = action.onClick,
    )
}

@Composable
private fun PrivacySafetyCard(
    onOpenFullPolicy: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AppColors.BrandBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = AppColors.BrandBlue,
                    )
                }
                Column {
                    Text(
                        text = stringResource(R.string.privacy_safety_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.TextHomeTitle,
                    )
                    Text(
                        text = stringResource(R.string.privacy_safety_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.TextMuted,
                    )
                }
            }
            PrivacyBullet(text = stringResource(R.string.privacy_safety_no_ads))
            PrivacyBullet(text = stringResource(R.string.privacy_safety_no_tracking))
            PrivacyBullet(text = stringResource(R.string.privacy_safety_parent_gate))
            PrivacyBullet(text = stringResource(R.string.privacy_safety_external_links))
            AppPillButton(
                text = stringResource(R.string.privacy_open_full_policy),
                onClick = onOpenFullPolicy,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PrivacyBullet(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "•",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = AppColors.BrandBlue,
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextHomeTitle,
        )
    }
}
