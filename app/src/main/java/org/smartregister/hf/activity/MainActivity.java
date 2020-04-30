package org.smartregister.hf.activity;


import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.smartregister.hf.R;
import org.smartregister.hf.application.AddoApplication;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Logout(View v){
        AddoApplication.getInstance().logoutCurrentUser();
    }

}
