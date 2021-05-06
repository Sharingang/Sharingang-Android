package com.example.sharingang.shake

import org.junit.Test

class ShakeDetectorTest {
    @Test
    fun noShakeIsDetectedForSmallMovements() {
        val detector = ShakeDetector()
        assert(!detector.detect(0F, 0F, 0F, 0))
        assert(!detector.detect(0F, 0F, 0F, 10000))
    }

    @Test
    fun shakeIsDetectedForLargeMovements() {
        val detector = ShakeDetector()
        assert(detector.detect(1000F, 1000F, 1000F, 10000))
    }

    @Test
    fun shakeIsNotDetectedForLargeMovementsThatHappenTooSoon() {
        val detector = ShakeDetector()
        assert(!detector.detect(1000F, 1000F, 1000F, 0))
        assert(detector.detect(1000F, 1000F, 1000F, 10000))
        assert(!detector.detect(1000F, 1000F, 1000F, 10000))
    }
}