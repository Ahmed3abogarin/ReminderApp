package com.ahmed.reminderapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun TaskDateTimePicker(onTimeChanged: (LocalTime)->Unit, onDateChanged: (LocalDate)->Unit) {
    val context = LocalContext.current
    val now = LocalDateTime.now()

    var selectedDate by remember { mutableStateOf(now.toLocalDate()) }
    var selectedTime by remember { mutableStateOf(now.toLocalTime().withSecond(0).withNano(0)) }


    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { showDatePicker.value = true }) {
            Text("Pick Date: $selectedDate")
        }

        Button(onClick = { showTimePicker.value = true }) {
            Text("Pick Time: $selectedTime")
        }

    }

    if (showDatePicker.value) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedDate = LocalDate.of(year, month + 1, day)
                onDateChanged(selectedDate)
                showDatePicker.value = false
            },
            now.year,
            now.monthValue - 1,
            now.dayOfMonth
        ).show()
    }

    if (showTimePicker.value) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                selectedTime = LocalTime.of(hour, minute)
                onTimeChanged(selectedTime)
                showTimePicker.value = false
            },
            now.hour,
            now.minute,
            true
        ).show()
    }
}