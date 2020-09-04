package org.smartregister.hf.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import org.smartregister.hf.fragment.HfPreferenceFragment;
import org.smartregister.hf.util.FileUtils;
import org.smartregister.view.activity.SettingsActivity;

import timber.log.Timber;

public class HfSettingsActivity extends SettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(HfSettingsActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                prepareSwitchFolder();
            }
        } else {
            prepareSwitchFolder();
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new HfPreferenceFragment()).commit();

    }

    public void prepareSwitchFolder(){
        String rootFolder = "Kituoni";
        createFolders(rootFolder, false);
        boolean onSdCard = FileUtils.canWriteToExternalDisk();
        if (onSdCard)
            createFolders(rootFolder, true);
    }

    public void createFolders(String rootFolder, boolean onSdCard){
        try {
            FileUtils.createDirectory(rootFolder, onSdCard);
        } catch (Exception e) {
            Timber.v(e);
        }
    }
}
