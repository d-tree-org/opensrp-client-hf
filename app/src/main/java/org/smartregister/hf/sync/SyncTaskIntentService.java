package org.smartregister.hf.sync;

import android.app.IntentService;
import android.content.Intent;

import org.jetbrains.annotations.Nullable;
import org.smartregister.domain.FetchStatus;
import org.smartregister.hf.sync.helper.KituoniTaskServiceHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.util.NetworkUtils;
import org.smartregister.util.SyncUtils;

/**
 * Author : Isaya Mollel on 2020-05-13.
 */
public class SyncTaskIntentService extends IntentService {

    private static final String TAG = "SyncTaskIntentService";

    private SyncUtils syncUtils;

    public SyncTaskIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!NetworkUtils.isNetworkAvailable()) {
            sendSyncStatusBroadcastMessage(FetchStatus.noConnection);
            return;
        }
        if (!syncUtils.verifyAuthorization()) {
            syncUtils.logoutUser();
            return;

        }
        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted);
        doSync();
    }

    private void sendSyncStatusBroadcastMessage(FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        syncUtils = new SyncUtils(getBaseContext());
        return super.onStartCommand(intent, flags, startId);
    }


    private void doSync() {
        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted);
        KituoniTaskServiceHelper taskServiceHelper = KituoniTaskServiceHelper.getInstance();

        sendSyncStatusBroadcastMessage(FetchStatus.fetchStarted);
        taskServiceHelper.syncTasks();
    }

}
