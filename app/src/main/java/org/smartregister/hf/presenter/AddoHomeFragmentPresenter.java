package org.smartregister.hf.presenter;

import org.smartregister.hf.contract.HomeFragmentContract;
import org.smartregister.hf.interactor.AddoHomeFragmentInteractor;
import org.smartregister.hf.model.AddoHomeFragmentModel;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AddoHomeFragmentPresenter implements HomeFragmentContract.Presenter{

    protected WeakReference<HomeFragmentContract.View> viewReference;
    protected HomeFragmentContract.Model model;
    protected RegisterConfiguration config;
    protected Set<View> visibleColumns = new TreeSet<>();
    private String viewConfigurationIdentifier;
    private HomeFragmentContract.Interactor interactor;

    public AddoHomeFragmentPresenter(HomeFragmentContract.View viewReference, HomeFragmentContract.Model model,
                                     String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(viewReference);
        this.model = AddoHomeFragmentModel.getInstance();
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.interactor = new AddoHomeFragmentInteractor();
    }

    @Override
    public void processViewConfigurations() {
    }

    private void setVisibleColumns(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    protected HomeFragmentContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
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
    @Override
    public List<String> getLocations() {
        return model.getAddoUserAllowedLocation();
    }
}
