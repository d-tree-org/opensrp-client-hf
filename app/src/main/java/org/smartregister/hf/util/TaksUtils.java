package org.smartregister.hf.util;

import android.database.Cursor;

import org.joda.time.DateTime;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Task;
import org.smartregister.hf.application.HfApplication;
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
            String q = "select * from task where status = 'READY' AND business_status = 'Referred' ";
            cursor = HfApplication.getInstance().getRepository().getReadableDatabase().rawQuery(q, null);
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
            String q = "select * from task where status = 'READY' AND business_status = 'Referred' AND  authored_on > "+startOfDay.getMillis();
            cursor = HfApplication.getInstance().getRepository().getReadableDatabase().rawQuery(q, null);
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

    public static int getLastThreeDaysTotalReferralCount(){

        Cursor cursor = null;

        String q = "select * from task " +
                "where status = '"+ Task.TaskStatus.READY +"' and " +
                "business_status = 'Referred' and " +
                "datetime(authored_on/1000, 'unixepoch') > date('now', 'start of day', '-2 days')";

        try {
            cursor = HfApplication.getInstance().getRepository().getReadableDatabase().rawQuery(q, null);
            cursor.moveToFirst();
            return cursor.getCount();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //be a good citizen
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
            String q = "select * from task where status IN ('"+ Task.TaskStatus.COMPLETED +"', '"+ Task.TaskStatus.IN_PROGRESS +"')" +
                    " AND business_status = 'Referred' " +
                    " AND last_modified > "+startOfDay.getMillis(); //TODO: When app sync for the first time this field is changed for all the tasks
            cursor = HfApplication.getInstance().getRepository().getReadableDatabase().rawQuery(q, null);
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

        boolean clientHasReferral = false;

        while (referralTasks.iterator().hasNext()){
            Task t = referralTasks.iterator().next();
            if (t.getBusinessStatus().equals("referred")){
                clientHasReferral = true;
            }
        }

        return clientHasReferral;
    }

    public static boolean clientHasReferral(String baseEntityId){
        Cursor cursor = null;
        DateTime today = new DateTime();
        DateTime startOfDay = today.withTimeAtStartOfDay();

        try {
            String q = "select * from task where status  = '"+ Task.TaskStatus.READY +"' "+
                    " AND business_status = 'Referred' AND for = '"+baseEntityId+"' ";
            cursor = HfApplication.getInstance().getRepository().getReadableDatabase().rawQuery(q, null);
            cursor.moveToFirst();
            return cursor.getCount() > 0;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return false;
    }

}
