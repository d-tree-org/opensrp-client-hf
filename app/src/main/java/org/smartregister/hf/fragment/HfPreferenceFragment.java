package org.smartregister.hf.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONObject;
import org.smartregister.hf.repository.HfSharedPreference;
import org.smartregister.hf.util.Constants;
import org.smartregister.simprint.OnDialogButtonClick;
import org.smartregister.view.activity.SettingsActivity;

import org.smartregister.hf.R;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class HfPreferenceFragment extends SettingsActivity.MyPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.hfpreference);

        Preference addoEnvironmentPreference = findPreference("enable_production");

        if (addoEnvironmentPreference != null) {

            final SwitchPreference addoSwitchPreference = (SwitchPreference) addoEnvironmentPreference;

            final boolean[] userAgreed = {false};

            addoSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String environment = preference.getSharedPreferences().getString(Constants.ENVIRONMENT_CONFIG.OPENSRP_HF_ENVIRONMENT, "test");

                    if(newValue instanceof Boolean && ((Boolean) newValue != preference.getSharedPreferences().getBoolean("enable_production", false))) {
                        if ("test".equalsIgnoreCase(environment)) {
                            confirmSwitchingEnvironment(getActivity(), new OnDialogButtonClick() {
                                @Override
                                public void onOkButtonClick() {
                                    switchToProduction();
                                    preference.getSharedPreferences().edit().putBoolean("enable_production", true).commit();
                                    addoSwitchPreference.setChecked(true);
                                    userAgreed[0] = true;
                                }

                                @Override
                                public void onCancelButtonClick() {
                                    addoSwitchPreference.setChecked(false);
                                    userAgreed[0] = false;
                                }
                            }, "Production");

                        } else {
                            confirmSwitchingEnvironment(getActivity(), new OnDialogButtonClick() {
                                @Override
                                public void onOkButtonClick() {
                                    switchToTest();
                                    preference.getSharedPreferences().edit().putBoolean("enable_production", false).commit();
                                    addoSwitchPreference.setChecked(false);
                                    userAgreed[0] = true;
                                }

                                @Override
                                public void onCancelButtonClick() {
                                    addoSwitchPreference.setChecked(true);
                                    userAgreed[0] = false;
                                }
                            }, "Test");

                        }

                    }

                    return userAgreed[0];

                }
            });
        }
    }

    private void switchToProduction() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        HfSharedPreference addoSharedPreferences = new HfSharedPreference(sharedPreferences);
        addoSharedPreferences.updateOpensrpADDOEnvironment("production");
        writeEnvironmentConfigurations("production");
        Toast.makeText(getActivity(), "I am switching to production " + addoSharedPreferences.getOpensrpADDOEnvironment(), Toast.LENGTH_SHORT).show();
    }

    private void switchToTest() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        HfSharedPreference addoSharedPreferences = new HfSharedPreference(sharedPreferences);
        addoSharedPreferences.updateOpensrpADDOEnvironment("test");
        writeEnvironmentConfigurations("test");
        Toast.makeText(getActivity(), "I am switching to test", Toast.LENGTH_SHORT).show();
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

    private void clearDeviceData() {

    }

    private void writeEnvironmentConfigurations(String env) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("env", env);

            Writer output = null;
            File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "Kituoni", "env_switch.json");
//            if (!file.exists()){
//                file.mkdirs();
//            }

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonObject.toString());
            bufferedWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
