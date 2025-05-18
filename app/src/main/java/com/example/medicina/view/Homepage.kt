package com.example.medicina.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.medicina.ui.theme.ComposePracticeTheme
import androidx.core.view.WindowCompat

class Homepage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.setEnterTransition(null)
        window.setExitTransition(null)
        window.setReenterTransition(null)
        window.setReturnTransition(null)
        window.setSharedElementEnterTransition(null)
        window.setSharedElementExitTransition(null)
        setContent {
            ComposePracticeTheme {
                MainScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomepagePreview() {
    ComposePracticeTheme {
        MainScreen()
    }
}

