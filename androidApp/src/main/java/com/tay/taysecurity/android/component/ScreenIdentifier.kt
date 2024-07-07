package com.tay.taysecurity.android.component

const val ROOT_GRAPH_ROUTE = "root"

sealed class ScreenIdentifier (open val route: String) {
    object HomeScreen : ScreenIdentifier("home_screen")
    object BlockingCallScreen : ScreenIdentifier("blocking_call_screen")

    fun withArgs(vararg args: String = emptyArray(), optional: Map<String, String?> = emptyMap()): String {
        return buildString {
            append(route)
            args.forEach { append("/$it") }

            if(optional.isNotEmpty()) {
                append("?")
            }

            optional.entries.forEachIndexed { index, map ->
                map.value?.let {
                    if(index != 0) append("&")

                    append("${map.key}=${map.value}")
                }
            }
        }
    }


}