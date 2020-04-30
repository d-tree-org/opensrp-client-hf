package org.smartregister.hf.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import org.smartregister.hf.activity.FamilyRegisterActivity;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.activity.BaseRegisterActivity;

public class AddoBottomNavigationListener extends BottomNavigationListener {
    private Activity context;

    public AddoBottomNavigationListener(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        super.onNavigationItemSelected(item);

        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) context;

        if (item.getItemId() == org.smartregister.family.R.id.action_family) {
            if (context instanceof FamilyRegisterActivity) {
                baseRegisterActivity.switchToBaseFragment();
            } else {
                Intent intent = new Intent(context, FamilyRegisterActivity.class);
                context.startActivity(intent);
                context.finish();
            }
        } else if (item.getItemId() == org.smartregister.family.R.id.action_scan_qr) {
            baseRegisterActivity.startQrCodeScanner();
            return false;
        } else if (item.getItemId() == org.smartregister.family.R.id.action_register) {

            if (context instanceof FamilyRegisterActivity) {
                baseRegisterActivity.startRegistration();
            } else {
                FamilyRegisterActivity.startFamilyRegisterForm(context);
            }

            return false;
        } else if (item.getItemId() == org.smartregister.family.R.id.action_job_aids) {
            //view.setSelectedItemId(R.id.action_family);
            //Intent intent = new Intent(context, JobAidsActivity.class);
            //context.startActivity(intent);
            return false;
        }

        return true;
    }
}
