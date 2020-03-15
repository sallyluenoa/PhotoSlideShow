package org.fog_rock.photo_slideshow.core.math

import org.junit.Assert.assertEquals
import org.junit.Test

class SizeCalculatorTest {

    private val delta = 1.0f-6
    private val calculator = SizeCalculator()

    @Test
    fun estimateEffectiveScale() {

        assertEquals(
            0.5f,
            calculator.estimateEffectiveScale(1080,1920, 500, 1000),
            delta)

        assertEquals(
            1.0f,
            calculator.estimateEffectiveScale(1080, 1920, 2000, 1000),
            delta
        )

        assertEquals(
            0.75f,
            calculator.estimateEffectiveScale(1920, 1080, 1500, 750),
            delta
        )

        assertEquals(
            1.0f,
            calculator.estimateEffectiveScale(360, 480, 500, 1000),
            delta
        )

        assertEquals(
            0.25f,
            calculator.estimateEffectiveScale(4096, 2160, 500, 1000),
            delta
        )

        assertEquals(
            0.375f,
            calculator.estimateEffectiveScale(1280, 960, 250, 500),
            delta
        )

        assertEquals(
            0.1875f,
            calculator.estimateEffectiveScale(3120, 4160, 500, 1000),
            delta
        )
    }
}