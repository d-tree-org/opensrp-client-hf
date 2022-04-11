package org.smartregister.hf.activity;

import static java.sql.DriverManager.println;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import org.json.JSONObject;
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

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

import timber.log.Timber;

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
//        checkEnvironment();
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
        if (!mLoginPresenter.isUserLoggedOut()) {
            goToHome(false);
        }
    }

    public void setServerURL() {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String env = sharedPreferences.getString(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT,"");
            if(env.isEmpty()){
                Timber.e("Environment %S",env);
                SwitchEnvironmentFragment dialog = new SwitchEnvironmentFragment();
                dialog.show(this.getSupportFragmentManager(), "SwitchEnvironmentFragment");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setButtonIndicator(String env) {
        if (env.equals(Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT)) {
            //We are on test env
            Button loginButton = findViewById(R.id.login_login_btn);
            loginButton.setBackgroundColor(getResources().getColor(R.color.test_environment_color));
        } else {
            //we are in production
            Button loginButton = findViewById(R.id.login_login_btn);
            loginButton.setBackgroundColor(getResources().getColor(R.color.primary_color));
        }
    }
}
