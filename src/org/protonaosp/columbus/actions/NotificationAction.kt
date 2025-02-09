package org.protonaosp.columbus.actions

import android.content.Context
import android.os.ServiceManager
import com.android.internal.statusbar.IStatusBarService

class NotificationAction(context: Context) : Action(context) {
    val service =
        IStatusBarService.Stub.asInterface(ServiceManager.getService(Context.STATUS_BAR_SERVICE))

    override fun canRun() = isDeviceInteractiveAndUnlocked(context)

    override fun canRunWhenScreenOff() = false

    override fun run() {
        service.togglePanel()
    }
}
