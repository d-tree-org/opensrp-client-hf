package org.smartregister.hf.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.smartregister.AllConstants;
import org.smartregister.hf.BuildConfig;
import org.smartregister.hf.R;
import org.smartregister.hf.util.Constants;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;

import java.net.MalformedURLException;
import java.net.URL;

public class SwitchEnvironmentFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final String[] listOfEnvironment = new String[]{"Production", "Test"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        builder.setTitle("Select the environment you want to switch to");
        builder.setSingleChoiceItems(R.array.hf_environment, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setSelectedEnvironment(listOfEnvironment[which]);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateEnvironmentUrl(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT, BuildConfig.opensrp_url_production);
            }
        });
        return builder.create();
    }

    private void setSelectedEnvironment(String selectedEnvironment) {
        if (selectedEnvironment.equalsIgnoreCase(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT)) {
            updateEnvironmentUrl(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT, BuildConfig.opensrp_url_production);
        } else {
            updateEnvironmentUrl(Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT, BuildConfig.opensrp_url_staging);
        }
    }

    private void updateEnvironmentUrl(String env, String baseUrl) {
        try {
            AllSharedPreferences preferences = Utils.getAllSharedPreferences();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        Button loginButton = getActivity().findViewById(R.id.login_login_btn);
        if (env.equals(Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT)) {
            loginButton.setBackgroundColor(getResources().getColor(R.color.test_environment_color));
        } else {
            loginButton.setBackgroundColor(getResources().getColor(R.color.primary_color));
        }
    }
}