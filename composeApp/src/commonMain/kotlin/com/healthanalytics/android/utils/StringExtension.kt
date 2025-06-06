package com.healthanalytics.android.utils

fun String.capitalizeFirst(): String = replaceFirstChar { it.uppercaseChar() }
