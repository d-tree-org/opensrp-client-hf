package org.smartregister.hf.contract;

import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.Set;

public interface ReportsFragmentContract {

    public interface Model {
        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String var1);

        Set<View> getRegisterActiveColumns(String var1);
    }

    public interface View extends BaseRegisterFragmentContract.View {
        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> var1);
        ReportsFragmentContract.Presenter presenter();
    }

    public interface Presenter extends BaseRegisterFragmentContract.Presenter {
        String getMainCondition();

        String getDefaultSortQuery();
    }

}