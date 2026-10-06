package com.nicolascommandeur.diablo4cowcompanion

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CowVisitorSessionTest {
    @Test
    fun ordinarySessionNeverShowsCowEvenAfterMultipleIntervals() {
        val session = CowVisitorSession(lucky = false)
        repeat(10) { assertNull(session.advance(300_000)) }
    }

    @Test
    fun luckySessionWaitsThenVisitsForExactly9500msAndReturnsAtFiveMinutes() {
        val session = CowVisitorSession(lucky = true)
        assertNull(session.advance(599))
        assertEquals(0L, session.advance(1)!!.ageMs)
        assertNotNull(session.advance(9_499))
        assertNull(session.advance(1))
        assertNull(session.advance(290_499))
        assertEquals(0L, session.advance(1)!!.ageMs)
    }

    @Test
    fun tappingPersistsWithinVisitButNextVisitUsesAnOccasionalLine() {
        val session = CowVisitorSession(lucky = true, nextLine = { 0 })
        assertEquals("Moo.", session.advance(600)!!.line)
        assertEquals("There is no cow level.", session.tap()!!.line)
        assertEquals("There is no cow level.", session.advance(3_000)!!.line)

        val next = session.advance(297_000)!!
        assertEquals("You saw nothing.", next.line)
        assertFalse(next.tapped)

        val alternate = CowVisitorSession(lucky = true, nextLine = { 1 })
        alternate.advance(600)
        assertEquals("Just passing through.", alternate.advance(300_000)!!.line)
    }

    @Test
    fun readingSameSessionAfterRecreationDoesNotRestartOrReroll() {
        val session = CowVisitorSession(lucky = true)
        val before = session.advance(3_600)

        session.tap()

        val restored = session.advance(0)!!

        assertEquals(before!!.ageMs, restored.ageMs)
        assertEquals("There is no cow level.", restored.line)
    }

    @Test
    fun animationPeeksEntersPausesAndLeavesUsingValidSpriteFrames() {
        fun at(ms: Long) = CowVisit(ms, "Moo.", false)

        assertEquals(0f, at(0).peekFraction)
        assertEquals(1f, at(650).peekFraction)
        assertEquals(0f, at(2_650).enterFraction)
        assertFalse(at(2_649).entering)
        assertEquals(1f, at(4_650).enterFraction)
        assertTrue(at(4_650).showBubble)
        assertFalse(at(7_450).showBubble)
        assertTrue(at(7_450).leaving)
        assertEquals(1f, at(9_500).exitFraction)

        for (time in 0L..9_500L) assertTrue(at(time).frame in 0..7)
    }
}
