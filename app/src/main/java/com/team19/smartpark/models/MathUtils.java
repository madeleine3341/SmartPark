package com.team19.smartpark.models;

class MathUtils {

    static int clamp(int amount, int low, int high) {
        return amount < low ? low : (Math.min(amount, high));
    }

    static float clamp(float amount, float low, float high) {
        return amount < low ? low : (Math.min(amount, high));
    }

}
