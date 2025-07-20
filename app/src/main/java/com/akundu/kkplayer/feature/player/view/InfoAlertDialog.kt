package com.akundu.kkplayer.feature.player.view

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun InfoAlertDialog(openDialog: MutableState<Boolean>, title: String, body: String) {

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = body)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        // Handle confirm action
                    }
                ) {
                    Text("Okay")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        // Handle dismiss action
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}