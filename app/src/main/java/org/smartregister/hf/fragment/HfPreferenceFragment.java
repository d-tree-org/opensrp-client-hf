package org.smartregister.hf.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceFragmentCompat;

import org.json.JSONObject;
import org.smartregister.hf.R;
import org.smartregister.hf.repository.HfSharedPreference;
import org.smartregister.hf.util.Constants;
import org.smartregister.simprint.OnDialogButtonClick;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class HfPreferenceFragment extends PreferenceFragmentCompat {

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
    }

    private void switchToProduction() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        HfSharedPreference hfSharedPreferences = new HfSharedPreference(sharedPreferences);
        hfSharedPreferences.updateOpensrpEnviroment(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT);
        writeEnvironmentConfigurations(Constants.ENVIRONMENT_CONFIG.PRODUCTION_ENVIROMENT);
        clearApplicationData();
    }

    private void switchToTest() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        HfSharedPreference hfSharedPreferences = new HfSharedPreference(sharedPreferences);
        hfSharedPreferences.updateOpensrpEnviroment(Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT);
        writeEnvironmentConfigurations(Constants.ENVIRONMENT_CONFIG.TEST_ENVIROMENT);
        clearApplicationData();
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

    private void writeEnvironmentConfigurations(String env) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("env", env);

            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Kituoni", "env_switch.json");

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonObject.toString());
            bufferedWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
