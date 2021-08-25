package org.smartregister.hf.interactor;

import org.smartregister.hf.contract.ReportsDashboardContract;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;

public class ReportDashboardInteractor implements ReportsDashboardContract.Interactor {

    @Override
    public void scheduleDailyTallyJob() {
        RecurringIndicatorGeneratingJob.scheduleJobImmediately(RecurringIndicatorGeneratingJob.TAG);
    }
}
