package org.smartregister.hf.interactor;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.hf.contract.AdvancedSearchContract;
import org.smartregister.domain.Response;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.hf.dao.FamilyDao;
import org.smartregister.hf.domain.Entity;
import org.smartregister.hf.util.Constants;
import org.smartregister.service.HTTPAgent;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class AdvancedSearchInteractor implements AdvancedSearchContract.Interactor {
    public static final String SEARCH_URL = "/rest/search/search";
    private AppExecutors appExecutors;
    private HTTPAgent httpAgent;
    private DristhiConfiguration dristhiConfiguration;

    public AdvancedSearchInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    AdvancedSearchInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void search(final Map<String, String> editMap, boolean isLocal, final AdvancedSearchContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Entity> response;

                if(isLocal) {
                    String searchText = editMap.get(Constants.DB.FIRST_NAME);
                    response = localSearch(searchText);
                }
                else {
                    response = globalSearch(editMap);
                }

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onResultsFound(response, isLocal);
                    }
                });
            }
        };

        appExecutors.networkIO().execute(runnable);
    }
    private List<Entity> localSearch(String searchText) {
        return FamilyDao.search(searchText);
    }

    private List<Entity> globalSearch(Map<String, String> map) {
        String baseUrl = getDristhiConfiguration().dristhiBaseURL();
        String paramString = "";
        if (!map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    value = urlEncode(value);
                    String param = key.trim() + "=" + value.trim();
                    if (StringUtils.isBlank(paramString)) {
                        paramString = "?" + param;
                    } else {
                        paramString += "&" + param;
                    }
                }
            }
        }
        String uri = baseUrl + SEARCH_URL + paramString;

        Log.d(AdvancedSearchInteractor.class.getCanonicalName(), uri);
        Response<String> response = getHttpAgent().fetch(uri);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        Gson gson = builder.create();

        List<Entity> members = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response.payload());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objectInArray = jsonArray.getJSONObject(i);
                String opensrpId = objectInArray.getJSONObject("identifiers").getString("opensrp_id");
                if (!opensrpId.contains("family")) {
                    Entity member = gson.fromJson(objectInArray.toString(), Entity.class);
                    members.add(member);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return members;
    }

    public DristhiConfiguration getDristhiConfiguration() {
        if (this.dristhiConfiguration == null) {
            this.dristhiConfiguration = CoreLibrary.getInstance().context().configuration();
        }
        return this.dristhiConfiguration;
    }

    public void setDristhiConfiguration(DristhiConfiguration dristhiConfiguration) {
        this.dristhiConfiguration = dristhiConfiguration;
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public HTTPAgent getHttpAgent() {
        if (this.httpAgent == null) {
            this.httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        }
        return this.httpAgent;

    }

    public void setHttpAgent(HTTPAgent httpAgent) {
        this.httpAgent = httpAgent;
    }
}
