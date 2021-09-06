package org.smartregister.hf.interactor;

import org.smartregister.hf.contract.ReportsDashboardContract;
import org.smartregister.reporting.contract.ReportContract;
import org.smartregister.reporting.job.RecurringIndicatorGeneratingJob;

import java.util.concurrent.TimeUnit;

public class ReportDashboardInteractor implements ReportContract.Interactor {

    public ReportDashboardInteractor(){}

    @Override
    public void scheduleDailyTallyJob() {
        RecurringIndicatorGeneratingJob.scheduleJob(RecurringIndicatorGeneratingJob.TAG,
                TimeUnit.MINUTES.toMillis(1), TimeUnit.MINUTES.toMillis(1));
    }
}
