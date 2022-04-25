package org.smartregister.hf.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import org.smartregister.AllConstants;
import org.smartregister.hf.BuildConfig;
import org.smartregister.hf.R;
import org.smartregister.hf.util.Constants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.simprint.OnDialogButtonClick;
import org.smartregister.util.Utils;

import java.io.File;

public class HfPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private int countClick = 0;
    private SwitchPreferenceCompat switchPreferenceCompat;
    private Preference preference = findPreference("preference");

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.hf_switch_env_preference, rootKey);
        switchPreferenceCompat = findPreference("enable_production");
        setButtonSwitchStatus();
        preference = findPreference("preference");
        if (preference != null)
            preference.setOnPreferenceClickListener(this);
        switchPreferenceCompat.setOnPreferenceChangeListener(this);
    }

    private void clearApplicationData() {
        File appDir = new File(Environment.getDataDirectory() + File.separator + "data/org.smartregister.hf");
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
                }
            }
        } else {
            // TODO
        }

        getActivity().finish();
        System.exit(0);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equalsIgnoreCase("preference")) {
            if (countClick < 7) {
                countClick++;
            }
            if (countClick == 7) {
                switchPreferenceCompat.setVisible(true);
                preference.setVisible(false);
            }
        }
        return false;
    }

    public void setButtonSwitchStatus() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String env = sharedPreferences.getString(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT, "");
        switchPreferenceCompat.setChecked(env.equalsIgnoreCase(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT));
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        final boolean[] userAgreed = {false};
        if ((Boolean) newValue) {
            confirmSwitchingEnvironment(getActivity(), new OnDialogButtonClick() {
                @Override
                public void onOkButtonClick() {
                    switchEnvironment(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT, BuildConfig.opensrp_url_production);
                    switchPreferenceCompat.setChecked(true);
                    userAgreed[0] = true;
                }

                @Override
                public void onCancelButtonClick() {
                    switchPreferenceCompat.setChecked(false);
                    userAgreed[0] = false;
                }
            }, Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT);
        } else {
            confirmSwitchingEnvironment(getActivity(), new OnDialogButtonClick() {
                @Override
                public void onOkButtonClick() {
                    switchEnvironment(Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT, BuildConfig.opensrp_url_staging);
                    switchPreferenceCompat.setChecked(false);
                    userAgreed[0] = true;
                }

                @Override
                public void onCancelButtonClick() {
                    switchPreferenceCompat.setChecked(true);
                    userAgreed[0] = false;
                }
            }, Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT);
        }
        return userAgreed[0];
    }

    private void confirmSwitchingEnvironment(Context context, final OnDialogButtonClick onDialogButtonClick, String environment) {
        final AlertDialog alert = new AlertDialog.Builder(context, R.style.SettingsAlertDialog).create();
        View title_view = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog_title, null);
        alert.setCustomTitle(title_view);
        alert.setMessage(String.format(getString(R.string.switch_environment_message), environment));
        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(context, context.getResources().getString(R.string.referral_submitted), Toast.LENGTH_LONG).show();
                onDialogButtonClick.onOkButtonClick();
                alert.dismiss();
            }
        });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogButtonClick.onCancelButtonClick();
                alert.dismiss();
            }
        });
        alert.show();
    }

    private void switchEnvironment(String env, String baseUrl) {
        AllSharedPreferences preferences = Utils.getAllSharedPreferences();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.savePreference(AllConstants.DRISHTI_BASE_URL, baseUrl);
        sharedPreferences.edit().putBoolean(Constants.ENVIRONMENT_CONFIG.PREFERENCE_PRODUCTION_ENVIRONMENT_SWITCH, true).apply();
        preferences.savePreference(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT, env);
        clearApplicationData();
    }
}
