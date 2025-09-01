/*
 * SPDX-FileCopyrightText: The Proton AOSP Project
 * SPDX-FileCopyrightText: TheParasiteProject
 * SPDX-License-Identifier: GPL-3.0
 */

package org.protonaosp.columbus.actions

import android.content.Context
import android.os.Bundle
import android.os.RemoteException
import android.os.ServiceManager
import android.util.Log
import com.android.internal.statusbar.IStatusBarService
import org.protonaosp.columbus.TAG

class AssistantAction(context: Context) : Action(context) {
    val service =
        IStatusBarService.Stub.asInterface(ServiceManager.getService(Context.STATUS_BAR_SERVICE))

    override fun run() {
        try {
            service.startAssist(Bundle())
        } catch (e: RemoteException) {
            Log.e(TAG, "Failed to start assistant: service unavailable", e)
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to start assistant: permission denied", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start assistant: unexpected error", e)
        }
    }
}
