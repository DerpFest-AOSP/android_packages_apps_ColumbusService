/*
 * SPDX-FileCopyrightText: The Proton AOSP Project
 * SPDX-FileCopyrightText: TheParasiteProject
 * SPDX-License-Identifier: GPL-3.0
 */

package org.protonaosp.columbus.actions

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.WindowManager
import com.android.internal.util.ScreenshotHelper

class ScreenshotAction(context: Context) : Action(context) {
    val helper = ScreenshotHelper(context)
    private val handler = Handler.createAsync(Looper.getMainLooper())
    val pm: PowerManager = run {
        val powerService = context.getSystemService(Context.POWER_SERVICE)
        if (powerService !is PowerManager) {
            throw IllegalStateException("Power service not available")
        }
        powerService
    }

    override fun canRun() = pm.isInteractive

    override fun canRunWhenScreenOff() = false

    override fun run() {
        helper.takeScreenshot(WindowManager.ScreenshotSource.SCREENSHOT_OTHER, handler, null)
    }
}
