package com.readyaid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.readyaid.core.theme.ReadyAidTheme
import com.readyaid.core.navigation.ReadyAidNavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadyAidTheme {
                ReadyAidNavGraph()
            }
        }
    }
}
