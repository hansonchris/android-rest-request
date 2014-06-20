package com.dridian.android_rest_request;

import java.util.List;
import java.util.Vector;

public class PendingWebServiceQueue
{
    protected List<PendingWebService> pendingServices;
    volatile protected PendingWebServicePersistentStorageInterface storage;

    public PendingWebServiceQueue(PendingWebServicePersistentStorageInterface storage)
    {
        this.storage = storage;
        populatePendingServices();
    }

    public void addPendingService(PendingWebService pendingService)
    {
        long id = storage.savePendingWebService(pendingService);
        pendingService.setId(id);
        pendingServices = storage.getPendingWebServices();
    }

    public void removePendingService(PendingWebService pendingService)
    {
        if (pendingServices.contains(pendingService))
        {
            pendingServices.remove(pendingService);
        }
        storage.removePendingWebService(pendingService);
    }

    public void removeAllInstances(WebServiceInterface webService)
    {
        if (pendingServices != null)
        {
            for (PendingWebService currentPendingService : pendingServices)
            {
                if (currentPendingService.getWebService().getClass().equals(webService.getClass()))
                {
                    removePendingService(currentPendingService);
                }
            }
        }
    }

    protected void populatePendingServices()
    {
        pendingServices = storage.getPendingWebServices();
        if (pendingServices == null)
        {
            pendingServices = getListPendingWebService();
        }
    }

    public List<PendingWebService> getPendingServices()
    {
        return pendingServices;
    }

    protected List<PendingWebService> getListPendingWebService()
    {
        return new Vector<PendingWebService>();
    }
}