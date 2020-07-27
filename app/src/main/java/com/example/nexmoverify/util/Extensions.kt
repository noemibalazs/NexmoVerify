package com.example.nexmoverify.util

import com.example.nexmoverify.region.Region

fun String.firstToUpperCase(): String {
    return this.substring(0, 1).toUpperCase()
}

fun Region.isSame(region: Region?): Boolean = this == region