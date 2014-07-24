package com.dridian.android_rest_request;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hansonchris.android.storage.StorageFactory;
import com.hansonchris.android.storage.StorageInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PendingWebServicePersistentStorageWrapper
{
    protected Context context;
    protected StorageInterface storage;
    protected static final String STORAGE_KEY = "pending_web_services2";
    protected static final String KEY_WEB_SERVICE_CLASS = "class";
    protected static final String KEY_FREQUENCY = "frequency";
    protected static final String KEY_FREQUENCY_UNIT = "frequency_unit";
    protected static final String KEY_REQUEST_VALUES = "values";

    public PendingWebServicePersistentStorageWrapper(Context context)
    {
        this.context = context;
        Map<String, String> params = new HashMap<String, String>();
        params.put(StorageFactory.PARAM_FILENAME, "pending_web_services");
        storage = StorageFactory.getStorage(context, StorageFactory.Type.File, params);
    }

    public String savePendingWebService(PendingWebService pendingWebService)
    {
        String id = pendingWebService.getWebService().getId();
        Map<String, PendingWebService> pendingWebServices = getPendingWebServices();
        pendingWebServices.put(id, pendingWebService);
        savePendingWebServices(pendingWebServices);

        return id;
    }

    public void removePendingWebService(PendingWebService pendingWebService)
    {
        Map<String, PendingWebService> pendingWebServices = getPendingWebServices();
        pendingWebServices.remove(pendingWebService);
        savePendingWebServices(pendingWebServices);
    }

    protected void savePendingWebServices(Map<String, PendingWebService> pendingWebServices)
    {
        Gson gson = new Gson();
        Map<String, Map<String, String>> pendingWebServiceValues = new HashMap<String, Map<String, String>>();
        Set<String> ids = pendingWebServices.keySet();
        for (String id : ids) {
            Map<String, String> values = new HashMap<String, String>();
            PendingWebService pendingWebService = pendingWebServices.get(id);
            values.put(KEY_WEB_SERVICE_CLASS, pendingWebService.getWebService().getClass().getCanonicalName());
            values.put(KEY_FREQUENCY, String.valueOf(pendingWebService.getFrequency()));
            values.put(KEY_FREQUENCY_UNIT, String.valueOf(pendingWebService.getFrequencyUnit()));
            values.put(KEY_REQUEST_VALUES, pendingWebService.getWebService().serializeValues());
            pendingWebServiceValues.put(pendingWebService.getWebService().getId(), values);
        }
        String json = gson.toJson(pendingWebServiceValues);
        storage.put(STORAGE_KEY, json);
    }

    public void removeAllPendingWebServices(WebServiceInterface service)
    {
        Map<String, PendingWebService> existingPendingWebServices = getPendingWebServices();
        List<PendingWebService> listToRemove = new ArrayList<PendingWebService>();
        Set<String> ids = existingPendingWebServices.keySet();
        for (String id : ids) {
            if (id == service.getId()) {
                listToRemove.add(existingPendingWebServices.get(id));
            }
        }
        for (PendingWebService pendingWebService : listToRemove) {
            existingPendingWebServices.remove(pendingWebService);
        }
    }

    public Map<String, PendingWebService> getPendingWebServices()
    {
        Map<String, PendingWebService> pendingWebServices = new HashMap<String, PendingWebService>();;
        if (storage.containsKey(STORAGE_KEY)) {
            String data = storage.getString(STORAGE_KEY);
            Gson gson = new Gson();
            try {
                Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
                Map<String, Map<String, String>> pendingWebServiceValues = gson.fromJson(data, type);
                Set<String> pendingServiceIds = pendingWebServiceValues.keySet();
                for (String id : pendingServiceIds) {
                    Map<String, String> row = pendingWebServiceValues.get(id);
                    @SuppressWarnings("unchecked")
                    Class<WebServiceInterface> classObj = (Class<WebServiceInterface>)Class.forName(row.get(KEY_WEB_SERVICE_CLASS));
                    Constructor<WebServiceInterface> constructor = (Constructor<WebServiceInterface>)classObj.getConstructor(Context.class);
                    WebServiceInterface webService = constructor.newInstance(context);
                    webService.unserializeValues(row.get(KEY_REQUEST_VALUES));
                    PendingWebService pendingWebService = new PendingWebService(
                        webService,
                        //sometimes Gson returns 2000 as "2000.0" so this is necessary:
                        (int)Float.parseFloat(row.get(KEY_FREQUENCY)),
                        (int)Float.parseFloat(row.get(KEY_FREQUENCY_UNIT))
                    );
                    pendingWebServices.put(id, pendingWebService);
                }
            } catch (Exception e) {}
        }

        return pendingWebServices;
    }
}
