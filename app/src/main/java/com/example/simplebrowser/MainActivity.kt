package com.example.simplebrowser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrowserMainScreen()
        }
    }
}

@Composable
fun BrowserMainScreen() {
    var url by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("输入网址") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (url.isNotBlank()) {
                    val shortcutManager = context.getSystemService(Context.SHORTCUT_SERVICE) as android.content.pm.ShortcutManager
                    val shortcut = android.content.pm.ShortcutInfo.Builder(context, url)
                        .setShortLabel("网页快捷方式")
                        .setLongLabel(url)
                        .setIcon(android.graphics.drawable.Icon.createWithResource(context, android.R.drawable.ic_menu_view))
                        .setIntent(
                            Intent(context, WebViewActivity::class.java)
                                .setAction(Intent.ACTION_VIEW)
                                .putExtra("url", url)
                        )
                        .build()
                    shortcutManager.requestPinShortcut(shortcut, null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("生成快捷方式")
        }
    }
}