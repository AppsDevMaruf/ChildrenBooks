package com.childrenbooks.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.childrenbooks.R

private const val ParentGateAnswer = "9"

@Composable
fun ParentGateDialog(
    onApproved: () -> Unit,
    onDismiss: () -> Unit,
) {
    var answer by rememberSaveable { mutableStateOf("") }
    val isCorrect = answer.trim() == ParentGateAnswer

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.parent_gate_title)) },
        text = {
            OutlinedTextField(
                value = answer,
                onValueChange = { answer = it.filter(Char::isDigit).take(2) },
                label = { Text(stringResource(R.string.parent_gate_question)) },
                supportingText = { Text(stringResource(R.string.parent_gate_message)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                enabled = isCorrect,
                onClick = onApproved,
            ) {
                Text(stringResource(R.string.parent_gate_continue))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}
