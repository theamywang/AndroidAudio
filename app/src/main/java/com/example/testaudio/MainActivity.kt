package com.example.testaudio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.testaudio.ui.theme.TestAudioTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    private lateinit var audioPlayerHelper: AudioPlayerHelper

    override fun onDestroy() {
        super.onDestroy()
        audioPlayerHelper.release()
    }

    // For when the user has paused the audio on the lockscreen and foregrounds the app again
    // This will need to be done on the TypeScript side
    override fun onResume() {
        super.onResume()
        audioPlayerHelper.calibrateIsPlaying()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        audioPlayerHelper = AudioPlayerHelper(this)
        val base64String = readBase64FromRaw(this)
        audioPlayerHelper.setAudio(base64String)

        setContent {
            TestAudioTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlayButton(audioPlayerHelper)
                        FastForwardButton(audioPlayerHelper)
                        RewindButton(audioPlayerHelper)
                        PlaybackSpeedMenu(audioPlayerHelper)
                    }
                }
            }
        }
    }
}

@Composable
fun PlaybackSpeedMenu(
    audioPlayerHelper: AudioPlayerHelper,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedSpeed by remember { mutableStateOf(1.0f) } // Default speed
    val playbackSpeeds = listOf(0.5f, 1.0f, 1.5f, 2.0f)

    Column(modifier = modifier) {
        Button(onClick = { expanded = true }) {
            Text("Playback Speed: ${selectedSpeed}x")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            playbackSpeeds.forEach { speed ->
                DropdownMenuItem(
                    text = { Text(text = "${speed}x") },
                    onClick = {
                        selectedSpeed = speed
                        audioPlayerHelper.setPlaybackSpeed(speed)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun RewindButton(
    audioPlayerHelper: AudioPlayerHelper
) {
    Button(
        onClick = {
            audioPlayerHelper.rewind(5)
        }
    ) {
        Icon(
            painter = painterResource(android.R.drawable.ic_media_rew),
            contentDescription = "Rewind",
            tint = Color.Blue
        )
    }
}

@Composable
fun FastForwardButton(
    audioPlayerHelper: AudioPlayerHelper
) {
    Button(
        onClick = {
            audioPlayerHelper.fastForward(5)
        }
    ) {
        Icon(
            painter = painterResource(android.R.drawable.ic_media_ff),
            contentDescription = "Fast forward",
            tint = Color.Blue
        )
    }
}

@Composable
fun PlayButton(
    audioPlayerHelper: AudioPlayerHelper
) {
    val isPlaying by audioPlayerHelper.isPlaying.observeAsState()

    Button(
        onClick = {
            if (isPlaying == true) {
                audioPlayerHelper.pause()
            } else {
                audioPlayerHelper.play()
            }
        }
    ) {
        Icon(
            painter = painterResource(
                id = if (isPlaying == true) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            ),
            contentDescription = "Play",
            tint = Color.Blue
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PlayPreview() {
//    TestAudioTheme {
//    }
//}