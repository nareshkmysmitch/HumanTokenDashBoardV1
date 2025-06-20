package com.healthanalytics.android.extension


fun formatToTwoDecimalPlaces(value: Double): String {
    val intPart = value.toInt()
    val decimalPart = ((value - intPart) * 100).toInt().let { if (it < 0) -it else it }
    return "$intPart.${decimalPart.toString().padStart(2, '0')}"
}

fun twoDecimalPlaces(double: Double): String {
    val rounded = (double * 10).toInt() / 10.0
    return if (rounded % 1.0 == 0.0) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}


fun twoDecimal(float: Float): String {
    val rounded = ((float * 100).toInt()) / 100.0f
    return if (rounded % 1.0f == 0.0f) {
        rounded.toInt().toString()
    } else {
        formatToTwoDecimalPlaces(rounded.toDouble())
    }
}


fun Double.inchToCM(): String {
    return twoDecimal((this.times(2.54).toFloat()))
}

fun Double.cmToInch(): String {
    return twoDecimalPlaces((this.div(2.54)))
}


