package com.marufalam.childrenbooks.ui.about

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.marufalam.childrenbooks.R
import com.marufalam.childrenbooks.ui.components.AppActionRowButton
import com.marufalam.childrenbooks.ui.components.AppPillButton
import com.marufalam.childrenbooks.ui.openUri
import com.marufalam.childrenbooks.ui.theme.AppColors

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
    onEmail: () -> Unit,
    onSuggestion: () -> Unit,
) {
    val context = LocalContext.current
    val actions = listOf(
        SettingsAction(R.string.drawer_more_apps, Icons.Rounded.Apps, AppColors.BrandPurple, onMoreApps),
        SettingsAction(R.string.drawer_privacy_policy, Icons.Rounded.Policy, AppColors.BrandBlue, onPrivacyPolicy),
        SettingsAction(R.string.drawer_rate_this_app, Icons.Rounded.StarRate, AppColors.BrandPink, onRateApp),
        SettingsAction(R.string.drawer_share, Icons.Rounded.Share, Color(0xFFFF8A00), onShare),
        SettingsAction(R.string.drawer_email, Icons.Rounded.Email, AppColors.BrandGreen, onEmail),
        SettingsAction(R.string.your_suggestion, Icons.Rounded.Feedback, AppColors.TextHomeTitle, onSuggestion),
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
        }
        item {
            DeveloperCard(
                onEmail = onEmail,
                onWebsite = { context.openUri("https://www.marufalam.com") },
                onFacebook = { context.openUri("https://www.facebook.com/marufalam.porosh.90") },
            )
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
private fun DeveloperCard(
    onEmail: () -> Unit,
    onWebsite: () -> Unit,
    onFacebook: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.maruf),
                contentDescription = stringResource(R.string.developer_name),
                modifier = Modifier
                    .size(118.dp)
                    .clip(RoundedCornerShape(32.dp)),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = stringResource(R.string.developer_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AppColors.TextHomeTitle,
            )
            Text(
                text = stringResource(R.string.developer_role),
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.TextMuted,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AppPillButton(
                    text = stringResource(R.string.drawer_email),
                    modifier = Modifier.weight(1f),
                    onClick = onEmail,
                )
                AppPillButton(
                    text = stringResource(R.string.website),
                    modifier = Modifier.weight(1f),
                    onClick = onWebsite,
                )
                AppPillButton(
                    text = stringResource(R.string.developer_facebook),
                    modifier = Modifier.weight(1f),
                    onClick = onFacebook,
                )
            }
        }
    }
}
