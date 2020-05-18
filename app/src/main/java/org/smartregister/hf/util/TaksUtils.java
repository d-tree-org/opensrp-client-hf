package org.smartregister.hf.util;

import org.smartregister.CoreLibrary;
import org.smartregister.domain.Task;
import org.smartregister.repository.TaskRepository;

import java.util.Set;

/**
 * Author : Isaya Mollel on 2020-05-18.
 */
public class TaksUtils {

    public static boolean isClientRefered(String clientBaseId){

        TaskRepository taskRepository = CoreLibrary.getInstance().context().getTaskRepository();
        Set<Task> referralTasks = taskRepository.getTasksByEntityAndStatus(
                "5270285b-5a3b-4647-b772-c0b3c52e2b71",
                clientBaseId,
                Task.TaskStatus.READY);
        if (referralTasks.size() > 0){
            return true;
        }else {
            return false;
        }
    }

}
