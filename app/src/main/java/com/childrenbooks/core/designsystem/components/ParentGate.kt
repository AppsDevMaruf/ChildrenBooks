package com.childrenbooks.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.childrenbooks.R
import com.childrenbooks.core.designsystem.GrownType
import com.childrenbooks.core.designsystem.KidColors
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * A question an adult answers easily and a pre-reader cannot: a random two-digit sum,
 * written in words. A new question appears after every miss.
 */
data class ParentGateQuestion(
    val first: Int,
    val second: Int,
    val options: List<Int>,
) {
    val answer: Int get() = first + second

    companion object {
        fun generate(random: Random): ParentGateQuestion {
            val first = random.nextInt(21, 70)
            val second = random.nextInt(11, 30)
            val answer = first + second
            return ParentGateQuestion(
                first = first,
                second = second,
                options = listOf(answer, answer - 10, answer + 10).shuffled(random),
            )
        }
    }
}

/** English number words for 0–99, used by the parent gate question. */
object NumberWords {
    private val ones = listOf(
        "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
        "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen",
    )
    private val tens = listOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")

    fun toWords(number: Int): String {
        require(number in 0..99) { "Only 0 to 99 is supported" }
        if (number < 20) return ones[number]
        val ten = tens[number / 10]
        val unit = number % 10
        return if (unit == 0) ten else "$ten-${ones[unit]}"
    }
}

const val ParentGateMaxMisses = 3
const val ParentGateLockMillis = 30_000L

/**
 * Covers the whole Grown-ups area and every action that leaves the app. Required by
 * Google Play Families and the App Store Kids category.
 */
@Composable
fun ParentGateDialog(
    onPassed: () -> Unit,
    onDismiss: () -> Unit,
) {
    var seed by rememberSaveable { mutableIntStateOf(Random.nextInt()) }
    var misses by rememberSaveable { mutableIntStateOf(0) }
    var showError by rememberSaveable { mutableIntStateOf(0) }
    var lockedUntil by rememberSaveable { mutableLongStateOf(0L) }
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val question = remember(seed) { ParentGateQuestion.generate(Random(seed)) }

    LaunchedEffect(lockedUntil) {
        now = System.currentTimeMillis()
        while (now < lockedUntil) {
            delay(500)
            now = System.currentTimeMillis()
        }
    }
    val locked = now < lockedUntil

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = KidColors.Surface) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(KidColors.TertiaryContainer, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Rounded.Lock, contentDescription = null, tint = KidColors.Tertiary)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Rounded.Close, contentDescription = stringResource(R.string.cd_close), tint = KidColors.Ink)
                    }
                }
                Text(stringResource(R.string.gate_title), style = GrownType.Title.copy(fontSize = 24.sp, lineHeight = 30.sp), color = KidColors.Ink)
                Text(stringResource(R.string.gate_body), style = GrownType.Body, color = KidColors.InkVariant)

                if (locked) {
                    val seconds = ((lockedUntil - now + 999) / 1000).toInt()
                    Text(
                        text = stringResource(R.string.gate_locked, seconds),
                        style = GrownType.Label,
                        color = KidColors.Error,
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KidColors.Background, RoundedCornerShape(20.dp))
                            .border(BorderStroke(1.dp, KidColors.Divider), RoundedCornerShape(20.dp))
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                    ) {
                        Text(stringResource(R.string.gate_what_is), style = GrownType.Label, color = KidColors.InkVariant)
                        Text(
                            text = stringResource(
                                R.string.gate_question,
                                NumberWords.toWords(question.first),
                                NumberWords.toWords(question.second),
                            ),
                            style = GrownType.Title.copy(fontSize = 22.sp, lineHeight = 28.sp),
                            color = KidColors.Ink,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        question.options.forEach { option ->
                            OutlinedButton(
                                onClick = {
                                    if (option == question.answer) {
                                        onPassed()
                                    } else {
                                        misses += 1
                                        showError += 1
                                        if (misses >= ParentGateMaxMisses) {
                                            misses = 0
                                            lockedUntil = System.currentTimeMillis() + ParentGateLockMillis
                                        }
                                        seed = Random.nextInt()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(2.dp, KidColors.Outline),
                            ) {
                                Text(option.toString(), style = GrownType.Metric.copy(fontSize = 22.sp), color = KidColors.Ink)
                            }
                        }
                    }
                    if (showError > 0) {
                        Row(
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = KidColors.Error, modifier = Modifier.size(20.dp))
                            Text(stringResource(R.string.gate_wrong), style = GrownType.Label, color = KidColors.Error)
                        }
                    }
                }
                Text(stringResource(R.string.gate_footnote), style = GrownType.Caption, color = KidColors.InkVariant)
            }
        }
    }
}
