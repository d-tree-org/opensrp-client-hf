package org.smartregister.hf.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.hf.BuildConfig;
import org.smartregister.hf.R;
import org.smartregister.hf.presenter.LoginPresenter;
import org.smartregister.hf.util.Constants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

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
        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    @Override
    public void goToHome(boolean b) {
        //Take user to a home page
        if (b){

        }
        goToMainActivity(b);
        finish();

    }

    private void goToMainActivity(boolean b){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase("Settings")) {
            startActivity(new Intent(this, HfSettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()){
            goToHome(false);
        }
    }

    public void setServerURL() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();

        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Kituoni", "env_switch.json");

            if (file.exists()) {
                // if the file is there, then  switching has taken place and the data was cleared from the device
                // Get the environment configurations from the file and set the url based on that
/*                        Yaml yaml = new Yaml();
                        Map<String, Object> envConfig = (Map<String, Object>) yaml.load(new FileInputStream(file));*/
                JSONObject envConfig = getSwitchConfigurationsFromFile(file);

                if (envConfig != null) {

                    if (envConfig.get("env").toString().equalsIgnoreCase("test")) {

                        preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_staging);
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(Constants.ENVIRONMENT_CONFIG.PREFERENCE_PRODUCTION_ENVIRONMENT_SWITCH, false).commit();
                        preferences.savePreference(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT, Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT);
                        setButtonIndicator(Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT);
                    } else {

                        preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_production);
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(Constants.ENVIRONMENT_CONFIG.PREFERENCE_PRODUCTION_ENVIRONMENT_SWITCH, true).commit();
                        preferences.savePreference(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT, Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT);
                        setButtonIndicator(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT);
                    }
                }

            } else {
                // The file does not exist, no switching environment has taken place. This is the first time the user logged into the device
                preferences.savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_production);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(Constants.ENVIRONMENT_CONFIG.PREFERENCE_PRODUCTION_ENVIRONMENT_SWITCH, true).commit();
                preferences.savePreference(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT, Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT);
                setButtonIndicator(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private JSONObject getSwitchConfigurationsFromFile(File file) {
        JSONObject jsonObject = new JSONObject();
        try {
            FileInputStream stream = new FileInputStream(file);
            String jString = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                /* Instead of using default, pass in a decoder. */
                jString = Charset.defaultCharset().decode(bb).toString();
            } finally {
                stream.close();
            }
            jsonObject = new JSONObject(jString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void setButtonIndicator(String env){
        if (env.equals(Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT)){
            //We are on test env
            Button loginButton = findViewById(R.id.login_login_btn);
            loginButton.setBackgroundColor(getResources().getColor(R.color.test_environment_color));
        }else{
            //we are in production
            Button loginButton = findViewById(R.id.login_login_btn);
            loginButton.setBackgroundColor(getResources().getColor(R.color.primary_color));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Arrays.asList(permissions).contains(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setServerURL();
            }

        } else {
            Utils.getAllSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, BuildConfig.opensrp_url_production);
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(Constants.ENVIRONMENT_CONFIG.PREFERENCE_PRODUCTION_ENVIRONMENT_SWITCH, true).commit();
            Utils.getAllSharedPreferences().savePreference(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT, Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT);
            setButtonIndicator(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT);
        }
    }

}
