package uz.itteacher.mygit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import uz.itteacher.mygit.ui.theme.MyGitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyGitTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                  Text(text = "Salom", modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}



