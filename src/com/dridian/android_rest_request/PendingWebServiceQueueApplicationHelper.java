package com.dridian.android_rest_request;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingWebServiceQueueApplicationHelper
{
    volatile static private PendingWebServiceQueueApplicationHelper instance;
    volatile protected Context context;
    volatile protected PendingWebServiceQueue pendingWebServiceQueue;
    volatile protected Map<PendingWebService, PendingWebServiceSubscriberInterface> pendingWebServiceSubscribers;

    private PendingWebServiceQueueApplicationHelper(Context context)
    {
        this.context = context;
        startService();
    }

    synchronized public static PendingWebServiceQueueApplicationHelper getInstance(Context context)
    {
        if (instance == null) {
            instance = new PendingWebServiceQueueApplicationHelper(context);
        }

        return instance;
    }

    public void startService()
    {
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(1000);
        boolean pendingWebServiceQueueServiceRunning = false;
        for (ActivityManager.RunningServiceInfo runningService : runningServices) {
            String name = runningService.service.getClassName();
            if (name.equals(PendingWebServiceQueueBackgroundService.class.getName()) &&
                runningService.started
            ) {
                PendingWebServiceQueueBackgroundService pendingWebServiceService =
                    PendingWebServiceQueueBackgroundService.getInstance();
                if (pendingWebServiceService != null && !pendingWebServiceService.threadIsRunning()) {
                    pendingWebServiceService.startTimerTask();
                    pendingWebServiceQueueServiceRunning = true;
                }
            }
        }
        if (!pendingWebServiceQueueServiceRunning) {
            Intent pendingWebServiceIntent = new Intent();
            pendingWebServiceIntent.setAction("com.dridian.android_rest_request.PendingWebServiceQueueBackgroundService");
            context.startService(pendingWebServiceIntent);
        }
    }

    synchronized public PendingWebServiceQueue getPendingWebServiceQueue()
    {
        if (pendingWebServiceQueue == null) {
            pendingWebServiceQueue = new PendingWebServiceQueue(context);
        }

        return pendingWebServiceQueue;
    }

    synchronized public Map<PendingWebService, PendingWebServiceSubscriberInterface> getPendingWebServiceSubscribers()
    {
        if (pendingWebServiceSubscribers == null) {
            pendingWebServiceSubscribers =
                new HashMap<PendingWebService, PendingWebServiceSubscriberInterface>();
        }

        return pendingWebServiceSubscribers;
    }
}
