package com.browshere.app

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var addressInput: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var pageTitle: TextView
    private lateinit var securityBadge: TextView
    private lateinit var backButton: ImageButton
    private lateinit var forwardButton: ImageButton
    private lateinit var refreshButton: ImageButton
    private lateinit var homeButton: ImageButton

    private val homeUrl = "https://www.google.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        configureToolbar()
        configureWebView()
        configureBackHandling()

        if (savedInstanceState == null) {
            webView.loadUrl(homeUrl)
        } else {
            webView.restoreState(savedInstanceState)
            syncChrome(webView.url ?: homeUrl, webView.title ?: getString(R.string.app_name), false)
        }
    }

    private fun bindViews() {
        webView = findViewById(R.id.webView)
        addressInput = findViewById(R.id.addressInput)
        progressBar = findViewById(R.id.progressBar)
        pageTitle = findViewById(R.id.pageTitle)
        securityBadge = findViewById(R.id.securityBadge)
        backButton = findViewById(R.id.backButton)
        forwardButton = findViewById(R.id.forwardButton)
        refreshButton = findViewById(R.id.refreshButton)
        homeButton = findViewById(R.id.homeButton)
    }

    private fun configureToolbar() {
        addressInput.setOnEditorActionListener { _, actionId, event ->
            val submit = actionId == EditorInfo.IME_ACTION_GO ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER

            if (submit) {
                navigateFromInput()
            }

            submit
        }

        backButton.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }

        forwardButton.setOnClickListener {
            if (webView.canGoForward()) {
                webView.goForward()
            }
        }

        refreshButton.setOnClickListener {
            webView.reload()
        }

        homeButton.setOnClickListener {
            webView.loadUrl(homeUrl)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            builtInZoomControls = false
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true
            mediaPlaybackRequiresUserGesture = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            safeBrowsingEnabled = true
        }

        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
            WebSettingsCompat.setAlgorithmicDarkeningAllowed(webView.settings, true)
        }

        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_AUTO)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val uri = request?.url ?: return false
                val scheme = uri.scheme ?: return true
                return scheme != "http" && scheme != "https"
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                syncChrome(url ?: homeUrl, getString(R.string.loading), true)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                syncChrome(
                    url = url ?: homeUrl,
                    title = view?.title ?: getString(R.string.app_name),
                    loading = false
                )
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                progressBar.visibility = if (newProgress >= 100) View.GONE else View.VISIBLE
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                syncChrome(
                    url = view?.url ?: homeUrl,
                    title = title ?: getString(R.string.app_name),
                    loading = view?.progress != 100
                )
            }
        }
    }

    private fun configureBackHandling() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun navigateFromInput(): Boolean {
        val destination = normalizeInput(addressInput.text?.toString().orEmpty())
        webView.loadUrl(destination)
        addressInput.clearFocus()
        return true
    }

    private fun syncChrome(url: String, title: String, loading: Boolean) {
        addressInput.setText(url)
        addressInput.setSelection(addressInput.text?.length ?: 0)
        pageTitle.text = if (loading) getString(R.string.loading) else title
        securityBadge.text = if (url.startsWith("https://")) getString(R.string.secure) else getString(R.string.caution)
        backButton.isEnabled = webView.canGoBack()
        forwardButton.isEnabled = webView.canGoForward()
    }

    private fun normalizeInput(raw: String): String {
        val input = raw.trim()
        if (input.isEmpty()) {
            return homeUrl
        }

        val uri = Uri.parse(input)
        val hasScheme = !uri.scheme.isNullOrBlank()
        val guessUrl = if (hasScheme) input else "https://$input"
        val guessUri = Uri.parse(guessUrl)
        val host = guessUri.host.orEmpty()

        return if ((guessUri.scheme == "http" || guessUri.scheme == "https") && host.contains('.')) {
            guessUrl
        } else {
            val encoded = URLEncoder.encode(input, StandardCharsets.UTF_8.toString())
            "https://www.google.com/search?q=$encoded"
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView.saveState(outState)
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
