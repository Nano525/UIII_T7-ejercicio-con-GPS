package mx.edu.utez.uiii_t7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import mx.edu.utez.uiii_t7.ui.AppNavigation
import mx.edu.utez.uiii_t7.ui.theme.UIII_T7Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UIII_T7Theme {
                AppNavigation()
            }
        }
    }
}