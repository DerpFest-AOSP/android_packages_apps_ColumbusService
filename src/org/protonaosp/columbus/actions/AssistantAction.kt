/*
 * SPDX-FileCopyrightText: The Proton AOSP Project
 * SPDX-FileCopyrightText: TheParasiteProject
 * SPDX-License-Identifier: GPL-3.0
 */

package org.protonaosp.columbus.actions

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import org.protonaosp.columbus.TAG
import org.protonaosp.columbus.dlog

class AssistantAction(context: Context) : Action(context) {
    // Android 17 dropped IStatusBarService.startAssist(); SearchManager.launchAssist() is the
    // system API that reaches the same AssistManager path in SystemUI.
    private val searchManager = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager?

    override fun run() {
        try {
            searchManager?.launchAssist(Bundle())
                ?: dlog(TAG, "Failed to start assistant: SearchManager unavailable")
        } catch (e: Exception) {
            dlog(TAG, "Failed to start assistant: unexpected error ${e}")
        }
    }
}
