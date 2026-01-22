package com.vantechinformatics.easycargo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun Double.format(digits: Int): String

expect fun Long.getFormattedDate(): String