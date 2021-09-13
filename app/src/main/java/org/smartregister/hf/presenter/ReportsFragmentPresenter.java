package org.smartregister.hf.presenter;

import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.hf.contract.ReportsFragmentContract;

import java.lang.ref.WeakReference;

public class ReportsFragmentPresenter  implements ReportsFragmentContract.Presenter {

    protected WeakReference<ReportsFragmentContract.View> viewWeakReference;
    protected RegisterConfiguration config;
    protected String viewConfigurationIdentifier;

    public ReportsFragmentPresenter(ReportsFragmentContract.View view, String viewConfigurations){
        this.viewWeakReference = new WeakReference<>(view);
        this.viewConfigurationIdentifier = viewConfigurations;
    }

    @Override
    public String getMainCondition() {
        return null;
    }

    @Override
    public String getDefaultSortQuery() {
        return null;
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String s) {

    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }
}
