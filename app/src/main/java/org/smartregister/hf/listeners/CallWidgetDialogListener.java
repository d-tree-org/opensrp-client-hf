package org.smartregister.hf.listeners;


import android.view.View;

import org.smartregister.hf.R;
import org.smartregister.hf.fragment.FamilyCallDialogFragment;
import org.smartregister.hf.util.Utils;

import timber.log.Timber;

public class CallWidgetDialogListener implements View.OnClickListener {

    private static String TAG = CallWidgetDialogListener.class.getCanonicalName();

    private FamilyCallDialogFragment callDialogFragment;

    public CallWidgetDialogListener(FamilyCallDialogFragment dialogFragment) {
        callDialogFragment = dialogFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                callDialogFragment.dismiss();
                break;
            case R.id.call_head_phone:
                try {
                    String phoneNumber = (String) v.getTag();
                    Utils.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                    callDialogFragment.dismiss();
                } catch (Exception e) {
                    Timber.e(e);
                }
                break;
            case R.id.call_caregiver_phone:
                try {
                    String phoneNumber = (String) v.getTag();
                    Utils.launchDialer(callDialogFragment.getActivity(), callDialogFragment, phoneNumber);
                    callDialogFragment.dismiss();
                } catch (Exception e) {
                    Timber.e(e);
                }
                break;
            default:
                break;
        }
    }

}