package org.smartregister.hf.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import org.smartregister.AllConstants;
import org.smartregister.hf.BuildConfig;
import org.smartregister.hf.R;
import org.smartregister.hf.fragment.SwitchEnvironmentFragment;
import org.smartregister.hf.presenter.LoginPresenter;
import org.smartregister.hf.util.Constants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Author : Isaya Mollel on 2019-10-18.
 */
public class LoginActivity extends BaseLoginActivity implements BaseLoginContract.View {

    public static final String TAG = BaseLoginActivity.class.getCanonicalName();

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setServerURL();
    }

    @Override
    public void goToHome(boolean b) {
        //Take user to a home page
        if (b) {

        }
        goToMainActivity(b);
        finish();

    }

    private void goToMainActivity(boolean b) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(getString(R.string.switch_environment));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase(getString(R.string.switch_environment))) {
            startActivity(new Intent(this, HfSettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
    }

    public void setServerURL() {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String env = sharedPreferences.getString(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT, "");
            if (env.isEmpty()) {
                SwitchEnvironmentFragment dialog = new SwitchEnvironmentFragment();
                dialog.setCancelable(false);
                dialog.show(this.getSupportFragmentManager(), "SwitchEnvironmentFragment");
            } else {
                if (env.equalsIgnoreCase(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT)) {
                    updateEnvironmentUrl(env, BuildConfig.opensrp_url_production);
                } else {
                    updateEnvironmentUrl(env, BuildConfig.opensrp_url_staging);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateEnvironmentUrl(String env, String baseUrl) {
        try {
            AllSharedPreferences preferences = Utils.getAllSharedPreferences();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            URL url = new URL(baseUrl);
            preferences.saveHost(url.getProtocol() + "://" + url.getHost());
            preferences.savePort(url.getPort());
            preferences.savePreference(AllConstants.DRISHTI_BASE_URL, baseUrl);
            sharedPreferences.edit().putBoolean(Constants.ENVIRONMENT_CONFIG.PREFERENCE_PRODUCTION_ENVIRONMENT_SWITCH, true).apply();
            preferences.savePreference(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT, env);
            setButtonIndicator(env);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void setButtonIndicator(String env) {
        Button loginButton = findViewById(R.id.login_login_btn);
        if (env.equals(Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT)) {
            loginButton.setBackgroundColor(getResources().getColor(R.color.test_environment_color));
        } else {
            loginButton.setBackgroundColor(getResources().getColor(R.color.primary_color));
        }
    }
}
