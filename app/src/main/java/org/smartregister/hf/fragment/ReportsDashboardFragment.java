package org.smartregister.hf.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.smartregister.hf.util.ChartUtils;
import org.smartregister.reporting.domain.IndicatorQuery;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.ReportIndicator;
import org.smartregister.domain.Task;
import org.smartregister.hf.R;
import org.smartregister.hf.contract.ReportsDashboardContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportsDashboardFragment extends Fragment implements ReportsDashboardContract.View {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Initialize all the reporting data here
         *

        String allReferralsIssuedW = "select * from task " +
                "where status = '"+ Task.TaskStatus.READY +"' and " +
                "business_status = 'Referred' and " +
                "datetime(authored_on/1000, 'unixepoch') > date('now', 'start of day', '-2 days')";
        */

        addReportIndicators();

    }


    private void addReportIndicators(){
        List<ReportIndicator> reportIndicators = new ArrayList<>();
        List<IndicatorQuery> indicatorQueries = new ArrayList<>();

        String allReferralsIssuedW = "select * from task where business_status = 'Referred' ";

        ReportIndicator allReferralsIndicator = new ReportIndicator();
        allReferralsIndicator.setKey(ChartUtils.allReferrals);
        allReferralsIndicator.setDescription("All Referrals Issued to facility within the ward");
        reportIndicators.add(allReferralsIndicator);


        IndicatorQuery allReferralsIndicatorQuery = new IndicatorQuery();
        allReferralsIndicatorQuery.setIndicatorCode(ChartUtils.allReferrals);
        allReferralsIndicatorQuery.setDbVersion(0);
        allReferralsIndicatorQuery.setId(null);
        allReferralsIndicatorQuery.setQuery(allReferralsIssuedW);
        indicatorQueries.add(allReferralsIndicatorQuery);

        //Add Indicators to the presenter



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_dashboard, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         *
         * Work with the UI elements once they are created
         *
         */

    }

    @Override
    public void refreshUI() {

    }

    @Override
    public void buildVisualization(ViewGroup viewGroup) {

    }

    @Override
    public List<Map<String, IndicatorTally>> getIndicatorTallies() {
        return null;
    }

    @Override
    public void setIndicatorTallies(List<Map<String, IndicatorTally>> tallies) {

    }
}
