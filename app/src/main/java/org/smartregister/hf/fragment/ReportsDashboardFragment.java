package org.smartregister.hf.fragment;

import static org.smartregister.reporting.contract.ReportContract.IndicatorView.CountType.LATEST_COUNT;
import static org.smartregister.reporting.util.ReportingUtil.getIndicatorDisplayModel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import org.smartregister.hf.application.HfApplication;
import org.smartregister.hf.presenter.ReportDashboardPresenter;
import org.smartregister.hf.util.ChartUtils;
import org.smartregister.reporting.domain.IndicatorQuery;
import org.smartregister.reporting.domain.IndicatorTally;
import org.smartregister.reporting.domain.ReportIndicator;
import org.smartregister.domain.Task;
import org.smartregister.hf.R;
import org.smartregister.hf.contract.ReportsDashboardContract;
import org.smartregister.reporting.model.NumericDisplayModel;
import org.smartregister.reporting.view.NumericIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportsDashboardFragment extends Fragment implements ReportsDashboardContract.View, LoaderManager.LoaderCallbacks<List<Map<String, IndicatorTally>>> {

    private static ReportsDashboardContract.Presenter presenter;
    private ViewGroup visualizationsViewGroup;
    private List<Map<String, IndicatorTally>> indicatorTallies;

    private static final String HAS_LOADED_SAMPLE_DATA = "has_loaded_sample_data";
    private boolean activityStarted = false;
    private boolean hasLoadedSampleData = true;

    private View spacerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new ReportDashboardPresenter(this);

        addReportIndicators();

        presenter.scheduleRecurringTallyJob();

        getLoaderManager().initLoader(0, null, this).forceLoad();
    }


    private void addReportIndicators(){
        List<ReportIndicator> reportIndicators = new ArrayList<>();
        List<IndicatorQuery> indicatorQueries = new ArrayList<>();

        String allReferralsIssuedW = "select * from task where business_status = 'Referred' ";

        String allAttendedReferralW = "select * from task where status IN ('"+ Task.TaskStatus.COMPLETED +"', '"+ Task.TaskStatus.IN_PROGRESS +"')" +
                " AND business_status = 'Referred' ";

        String currentMonthAttendedReferralW = "select * from task where status IN ('"+ Task.TaskStatus.COMPLETED +"', '"+ Task.TaskStatus.IN_PROGRESS +"')" +
                " AND business_status = 'Referred' and" +
                " datetime(authored_on/1000, 'unixepoch') > date('now', 'start of month')";


        ReportIndicator allReferralsIndicator = new ReportIndicator();
        allReferralsIndicator.setKey(ChartUtils.allReferrals);
        allReferralsIndicator.setDescription("All Referrals Issued to facility within the ward");
        reportIndicators.add(allReferralsIndicator);

        ReportIndicator allAttendedReferralsIndicator = new ReportIndicator();
        allAttendedReferralsIndicator.setKey(ChartUtils.allAttendedReferrals);
        allAttendedReferralsIndicator.setDescription("All Attended Referrals within a ward");
        reportIndicators.add(allAttendedReferralsIndicator);

        ReportIndicator currentMonthAttendedReferralsIndicator = new ReportIndicator();
        currentMonthAttendedReferralsIndicator.setKey(ChartUtils.currentMonthAttendedReferrals);
        currentMonthAttendedReferralsIndicator.setDescription("Current Month Attended Referrals within a ward");
        reportIndicators.add(currentMonthAttendedReferralsIndicator);

        IndicatorQuery allReferralsIndicatorQuery = new IndicatorQuery();
        allReferralsIndicatorQuery.setIndicatorCode(ChartUtils.allReferrals);
        allReferralsIndicatorQuery.setDbVersion(0);
        allReferralsIndicatorQuery.setId(null);
        allReferralsIndicatorQuery.setQuery(allReferralsIssuedW);
        indicatorQueries.add(allReferralsIndicatorQuery);

        IndicatorQuery allAttendedReferralsQuery = new IndicatorQuery();
        allAttendedReferralsQuery.setIndicatorCode(ChartUtils.allAttendedReferrals);
        allAttendedReferralsQuery.setDbVersion(0);
        allAttendedReferralsQuery.setId(null);
        allAttendedReferralsQuery.setQuery(allAttendedReferralW);
        indicatorQueries.add(allAttendedReferralsQuery);

        IndicatorQuery currentMonthAttendedReferralsQuery = new IndicatorQuery();
        currentMonthAttendedReferralsQuery.setIndicatorCode(ChartUtils.currentMonthAttendedReferrals);
        currentMonthAttendedReferralsQuery.setDbVersion(0);
        currentMonthAttendedReferralsQuery.setId(null);
        currentMonthAttendedReferralsQuery.setQuery(currentMonthAttendedReferralW);
        indicatorQueries.add(currentMonthAttendedReferralsQuery);

        //Add Indicators to the presenter
        presenter.addIndicators(reportIndicators);
        presenter.addIndicatorQueries(indicatorQueries);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        spacerView = inflater.inflate(R.layout.report_spacer_view, container, false);
        View rootView = inflater.inflate(R.layout.fragment_report_dashboard, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        visualizationsViewGroup = getView().findViewById(R.id.dashboard_content);

    }

    @Override
    public void refreshUI() {
        buildVisualization(visualizationsViewGroup);
    }

    @Override
    public void buildVisualization(ViewGroup viewGroup) {
        viewGroup.removeAllViews();
        createReportViews(viewGroup);
    }

    private void createReportViews(ViewGroup mainLayout){
        mainLayout.addView(getTitleView("All Referrals Indicators within the ward"));

        NumericDisplayModel allReferralsWard = getIndicatorDisplayModel(LATEST_COUNT, ChartUtils.allReferrals, R.string.all_ward_referrals, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), allReferralsWard).createView());

        NumericDisplayModel allAttendedReferralsWard = getIndicatorDisplayModel(LATEST_COUNT, ChartUtils.allAttendedReferrals, R.string.all_ward_attended_referrals, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), allAttendedReferralsWard).createView());

        NumericDisplayModel currentMonthAttendedReferralsWard = getIndicatorDisplayModel(LATEST_COUNT, ChartUtils.currentMonthAttendedReferrals, R.string.all_ward_current_month_attended_referrals, indicatorTallies);
        mainLayout.addView(new NumericIndicatorView(getContext(), currentMonthAttendedReferralsWard).createView());

        mainLayout.addView(spacerView);

        mainLayout.addView(getTitleView("Referral Indicators within the facility"));

    }

    View getTitleView(String titleText){
        View tv = LayoutInflater.from(this.getContext()).inflate(R.layout.report_indicator_title,null, false);
        TextView title = tv.findViewById(R.id.indicator_title);
        title.setText(titleText);
        return tv;
    }

    @Override
    public List<Map<String, IndicatorTally>> getIndicatorTallies() {
        return indicatorTallies;
    }

    @Override
    public void setIndicatorTallies(List<Map<String, IndicatorTally>> tallies) {
        this.indicatorTallies = tallies;
    }

    @NonNull
    @Override
    public Loader<List<Map<String, IndicatorTally>>> onCreateLoader(int id, @Nullable Bundle args) {
        hasLoadedSampleData = Boolean.parseBoolean(HfApplication.getInstance().getContext().allSharedPreferences().getPreference(HAS_LOADED_SAMPLE_DATA));
        if (!activityStarted){
            activityStarted = true;
            return new ReportIndicatorsLoader(getContext(), false);
        }else{
            return new ReportIndicatorsLoader(getContext(), true);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Map<String, IndicatorTally>>> loader, List<Map<String, IndicatorTally>> data) {
        setIndicatorTallies(data);
        refreshUI();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Map<String, IndicatorTally>>> loader) {

    }

    private static class ReportIndicatorsLoader extends AsyncTaskLoader<List<Map<String, IndicatorTally>>> {

        boolean loadedIndicators;

        public ReportIndicatorsLoader(Context context, boolean alreadyLoaded) {
            super(context);
            this.loadedIndicators = alreadyLoaded;
        }

        @Nullable
        @Override
        public List<Map<String, IndicatorTally>> loadInBackground() {
            List<Map<String, IndicatorTally>> empty = new ArrayList<>();
            if (!loadedIndicators) {
                HfApplication.getInstance().getContext().allSharedPreferences().savePreference(HAS_LOADED_SAMPLE_DATA, "true");
                return presenter.fetchIndicatorsDailytallies();
            }else{
                return empty;
            }
        }
    }

}
