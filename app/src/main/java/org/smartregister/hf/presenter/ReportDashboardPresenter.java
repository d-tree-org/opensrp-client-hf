package org.smartregister.hf.presenter;

import org.smartregister.hf.contract.ReportsDashboardContract;
import org.smartregister.hf.interactor.ReportDashboardInteractor;
import org.smartregister.hf.model.ReportDashboardModel;
import org.smartregister.reporting.domain.IndicatorQuery;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.ReportIndicator;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

public class ReportDashboardPresenter implements ReportsDashboardContract.Presenter {

    private WeakReference<ReportsDashboardContract.View> weakReference;
    private ReportsDashboardContract.Model model;
    private ReportsDashboardContract.Interactor interactor;

    public ReportDashboardPresenter(ReportsDashboardContract.View view){
        this.weakReference = new WeakReference<>(view);
        this.interactor = new ReportDashboardInteractor();
        this.model = new ReportDashboardModel();
    }

    @Override
    public void onResume() {
        if (getView() != null)
            getView().refreshUI();
    }

    @Override
    public List<Map<String, IndicatorTally>> fetchIndicatorsDailytallies() {
        return model.getIndicatorsDailyTallies();
    }

    @Override
    public void addIndicators(List<ReportIndicator> indicators) {
        for (ReportIndicator indicator : indicators){
            this.model.addIndicator(indicator);
        }
    }

    @Override
    public void addIndicatorQueries(List<IndicatorQuery> queries) {
        for(IndicatorQuery query : queries){
            model.addIndicatorQuery(query);
        }
    }

    @Override
    public void scheduleRecurringTallyJob() {
        if (interactor != null)
            interactor.scheduleDailyTallyJob();
    }

    public ReportsDashboardContract.View getView(){
        if (weakReference != null){
            return weakReference.get();
        }
        return null;
    }

}
