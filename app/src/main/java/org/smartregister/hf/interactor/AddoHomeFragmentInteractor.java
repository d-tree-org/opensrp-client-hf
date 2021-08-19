package org.smartregister.hf.interactor;

import org.smartregister.hf.contract.AddoHomeContract;
import org.smartregister.hf.contract.AddoHomeFragmentContract;
import org.smartregister.hf.model.DashboardDataModel;
import org.smartregister.hf.util.TaksUtils;

import java.util.Date;

public class AddoHomeFragmentInteractor implements AddoHomeFragmentContract.Interactor {

    @Override
    public void fetchDashboardData(AddoHomeFragmentContract.InteractorCallback callback) {

        int lastThreeDaysReferralCount = TaksUtils.getLastThreeDaysTotalReferralCount();
        int attended = TaksUtils.getAttendedReferrals();

        DashboardDataModel dashboardData = new DashboardDataModel();
        dashboardData.setLastThreeDaysReferralCount(lastThreeDaysReferralCount);
        dashboardData.setReferralsAttendedTodayCount(attended);

        callback.onDashboardDataFetched(dashboardData);

    }

    public void fetchByVillageReferralCount(){

    }

}
