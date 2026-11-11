package com.taskydad.unityadstester

import android.app.Activity
import android.os.Bundle
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.*
import android.text.InputType
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.unity3d.ads.UnityAds
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener

class MainActivity : Activity() {
    private lateinit var gameIdInput: EditText
    private lateinit var placementInput: EditText
    private lateinit var testMode: CheckBox
    private lateinit var logView: TextView
    private lateinit var statusView: TextView
    private var initialized = false
    private var loadedPlacement: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
        log("Ready. Enter your Android Game ID and a Placement ID from Unity Dashboard.")
        log("Test Mode is ON by default. Use test ads while validating integration.")
        log("Network: ${if (hasNetwork()) "available" else "not detected"}")
    }

    private fun buildUi() {
        val scroll = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(24))
            setBackgroundColor(Color.rgb(246, 248, 251))
        }
        scroll.addView(content)
        setContentView(scroll)

        content.addView(TextView(this).apply {
            text = "Unity Ads Tester"
            textSize = 26f
            setTextColor(Color.rgb(25, 35, 55))
            typeface = Typeface.DEFAULT_BOLD
        })
        content.addView(TextView(this).apply {
            text = "Native Android • SDK initialization • Load • Show • Live logs"
            textSize = 13f
            setTextColor(Color.DKGRAY)
            setPadding(0, dp(5), 0, dp(16))
        })

        gameIdInput = field(content, "Unity Game ID (Android)", "Enter Android Game ID")
        placementInput = field(content, "Placement ID", "Example: Rewarded_Android")

        testMode = CheckBox(this).apply {
            text = "Test Mode (recommended)"
            isChecked = true
            textSize = 15f
        }
        content.addView(testMode)

        statusView = TextView(this).apply {
            text = "Status: Not initialized"
            textSize = 14f
            setTextColor(Color.rgb(90, 90, 90))
            setPadding(0, dp(10), 0, dp(10))
        }
        content.addView(statusView)

        addButton(content, "1. Initialize SDK") { initializeSdk() }
        addButton(content, "2. Load Ad") { loadAd() }
        addButton(content, "3. Show Ad") { showAd() }
        addButton(content, "Clear Logs") { logView.text = "" }

        content.addView(TextView(this).apply {
            text = "LIVE LOGS"
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.rgb(35, 45, 65))
            setPadding(0, dp(18), 0, dp(8))
        })
        logView = TextView(this).apply {
            textSize = 12f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.rgb(220, 235, 220))
            setBackgroundColor(Color.rgb(24, 30, 38))
            setPadding(dp(12), dp(12), dp(12), dp(12))
            text = ""
        }
        content.addView(logView, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(260)
        ))
        content.addView(TextView(this).apply {
            text = "Note: A successful SDK load does not guarantee an ad will always be available. Fill IDs exactly as shown in Unity Dashboard."
            textSize = 12f
            setTextColor(Color.DKGRAY)
            setPadding(0, dp(12), 0, 0)
        })
    }

    private fun field(parent: LinearLayout, label: String, hintText: String): EditText {
        parent.addView(TextView(this).apply {
            text = label
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.rgb(45, 55, 75))
            setPadding(0, dp(10), 0, dp(5))
        })
        val edit = EditText(this).apply {
            hint = hintText
            textSize = 15f
            singleLine = true
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(dp(12), 0, dp(12), 0)
            setBackgroundColor(Color.WHITE)
        }
        parent.addView(edit, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(52)
        ))
        return edit
    }

    private fun addButton(parent: LinearLayout, title: String, action: () -> Unit) {
        val button = Button(this).apply {
            text = title
            isAllCaps = false
            setOnClickListener { action() }
        }
        val lp = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        lp.topMargin = dp(8)
        parent.addView(button, lp)
    }

    private fun initializeSdk() {
        val gameId = gameIdInput.text.toString().trim()
        if (gameId.isBlank()) {
            log("ERROR: Game ID is empty.")
            return
        }
        if (!hasNetwork()) log("WARNING: Network not detected; SDK initialization may fail.")
        initialized = false
        loadedPlacement = null
        statusView.text = "Status: Initializing…"
        val useTestMode = testMode.isChecked
        log("Initializing Unity Ads. Test Mode=$useTestMode")
        try {
            UnityAds.initialize(
                applicationContext,
                gameId,
                useTestMode,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        runOnUiThread {
                            initialized = true
                            statusView.text = "Status: SDK initialized"
                            log("SUCCESS: Unity Ads initialization complete.")
                        }
                    }
                    override fun onInitializationFailed(
                        error: UnityAds.UnityAdsInitializationError,
                        message: String
                    ) {
                        runOnUiThread {
                            initialized = false
                            statusView.text = "Status: Initialization failed"
                            log("INIT FAILED: $error — $message")
                        }
                    }
                }
            )
        } catch (e: Exception) {
            statusView.text = "Status: Initialization exception"
            log("EXCEPTION during initialize: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun loadAd() {
        val placement = placementInput.text.toString().trim()
        if (!initialized) {
            log("ERROR: Initialize SDK successfully before loading.")
            return
        }
        if (placement.isBlank()) {
            log("ERROR: Placement ID is empty.")
            return
        }
        loadedPlacement = null
        log("Loading placement: $placement")
        try {
            UnityAds.load(placement, object : IUnityAdsLoadListener {
                override fun onUnityAdsAdLoaded(placementId: String) {
                    runOnUiThread {
                        loadedPlacement = placementId
                        log("SUCCESS: Ad loaded for placement: $placementId")
                    }
                }
                override fun onUnityAdsFailedToLoad(
                    placementId: String,
                    error: UnityAds.UnityAdsLoadError,
                    message: String
                ) {
                    runOnUiThread {
                        loadedPlacement = null
                        log("LOAD FAILED: placement=$placementId, error=$error, message=$message")
                    }
                }
            })
        } catch (e: Exception) {
            log("EXCEPTION during load: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun showAd() {
        val placement = placementInput.text.toString().trim()
        if (!initialized) {
            log("ERROR: SDK is not initialized.")
            return
        }
        if (placement.isBlank()) {
            log("ERROR: Placement ID is empty.")
            return
        }
        if (loadedPlacement != placement) {
            log("ERROR: This placement has not successfully loaded. Tap Load Ad first.")
            return
        }
        log("Showing ad: $placement")
        try {
            UnityAds.show(this, placement, object : IUnityAdsShowListener {
                override fun onUnityAdsShowFailure(
                    placementId: String,
                    error: UnityAds.UnityAdsShowError,
                    message: String
                ) {
                    runOnUiThread { log("SHOW FAILED: placement=$placementId, error=$error, message=$message") }
                }
                override fun onUnityAdsShowStart(placementId: String) {
                    runOnUiThread { log("SHOW STARTED: $placementId") }
                }
                override fun onUnityAdsShowClick(placementId: String) {
                    runOnUiThread { log("AD CLICKED: $placementId") }
                }
                override fun onUnityAdsShowComplete(
                    placementId: String,
                    state: UnityAds.UnityAdsShowCompletionState
                ) {
                    runOnUiThread {
                        log("SHOW COMPLETE: $placementId, completion=$state")
                        loadedPlacement = null
                    }
                }
            })
        } catch (e: Exception) {
            log("EXCEPTION during show: ${e.javaClass.simpleName}: ${e.message}")
        }
    }

    private fun hasNetwork(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun log(message: String) {
        runOnUiThread {
            logView.append("• $message\n")
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
