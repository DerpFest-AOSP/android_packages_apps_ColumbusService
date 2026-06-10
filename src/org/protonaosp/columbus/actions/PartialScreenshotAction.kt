/*
 * SPDX-FileCopyrightText: DerpFest AOSP
 * SPDX-License-Identifier: Apache-2.0
 */

package org.protonaosp.columbus.actions

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.WindowManager
import com.android.internal.util.ScreenshotHelper

class PartialScreenshotAction(context: Context) : Action(context) {
    val helper = ScreenshotHelper(context)
    private val handler = Handler.createAsync(Looper.getMainLooper())
    val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager

    override fun canRun() = pm?.isInteractive == true

    override fun canRunWhenScreenOff() = false

    override fun run() {
        helper.takeScreenshot(
            WindowManager.TAKE_SCREENSHOT_SELECTED_REGION,
            WindowManager.ScreenshotSource.SCREENSHOT_OTHER,
            handler,
            null,
        )
    }
}
