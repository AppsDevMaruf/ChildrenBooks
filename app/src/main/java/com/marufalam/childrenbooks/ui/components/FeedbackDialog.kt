package com.marufalam.childrenbooks.ui.components

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

@Composable
fun FeedbackDialog(
    sending: Boolean,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var feedback by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Your suggestion") },
        text = {
            OutlinedTextField(
                value = feedback,
                onValueChange = { feedback = it },
                label = { Text("How can we improve?") },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                enabled = feedback.isNotBlank() && !sending,
                onClick = { onSubmit(feedback) },
            ) {
                Text(if (sending) "Sending…" else "Send")
            }
        },
        dismissButton = {
            TextButton(enabled = !sending, onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
