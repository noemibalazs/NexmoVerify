package com.example.nexmoverify.util

import android.content.Context
import android.content.Intent
import com.example.nexmoverify.region.Region

fun String.firstToUpperCase(): String {
    return this.substring(0, 1).toUpperCase()
}

fun Region.isSame(region: Region?): Boolean = this == region

fun Context.openActivity(dest: Class<*>){
    startActivity(Intent(this, dest))
}

fun Boolean.openActivity(context:Context, dest: Class<*>){
    if (this)
        context.openActivity(dest)
}