/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class CountdownViewModel : ViewModel() {
    private val _timeRemaining = MutableLiveData(10)
    val timeRemaining: LiveData<Int> = _timeRemaining

    private val _timerIsRunning = MutableLiveData(false)
    val timerIsRunning: LiveData<Boolean> = _timerIsRunning

    private val _timerIsFinished = MutableLiveData(false)
    val timerIsFinished: LiveData<Boolean> = _timerIsFinished

    private val timer = object : CountDownTimer(10000, 1) {
        override fun onTick(millisUntilFinished: Long) {
            onTimeRemainingChanged((millisUntilFinished.toInt() / 1000) + 1)
        }

        override fun onFinish() {
            _timerIsRunning.value = false
            _timerIsFinished.value = true
        }
    }

    fun onTimeRemainingChanged(newTime: Int) {
        _timeRemaining.value = newTime
    }

    fun startTimer() {
        timer.start()
        _timerIsRunning.value = true
        _timerIsFinished.value = false
    }
}

@Composable
fun CountdownScreen(countdownViewModel: CountdownViewModel = viewModel()) {
    val timeRemaining: Int by countdownViewModel.timeRemaining.observeAsState(10)
    val timerIsRunning: Boolean by countdownViewModel.timerIsRunning.observeAsState(false)
    val showRocket: Boolean by countdownViewModel.timerIsFinished.observeAsState(false)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "10 Second Countdown") }
                )
            },
            content = {
                CountdownContent(
                    timeRemaining = timeRemaining,
                    onClickButton = { countdownViewModel.startTimer() },
                    timerIsRunning = timerIsRunning,
                    timerIsFinished = showRocket
                )
            }
        )
    }
}

@Composable
fun CountdownContent(
    timeRemaining: Int,
    onClickButton: () -> Unit,
    timerIsRunning: Boolean,
    timerIsFinished: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (timerIsFinished) {
            Text(
                text = "🚀",
                style = MaterialTheme.typography.h1,
                textAlign = TextAlign.Center
            )
        }
        CountdownText(value = timeRemaining, timerIsFinished = timerIsFinished)
        Spacer(modifier = Modifier.height(20.dp))
        if (!timerIsRunning) {
            Button(
                onClick = { onClickButton() }
            ) {
                Text(text = "Start launch sequence")
            }
        }
    }
}

@Composable
fun CountdownText(value: Int, timerIsFinished: Boolean) {
    val textToShow = if (!timerIsFinished) "$value" else "Lift off!"

    Text(
        text = textToShow,
        style = MaterialTheme.typography.h1,
        textAlign = TextAlign.Center
    )
}

@Composable
@Preview(showSystemUi = true)
fun CountdownContentPreview() {
    CountdownContent(
        timeRemaining = 10000,
        onClickButton = { },
        timerIsRunning = false,
        timerIsFinished = true
    )
}
