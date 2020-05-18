package org.smartregister.hf.job;

import android.content.Intent;

import com.evernote.android.job.Job;

import org.smartregister.AllConstants;
import org.smartregister.hf.sync.SyncTaskIntentService;
import org.smartregister.job.BaseJob;

import androidx.annotation.NonNull;

/**
 * Author : Isaya Mollel on 2020-05-13.
 */
public class KituoniTaskServiceJob extends BaseJob {

    public static final String TAG = "KituoniTaskServiceJob";

    @NonNull
    @Override
    protected Job.Result onRunJob(@NonNull Job.Params params) {
        Intent intent = new Intent(getApplicationContext(), SyncTaskIntentService.class);
        getApplicationContext().startService(intent);
        return params != null && params.getExtras().getBoolean(AllConstants.INTENT_KEY.TO_RESCHEDULE,
                false) ? Job.Result.RESCHEDULE : Job.Result.SUCCESS;
    }

}
