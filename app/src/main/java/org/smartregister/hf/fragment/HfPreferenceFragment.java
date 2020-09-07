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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.smartregister.hf.repository.HfSharedPreference;
import org.smartregister.hf.util.Constants;
import org.smartregister.simprint.OnDialogButtonClick;
import org.smartregister.view.activity.SettingsActivity;

import org.smartregister.hf.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class HfPreferenceFragment extends SettingsActivity.MyPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference switchReference = database.getReference("config/switch");

        // Read from the database
        switchReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                boolean value = dataSnapshot.getValue(Boolean.class);
                if (value){

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
                }else{

                }

                Log.d("FB", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("FB", "Failed to read value.", error.toException());
            }
        });

    }

    private void switchToProduction() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        HfSharedPreference hfSharedPreferences = new HfSharedPreference(sharedPreferences);
        hfSharedPreferences.updateOpensrpEnviroment("production");
        writeEnvironmentConfigurations("production");
        clearApplicationData();
        Toast.makeText(getActivity(), "I am switching to production " + hfSharedPreferences.getOpensrpEnviroment(), Toast.LENGTH_SHORT).show();
    }

    private void switchToTest() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        HfSharedPreference hfSharedPreferences = new HfSharedPreference(sharedPreferences);
        hfSharedPreferences.updateOpensrpEnviroment("test");
        writeEnvironmentConfigurations("test");
        clearApplicationData();
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

    private void clearApplicationData() {
        File appDir = new File(Environment.getDataDirectory() + File.separator + "data/org.smartregister.hf");
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s +" DELETED");
                }
            }
        } else {
            // TODO
        }

        getActivity().finish();
        System.exit(0);
    }

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

    private void writeEnvironmentConfigurations(String env) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("env", env);

            File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "Kituoni", "env_switch.json");

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(jsonObject.toString());
            bufferedWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
