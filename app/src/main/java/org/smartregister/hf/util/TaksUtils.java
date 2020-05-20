package org.smartregister.hf.util;

import android.database.Cursor;

import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Task;
import org.smartregister.hf.application.AddoApplication;
import org.smartregister.repository.TaskRepository;

import java.util.Set;

import androidx.annotation.NonNull;

/**
 * Author : Isaya Mollel on 2020-05-18.
 */
public class TaksUtils {

    public static int getReferralCount(){
        Cursor cursor = null;

        try {
            String q = "select * from task where status = 'READY'";
            cursor = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(q, null);
            cursor.moveToFirst();
            return cursor.getCount();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return 0;
    }

    public static int getTodayReferrals(){
        Cursor cursor = null;
        DateTime today = new DateTime();
        DateTime startOfDay = today.withTimeAtStartOfDay();

        try {
            String q = "select * from task where status = 'READY' and authored_on > "+startOfDay.getMillis();
            cursor = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(q, null);
            cursor.moveToFirst();
            return cursor.getCount();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return 0;
    }

    public static int getAttendedReferrals(){
        Cursor cursor = null;
        DateTime today = new DateTime();
        DateTime startOfDay = today.withTimeAtStartOfDay();

        try {
            String q = "select * from task where status = '"+ Task.TaskStatus.COMPLETED +"' and authored_on > "+startOfDay.getMillis();
            cursor = AddoApplication.getInstance().getRepository().getReadableDatabase().rawQuery(q, null);
            cursor.moveToFirst();
            return cursor.getCount();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return 0;
    }


    public static Set<Task> getReferralTask(@NonNull String baseEntityId){

        /**
         * Optionally we could use raw query to fetch the task with the current baseEntityId
         */
        TaskRepository taskRepository = CoreLibrary.getInstance().context().getTaskRepository();
        Set<Task> referralTasks = taskRepository.getTasksByEntityAndStatus(
                "5270285b-5a3b-4647-b772-c0b3c52e2b71",
                baseEntityId,
                Task.TaskStatus.READY);

        return referralTasks;
    }

    public static boolean getReferralStatus(String clientBaseId){

        TaskRepository taskRepository = CoreLibrary.getInstance().context().getTaskRepository();
        Set<Task> referralTasks = taskRepository.getTasksByEntityAndStatus(
                "5270285b-5a3b-4647-b772-c0b3c52e2b71",
                clientBaseId,
                Task.TaskStatus.READY);

        //referralTasks.toArray();

        return !referralTasks.isEmpty();
    }

}
