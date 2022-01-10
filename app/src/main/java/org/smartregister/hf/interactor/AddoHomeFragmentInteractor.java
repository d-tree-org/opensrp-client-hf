package org.smartregister.hf.interactor;

import org.smartregister.hf.contract.HomeFragmentContract;
import org.smartregister.hf.model.DashboardDataModel;
import org.smartregister.hf.util.TaksUtils;

public class AddoHomeFragmentInteractor implements HomeFragmentContract.Interactor {

    @Override
    public void fetchDashboardData(HomeFragmentContract.InteractorCallback callback) {

        int lastThreeDaysReferralCount = TaksUtils.getLastThreeDaysTotalReferralCount();
        int attended = TaksUtils.getAttendedReferrals();

        DashboardDataModel dashboardData = new DashboardDataModel();
        dashboardData.setLastThreeDaysReferralCount(lastThreeDaysReferralCount);
        dashboardData.setReferralsAttendedTodayCount(attended);

        callback.onDashboardDataFetched(dashboardData);

    }
}
