package org.smartregister.hf.activity;

import android.content.Intent;
import android.os.Bundle;

import org.smartregister.hf.R;
import org.smartregister.hf.presenter.LoginPresenter;
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
        Intent intent = new Intent(this, AddoHomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginPresenter.processViewCustomizations();
        if (!mLoginPresenter.isUserLoggedOut()){
            goToHome(false);
        }
    }
}
