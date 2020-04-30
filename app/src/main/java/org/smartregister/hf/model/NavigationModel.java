package org.smartregister.hf.model;

import org.smartregister.hf.contract.NavigationContract;
import org.smartregister.hf.util.Utils;

import java.util.List;

import timber.log.Timber;

public class NavigationModel implements NavigationContract.Model {

    public static NavigationModel instance;

    public static Flavor flavor = new NavigationModelFlv();

    public static NavigationModel getInstance() {
        if (instance == null) {
            instance = new NavigationModel();
        }

        return instance;
    }


    @Override
    public List<NavigationOption> getNavigationItems() {
        return flavor.getNavigationItems();
    }

    @Override
    public String getCurrentUser() {

        String res = "";
        try {
            res = Utils.getPrefferedName().split(" ")[0];
        } catch (Exception e) {
            Timber.e(e);
        }

        return res;
    }

    interface Flavor {
        List<NavigationOption> getNavigationItems();
    }
}
