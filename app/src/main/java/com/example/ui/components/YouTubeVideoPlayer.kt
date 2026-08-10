package com.example.ui.components

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun YouTubeVideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                
                // Load the HTML with the iframe
                val html = """
                    <html>
                    <body style="margin:0;padding:0;">
                        <iframe width="100%" height="100%" src="$videoUrl" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                    </body>
                    </html>
                """.trimIndent()
                loadData(html, "text/html", "utf-8")
            }
        },
        update = { webView ->
            val html = """
                    <html>
                    <body style="margin:0;padding:0;">
                        <iframe width="100%" height="100%" src="$videoUrl" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                    </body>
                    </html>
                """.trimIndent()
            webView.loadData(html, "text/html", "utf-8")
        },
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}
