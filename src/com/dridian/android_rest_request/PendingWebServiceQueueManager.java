package com.dridian.android_rest_request;

import android.content.Context;

import java.util.Map;

public class PendingWebServiceQueueManager
{
    protected Context context;
    volatile protected PendingWebServiceQueue queue;
    volatile static private PendingWebServiceQueueManager instance;

    private PendingWebServiceQueueManager(Context context)
    {
        this.context = context;
        queue = getPendingWebServiceQueue();
    }

    synchronized static public PendingWebServiceQueueManager getInstance(Context context)
    {
        if (instance == null) {
            instance = new PendingWebServiceQueueManager(context);
        }

        return instance;
    }

    public void addPendingWebService(PendingWebService pendingService)
    {
        queue.addPendingService(pendingService);
        PendingWebServiceQueueBackgroundService service = PendingWebServiceQueueBackgroundService.getInstance(context);
        if (service != null) {
            service.addPendingWebService(pendingService);
        }
    }

    public void addPendingWebService(
        PendingWebService pendingService,
        PendingWebServiceSubscriberInterface subscriber
    ) {
        addPendingWebService(pendingService);
    }

    public Map<String, PendingWebService> getPendingWebServices()
    {
        return queue.getPendingServices();
    }

    public void removePendingWebService(PendingWebService pendingWebService)
    {
        queue.removePendingService(pendingWebService);
    }

    protected PendingWebServiceQueue getPendingWebServiceQueue()
    {
        return new PendingWebServiceQueue(context);
    }

    synchronized protected PendingWebServiceQueueBackgroundService getPendingWebServiceQueueBackgroundService()
    {
        return PendingWebServiceQueueBackgroundService.getInstance(context);
    }
}
