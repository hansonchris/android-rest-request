package com.dridian.android_rest_request;

import java.util.List;

public interface PendingWebServicePersistentStorageInterface
{
    public long savePendingWebService(PendingWebService pendingService);

    public void removePendingWebService(PendingWebService pendingService);

    public void removeAllPendingWebServices(WebServiceInterface service);

    public List<PendingWebService> getPendingWebServices();
}