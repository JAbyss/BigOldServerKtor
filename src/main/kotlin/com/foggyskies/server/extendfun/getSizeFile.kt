package com.foggyskies.server.extendfun

fun getSizeFile(size: Long): String {
        if (size > 0) {
            val bytes = size
            if (bytes > 1024) {
                val kBytes = bytes / 1024f
                if (kBytes > 1024) {
                    val mBytes = kBytes / 1024f
                    if (mBytes > 1024)
                        return "${String.format("%.1f", mBytes / 1024f)} GB"
                    else
                        return "${String.format("%.1f", mBytes)} MB"
                } else
                    return "${String.format("%.1f", kBytes)} KB"
            } else
                return "$bytes B"
        } else if (size == 0L)
            return "0 Bit"
        else
            return "${String.format("%.1f", size)} Bit"
    }