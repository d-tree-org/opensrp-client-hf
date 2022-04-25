package org.smartregister.hf.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.smartregister.hf.fragment.HfPreferenceFragment;
import org.smartregister.hf.util.FileUtils;

import timber.log.Timber;

public class HfSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new HfPreferenceFragment()).commit();

    }

    public void prepareSwitchFolder() {
        String rootFolder = "Kituoni";
        createFolders(rootFolder, false);
        boolean onSdCard = FileUtils.canWriteToExternalDisk();
        if (onSdCard)
            createFolders(rootFolder, true);
    }

    public void createFolders(String rootFolder, boolean onSdCard) {
        try {
            FileUtils.createDirectory(rootFolder, onSdCard);
        } catch (Exception e) {
            Timber.v(e);
        }
    }
}
