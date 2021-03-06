package org.smartregister.hf.sync.helper;

import android.util.Log;

import org.smartregister.CoreLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.TaskRepository;
import org.smartregister.sync.helper.TaskServiceHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author : Isaya Mollel on 2020-05-13.
 */
public class KituoniTaskServiceHelper extends TaskServiceHelper {

    protected static KituoniTaskServiceHelper instance;

    public KituoniTaskServiceHelper(TaskRepository taskRepository) {
        super(taskRepository);
    }

    public static KituoniTaskServiceHelper getInstance() {
        if (instance == null) {
            instance = new KituoniTaskServiceHelper(CoreLibrary.getInstance().context().getTaskRepository());
        }
        return instance;
    }

    @Override
    protected List<String> getLocationIds() {
        return LocationHelper.getInstance().locationsFromHierarchy(true, null);
    }

    @Override
    protected Set<String> getPlanDefinitionIds() {
        Set<String> res = new HashSet<>();
        res.add("5270285b-5a3b-4647-b772-c0b3c52e2b71");
        return res;
    }

}
