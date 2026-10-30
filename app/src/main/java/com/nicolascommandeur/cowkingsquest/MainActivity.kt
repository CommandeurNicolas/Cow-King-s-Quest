package com.nicolascommandeur.cowkingsquest

import android.animation.ValueAnimator
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.nicolascommandeur.cowkingsquest.data.local_storage.CowVisitorLaunch
import com.nicolascommandeur.cowkingsquest.view.navigation.CowHuntApp
import com.nicolascommandeur.cowkingsquest.view.theme.DiabloCowCounterTheme
import com.nicolascommandeur.cowkingsquest.viewmodel.CounterViewModel
import com.nicolascommandeur.cowkingsquest.viewmodel.GuideViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Rotation restores the activity without showing another launch splash.
        var splashFinished by mutableStateOf(savedInstanceState != null)
        splashScreen.setOnExitAnimationListener { splash ->
            if (ValueAnimator.areAnimatorsEnabled()) {
                splash.view.animate()
                    .alpha(0f)
                    .setDuration(160L)
                    .withEndAction { splash.remove(); splashFinished = true }
                    .start()
            } else {
                splash.remove()
                splashFinished = true
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )

        val viewModels = ViewModelProvider(this)
        val counterViewModel = viewModels[CounterViewModel::class.java]
        val guideViewModel = viewModels[GuideViewModel::class.java]

        // Moo ?
        val visitorSession = if (
            applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0 &&
            intent.getBooleanExtra("cow_visitor_preview", false)
        ) {
            // Forced Moo.
            CowVisitorLaunch.previewSession
        } else {
            // Random Moo, with a guaranteed visit every 666 cold launches.
            CowVisitorLaunch.session(this)
        }

        setContent {
            DiabloCowCounterTheme {
                CowHuntApp(
                    state = counterViewModel.state,
                    completedSteps = guideViewModel.completedSteps,
                    onHuntChange = counterViewModel::update,
                    onGuideChange = guideViewModel::update,
                    visitorSession = visitorSession,
                    splashFinished = splashFinished
                )
            }
        }
    }
}
