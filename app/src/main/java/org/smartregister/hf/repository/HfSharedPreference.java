package org.smartregister.hf.repository;

import android.content.SharedPreferences;

import com.vijay.jsonwizard.utils.AllSharedPreferences;

public class HfSharedPreference extends AllSharedPreferences {

    public static final String OPENSRP_HF_ENVIRONMENT = "opensrp_hf_environment";
    private SharedPreferences preferences;

    public HfSharedPreference(SharedPreferences preferences) {
        super(preferences);
        this.preferences = preferences;
    }

    public void updateOpensrpEnviroment(String environmentValue) {
        preferences.edit().putString(OPENSRP_HF_ENVIRONMENT, environmentValue).apply();
    }

    public String getOpensrpEnviroment() {
        return preferences.getString(OPENSRP_HF_ENVIRONMENT, "test");
    }

}
