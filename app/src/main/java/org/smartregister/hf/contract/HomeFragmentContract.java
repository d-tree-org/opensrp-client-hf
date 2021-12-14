package org.smartregister.hf.contract;

import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.hf.model.DashboardDataModel;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Set;

public interface HomeFragmentContract {

    interface View extends BaseRegisterFragmentContract.View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);

        HomeFragmentContract.Presenter presenter();

    }

    interface Presenter extends BaseRegisterFragmentContract.Presenter{

        List<String> getLocations();

        void processViewConfigurations();

    }

    interface Model {

        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String viewConfigurationIdentifier);

        List<String> getAddoUserAllowedLocation();
    }

    interface Interactor {
        void fetchDashboardData(InteractorCallback callback);
    }

    interface InteractorCallback {
        void onDashboardDataFetched(DashboardDataModel dashboardData);
    }
}
