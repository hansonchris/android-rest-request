package com.dridian.android_rest_request;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PendingWebServiceQueue
{
    protected Map<String, PendingWebService> pendingWebServices;
    protected PendingWebServicePersistentStorageWrapper storage;

    public PendingWebServiceQueue(Context context)
    {
        initStorage(context);
        populatePendingServices();
    }

    public void addPendingService(PendingWebService pendingWebService)
    {
        storage.savePendingWebService(pendingWebService);
        pendingWebServices = storage.getPendingWebServices();
    }

    public void removePendingService(PendingWebService pendingWebService)
    {
        if (pendingWebServices.containsValue(pendingWebService)) {
            pendingWebServices.remove(pendingWebService);
        }
        storage.removePendingWebService(pendingWebService);
    }

    public void removeAllInstances(WebServiceInterface webService)
    {
        if (pendingWebServices != null) {
            Set<String> ids = pendingWebServices.keySet();
            for (String id : ids) {
                PendingWebService currentPendingService = pendingWebServices.get(id);
                if (currentPendingService.getWebService().getClass().equals(webService.getClass())) {
                    removePendingService(currentPendingService);
                }
            }
        }
    }

    public Map<String, PendingWebService> getPendingServices()
    {
        return pendingWebServices;
    }

    protected void initStorage(Context context)
    {
        storage = new PendingWebServicePersistentStorageWrapper(context);
    }

    protected void populatePendingServices()
    {
        pendingWebServices = storage.getPendingWebServices();
        if (pendingWebServices == null) {
            pendingWebServices = getPendingWebService();
        }
    }

    protected Map<String, PendingWebService> getPendingWebService()
    {
        return new HashMap<String, PendingWebService>();
    }
}
