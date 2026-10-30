package com.nicolascommandeur.cowkingsquest.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.nicolascommandeur.cowkingsquest.data.guide.guideStepIds
import com.nicolascommandeur.cowkingsquest.data.local_storage.GuideStore

/** Owns guide completion independently of counter progress. */
class GuideViewModel(application: Application) : AndroidViewModel(application) {
    private val store = GuideStore(application)

    var completedSteps by mutableStateOf(store.load())
        private set

    fun update(next: Set<String>) {
        completedSteps = next.intersect(guideStepIds)
        store.save(completedSteps)
    }
}
