package com.example.sharingang.utils

/**
 * Convert an angle in radians to an angle in degrees
 *
 * @param rad the radians to convert
 * @return the value in degrees
 */
fun radiansToDegrees(rad: Double): Double =
    rad * 180 / Math.PI

/**
 * Convert an angle in degrees to an angle in radians
 *
 * @param deg the degrees to convert
 * @return the value in radians
 */
fun degreesToRadians(deg: Double): Double =
    deg * Math.PI / 180