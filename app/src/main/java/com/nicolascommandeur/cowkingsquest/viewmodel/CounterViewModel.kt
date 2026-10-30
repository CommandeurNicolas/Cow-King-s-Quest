package com.nicolascommandeur.cowkingsquest.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.nicolascommandeur.cowkingsquest.data.local_storage.HuntStore
import com.nicolascommandeur.cowkingsquest.model.hunt.HuntState

/** Owns counter progress and farming setup across activity recreation. */
class CounterViewModel(application: Application) : AndroidViewModel(application) {
    private val store = HuntStore(application)

    var state by mutableStateOf(store.load())
        private set

    fun update(next: HuntState) {
        state = next
        store.save(next)
    }
}
