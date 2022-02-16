package org.smartregister.hf.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;
import org.smartregister.chw.referral.activity.BaseReferralRegisterActivity;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.hf.custom_views.NavigationMenu;
import org.smartregister.hf.fragment.ReferralRegisterFragment;
import org.smartregister.hf.util.Constants;
import org.smartregister.util.LangUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.List;

public class ReferralRegisterActivity extends BaseReferralRegisterActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationMenu.getInstance(this, null, null);
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        // Implement
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(Constants.CONFIGURATION.REFERRAL_REGISTER);
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return ReferralRegisterFragment.newInstance(this.getIntent().getExtras());
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        NavigationMenu menu = NavigationMenu.getInstance(this, null, null);
        if (menu != null) {
            menu.getNavigationAdapter().setSelectedView(Constants.DrawerMenu.REFERRALS);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        // get language from prefs
        String lang = LangUtils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(LangUtils.setAppLocale(base, lang));
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }
}
