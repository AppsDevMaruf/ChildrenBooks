package com.childrenbooks.feature.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.childrenbooks.R
import com.childrenbooks.appViewModelFactory
import com.childrenbooks.core.audio.Narrator
import com.childrenbooks.core.designsystem.KidColors
import com.childrenbooks.core.designsystem.KidShapes
import com.childrenbooks.core.designsystem.KidSizes
import com.childrenbooks.core.designsystem.KidType
import com.childrenbooks.core.designsystem.components.KidButton
import com.childrenbooks.core.designsystem.components.KidCard
import com.childrenbooks.core.designsystem.components.KidIconButton
import com.childrenbooks.data.content.AudioClip
import com.childrenbooks.data.progress.AgeBand
import com.childrenbooks.data.progress.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val progress: ProgressRepository,
    private val narrator: Narrator,
    private val strings: (Int) -> String,
) : ViewModel() {
    private val _ageBand = MutableStateFlow(AgeBand.Young)
    val ageBand: StateFlow<AgeBand> = _ageBand.asStateFlow()

    fun speak(textRes: Int) = narrator.speak(AudioClip.Spoken(strings(textRes)))

    fun selectAge(band: AgeBand, labelRes: Int) {
        _ageBand.value = band
        speak(labelRes)
    }

    fun finish(onDone: () -> Unit) {
        viewModelScope.launch {
            progress.updateSettings { it.copy(onboardingDone = true, ageBand = _ageBand.value) }
            onDone()
        }
    }

    override fun onCleared() = narrator.stop()

    companion object {
        val Factory = appViewModelFactory { container ->
            OnboardingViewModel(container.progressRepository, container.narrator, container.resources::getString)
        }
    }
}

/** First run: a hello, then "Who is learning today?" (age band). No account, no permissions. */
@Composable
fun OnboardingRoute(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModel.Factory),
) {
    val ageBand by viewModel.ageBand.collectAsStateWithLifecycle()
    var step by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(step) {
        viewModel.speak(if (step == 0) R.string.onboarding_hello_spoken else R.string.onboarding_who)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.width(64.dp))
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            ) {
                StepDot(active = step == 0, done = step > 0)
                StepDot(active = step == 1, done = false)
            }
            TextButton(onClick = { viewModel.finish(onFinished) }, modifier = Modifier.heightIn(min = 48.dp)) {
                Text(stringResource(R.string.action_skip), style = KidType.Caption, color = KidColors.InkVariant)
            }
        }

        Spacer(Modifier.height(24.dp))
        KidCard(onClick = null, role = null, shape = KidShapes.Card, modifier = Modifier.size(164.dp)) {
            Image(
                painter = painterResource(R.drawable.splash_logo),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
            )
        }
        Spacer(Modifier.height(28.dp))

        if (step == 0) {
            Text(
                text = stringResource(R.string.onboarding_hello),
                style = KidType.Title,
                color = KidColors.Ink,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.onboarding_hello_body),
                style = KidType.Body,
                color = KidColors.InkVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(20.dp))
            ReplayButton { viewModel.speak(R.string.onboarding_hello_spoken) }
            Spacer(Modifier.height(48.dp))
            KidButton(
                text = stringResource(R.string.onboarding_start),
                onClick = { step = 1 },
                trailingIcon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(
                text = stringResource(R.string.onboarding_who),
                style = KidType.Title,
                color = KidColors.Ink,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            ReplayButton { viewModel.speak(R.string.onboarding_who) }
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AgeCard(
                    selected = ageBand == AgeBand.Young,
                    image = R.drawable.alphabet_example_bee,
                    range = stringResource(R.string.age_young_range),
                    name = stringResource(R.string.age_young_name),
                    onClick = { viewModel.selectAge(AgeBand.Young, R.string.age_young_spoken) },
                    modifier = Modifier.weight(1f),
                )
                AgeCard(
                    selected = ageBand == AgeBand.Older,
                    image = R.drawable.alphabet_example_cat,
                    range = stringResource(R.string.age_older_range),
                    name = stringResource(R.string.age_older_name),
                    onClick = { viewModel.selectAge(AgeBand.Older, R.string.age_older_spoken) },
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.onboarding_change_later),
                style = KidType.Small,
                color = KidColors.InkVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(40.dp))
            KidButton(
                text = stringResource(R.string.onboarding_lets_go),
                onClick = { viewModel.finish(onFinished) },
                trailingIcon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun StepDot(active: Boolean, done: Boolean) {
    Box(
        Modifier
            .width(if (active) 28.dp else 10.dp)
            .height(10.dp)
            .background(
                color = when {
                    active -> KidColors.Primary
                    done -> KidColors.Primary.copy(alpha = 0.45f)
                    else -> KidColors.Track
                },
                shape = KidShapes.Pill,
            ),
    )
}

@Composable
private fun ReplayButton(onClick: () -> Unit) {
    KidIconButton(
        icon = Icons.AutoMirrored.Rounded.VolumeUp,
        contentDescription = stringResource(R.string.cd_hear_again),
        onClick = onClick,
        container = KidColors.PrimaryContainer,
        contentColor = KidColors.Primary,
        lipColor = KidColors.PrimaryLip.copy(alpha = 0.18f),
    )
}

@Composable
private fun AgeCard(
    selected: Boolean,
    @DrawableRes image: Int,
    range: String,
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    KidCard(
        onClick = onClick,
        modifier = modifier.semantics { this.selected = selected },
        color = if (selected) KidColors.PrimaryContainer else KidColors.Surface,
        lipColor = if (selected) KidColors.PrimaryLip.copy(alpha = 0.22f) else KidColors.Lip,
        border = if (selected) BorderStroke(3.dp, KidColors.Primary) else null,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = KidSizes.TargetPrimary * 3.5f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            Image(painterResource(image), contentDescription = null, modifier = Modifier.size(96.dp))
            Text(range, style = KidType.Title, color = if (selected) KidColors.OnPrimaryContainer else KidColors.Ink)
            Text(name, style = KidType.Caption, color = if (selected) KidColors.OnPrimaryContainer else KidColors.InkVariant, textAlign = TextAlign.Center)
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(32.dp)
                    .background(KidColors.Primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null, tint = KidColors.OnPrimary, modifier = Modifier.size(18.dp))
            }
        }
    }
}
