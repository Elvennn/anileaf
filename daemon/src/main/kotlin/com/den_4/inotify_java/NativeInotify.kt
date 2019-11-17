package com.den_4.inotify_java

import com.den_4.inotify_java.exceptions.InsufficientKernelMemoryException
import com.den_4.inotify_java.exceptions.SystemLimitException
import com.den_4.inotify_java.exceptions.UserInstanceLimitException
import com.den_4.inotify_java.exceptions.UserWatchLimitException
import 

class NativeInotify {
    @Throws(
        InsufficientKernelMemoryException::class,
        SystemLimitException::class,
        UserInstanceLimitException::class
    )
    private external fun init(): Int

    private external fun close(fd: Int)

    @Throws(UserWatchLimitException::class)
    private external fun add_watch(fd: Int, path: String, mask: Int): Int

    private external fun rm_watch(fd: Int, wd: Int): Int

    private external fun read(fd: Int)

    companion object {
        init {
            System.loadLibrary("inotify-java")
        }
    }
}

