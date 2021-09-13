package org.smartregister.hf.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.smartregister.hf.contract.AddoHomeFragmentContract;
import org.smartregister.hf.interactor.AddoHomeFragmentInteractor;
import org.smartregister.hf.model.DashboardDataModel;

/**
 * Author : Isaya Mollel on 2021-01-18.
 */

public class HomescreenViewModel extends ViewModel {

    private final MutableLiveData<String> selectedVillage = new MutableLiveData<String>();

    private MutableLiveData<DashboardDataModel> metrics;

    public HomescreenViewModel() {}

    public void setSelectedVillage(String village) {
        selectedVillage.setValue(village);
    }

    public LiveData<String> getSelectedVillage() {
        return selectedVillage;
    }

    public LiveData<DashboardDataModel> getMetrics() {
        if (metrics == null){
            loadAttendedReferrals();
        }
        return metrics;
    }

    private void loadAttendedReferrals(){
        AddoHomeFragmentInteractor interactor = new AddoHomeFragmentInteractor();
        metrics  = new MutableLiveData<>();
        interactor.fetchDashboardData(new AddoHomeFragmentContract.InteractorCallback() {
            @Override
            public void onDashboardDataFetched(DashboardDataModel dashboardData) {
                metrics.setValue(dashboardData);
            }
        });
    }

}