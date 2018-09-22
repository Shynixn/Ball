package com.github.shynixn.ball.bukkit.core.logic.business.service

import com.github.shynixn.ball.api.business.service.SpigotTimingService
import com.github.shynixn.ball.bukkit.core.nms.VersionSupport
import java.lang.reflect.Field

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class SpigotTimingServiceImpl : SpigotTimingService {
    private var isInstalled = true
    private var isRunning = false
    private var clazz: Class<*>? = null
    private var timerField: Field? = null

    init {
        try {
            clazz = Class.forName("org.bukkit.craftbukkit.VERSION.SpigotTimings".replace("VERSION", VersionSupport.getServerVersion().versionText))
            timerField = clazz!!.getDeclaredField("entityMoveTimer")
        } catch (ignored: ClassNotFoundException) {
            isInstalled = false
        }
    }

    /**
     * Starts the spigot timer for ball calculation.
     * Does nothing if the spigot timer is already running or the timer framework is not installed.
     */
    override fun startTiming() {
        if (isInstalled && !isRunning) {
            timerField!!.javaClass.getDeclaredMethod("startTiming").invoke(timerField)
            isRunning = true
        }
    }

    /**
     * Stops the spigot timer for ball calculation.
     * Does nothing if the spigot timer is already stopped or the timer framework is not installed.
     */
    override fun stopTiming() {
        if (isInstalled && isRunning) {
            timerField!!.javaClass.getDeclaredMethod("stopTiming").invoke(timerField)
            isRunning = false
        }
    }
}