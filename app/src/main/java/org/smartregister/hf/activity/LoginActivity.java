package org.smartregister.hf.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import org.smartregister.hf.R;
import org.smartregister.hf.fragment.SwitchEnvironmentFragment;
import org.smartregister.hf.presenter.LoginPresenter;
import org.smartregister.hf.util.Constants;
import org.smartregister.view.activity.BaseLoginActivity;
import org.smartregister.view.contract.BaseLoginContract;

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
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String env = sharedPreferences.getString(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT,"");
            if(env.isEmpty()){
                SwitchEnvironmentFragment dialog = new SwitchEnvironmentFragment();
                dialog.show(this.getSupportFragmentManager(), "SwitchEnvironmentFragment");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
