package com.tay.taysecurity

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform