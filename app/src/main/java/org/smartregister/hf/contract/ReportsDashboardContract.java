package org.smartregister.hf.contract;

import android.view.ViewGroup;

import org.smartregister.reporting.domain.IndicatorQuery;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.ReportIndicator;

import java.util.List;
import java.util.Map;

public interface ReportsDashboardContract {

    public interface View {

        void refreshUI();

        void buildVisualization(ViewGroup viewGroup);

        List<Map<String, IndicatorTally>> getIndicatorTallies();

        void setIndicatorTallies(List<Map<String, IndicatorTally>> tallies);

    }

    public interface Interactor {
        void scheduleDailyTallyJob();
    }

    public interface Model {
        void addIndicator(ReportIndicator indicator);

        void addIndicatorQuery(IndicatorQuery query);

        List<Map<String, IndicatorTally>> getIndicatorsDailyTallies();
    }

    public interface Presenter {

        void onResume();

        List<Map<String, IndicatorTally>> fetchIndicatorsDailytallies();

        void addIndicators(List<ReportIndicator> indicators);

        void addIndicatorQueries(List<IndicatorQuery> queries);

        void scheduleRecurringTallyJob();

    }

}
