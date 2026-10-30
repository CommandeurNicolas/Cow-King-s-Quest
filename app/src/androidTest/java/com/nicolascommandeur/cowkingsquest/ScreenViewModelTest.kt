package com.nicolascommandeur.cowkingsquest

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import com.nicolascommandeur.cowkingsquest.model.hunt.HuntState
import com.nicolascommandeur.cowkingsquest.viewmodel.CounterViewModel
import com.nicolascommandeur.cowkingsquest.viewmodel.GuideViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ScreenViewModelTest {
    @Test
    fun screenProgressPersistsIndependentlyAndViewModelsSurviveOwnerRecreation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Isolate these stores from the installed app's actual progress.
        val application = object : Application() {
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences =
                context.getSharedPreferences("viewmodel_test_$name", mode)
        }
        val preferenceNames = listOf("cow_hunts_v1", "cow_guide_v1")
        fun clearPreferences() = preferenceNames.forEach {
            application.getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().commit()
        }

        val store = ViewModelStore()
        clearPreferences()
        try {
            val factory = ViewModelProvider.AndroidViewModelFactory(application)
            val provider = ViewModelProvider(store, factory)
            val counter = provider[CounterViewModel::class.java]
            val guide = provider[GuideViewModel::class.java]
            val hunt = HuntState().let { it.updateActive(it.activeHunt.add(10)) }

            counter.update(hunt)
            assertEquals(emptySet<String>(), guide.completedSteps)
            guide.update(setOf("shard", "unknown_step"))
            assertEquals(setOf("shard"), guide.completedSteps)
            assertEquals(hunt, counter.state)

            val recreatedProvider = ViewModelProvider(store, factory)
            assertSame(counter, recreatedProvider[CounterViewModel::class.java])
            assertSame(guide, recreatedProvider[GuideViewModel::class.java])
            assertEquals(hunt, CounterViewModel(application).state)
            assertEquals(setOf("shard"), GuideViewModel(application).completedSteps)

            guide.update(emptySet())
            assertEquals(hunt, CounterViewModel(application).state)
            assertEquals(emptySet<String>(), GuideViewModel(application).completedSteps)
        } finally {
            store.clear()
            clearPreferences()
        }
    }
}
