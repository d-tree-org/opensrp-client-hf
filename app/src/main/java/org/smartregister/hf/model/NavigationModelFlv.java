package org.smartregister.hf.model;

import org.smartregister.hf.R;
import org.smartregister.hf.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class NavigationModelFlv implements NavigationModel.Flavor {

    public static List<NavigationOption> navigationOptions = new ArrayList<>();

    @Override
    public List<NavigationOption> getNavigationItems() {

        if (navigationOptions.size() == 0 ) {

            NavigationOption opt1 = new NavigationOption(R.mipmap.ic_home, 0, R.string.menu_hf_home, Constants.DrawerMenu.ALL_FAMILIES, 0);
            NavigationOption opt2 = new NavigationOption(R.drawable.ic_reporting, 0, R.string.menu_hf_report, Constants.DrawerMenu.REPORTS, 0);

            navigationOptions.addAll(asList(opt1, opt2));
        }

        return navigationOptions;
    }
}