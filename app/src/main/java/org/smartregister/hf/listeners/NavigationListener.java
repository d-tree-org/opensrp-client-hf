package org.smartregister.hf.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import org.smartregister.hf.R;
import org.smartregister.hf.activity.HomeActivity;
import org.smartregister.hf.activity.ReportsActivity;
import org.smartregister.hf.adapter.NavigationAdapter;
import org.smartregister.hf.util.Constants;

public class NavigationListener implements View.OnClickListener {

    private Activity activity;
    private NavigationAdapter navigationAdapter;

    public NavigationListener(Activity activity, NavigationAdapter navigationAdapter) {
        this.activity = activity;
        this.navigationAdapter = navigationAdapter;
    }

    @Override
    public void onClick(View v) {

        if (v.getTag() != null) {

            if (v.getTag() instanceof String) {
                String tag = (String) v.getTag();
                //All cases down here needs to be changed to reflect the ADDO application. Based on
                // the functionality we need to define a new navigation menu for ADDO application
                switch (tag) {
                    case Constants.DrawerMenu.HOME:
                        startRegisterActivity(HomeActivity.class);
                        break;
                    case Constants.DrawerMenu.REPORTS:
                        startRegisterActivity(ReportsActivity.class);
                        break;
                    default:
                        break;
                }

                navigationAdapter.setSelectedView(tag);
            }
        }

    }

    private void startRegisterActivity(Class registerClass) {
        Intent intent = new Intent(activity, registerClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        activity.finish();
    }
}
