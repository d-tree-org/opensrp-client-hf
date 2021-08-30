package org.smartregister.hf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;
import org.smartregister.hf.application.HfApplication;
import org.smartregister.hf.custom_views.NavigationMenu;
import org.smartregister.hf.fragment.ReportFragment;
import org.smartregister.hf.presenter.ReportsPresenter;
import org.smartregister.hf.util.Constants;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.List;

public class ReportsActivity extends BaseRegisterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
        HfApplication.getInstance().notifyAppContextChange();
    }

    @Override
    protected void setupViews() {
        super.setupViews();
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    protected void initializePresenter() {
        presenter = new ReportsPresenter();
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ReportFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        Fragment fragment = new ReportFragment();
        return new Fragment[]{fragment};
    }

    @Override
    public void startRegistration() {

    }

    @Override
    public void startFormActivity(String s, String s1, String s2) {

    }

    @Override
    public void startFormActivity(JSONObject jsonObject) {

    }

    @Override
    protected void onActivityResultExtended(int i, int i1, Intent intent) {

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public List<String> getViewIdentifiers() {
        return null;
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.REPORTS);
        }
    }
}
