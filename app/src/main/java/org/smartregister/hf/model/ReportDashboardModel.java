package org.smartregister.hf.model;

import org.smartregister.hf.contract.ReportsDashboardContract;
import org.smartregister.reporting.domain.IndicatorQuery;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.ReportIndicator;

import java.util.List;
import java.util.Map;

public class ReportDashboardModel implements ReportsDashboardContract.Model {

    @Override
    public void addIndicator(ReportIndicator indicator) {

    }

    @Override
    public void addIndicatorQuery(IndicatorQuery query) {

    }

    @Override
    public List<Map<String, IndicatorTally>> getIndicatorsDailyTallies() {
        return null;
    }

}
