package org.smartregister.hf.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.hf.R;
import org.smartregister.hf.adapter.AddoLocationRecyclerViewProviderAdapter;
import org.smartregister.hf.contract.AddoHomeFragmentContract;
import org.smartregister.hf.custom_views.NavigationMenu;
import org.smartregister.hf.model.AddoHomeFragmentModel;
import org.smartregister.hf.model.DashboardDataModel;
import org.smartregister.hf.presenter.AddoHomeFragmentPresenter;
import org.smartregister.hf.view.EmptystateView;
import org.smartregister.hf.viewmodels.HomescreenViewModel;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class HomeFragment extends BaseRegisterFragment implements AddoHomeFragmentContract.View {

    private RecyclerView addoLocationView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<String> villageLocations = new ArrayList<>();
    private HomescreenViewModel model;
    private TextView tvNoVillage;
    private EmptystateView emptystateView;

    private TextView pastThreeDaysReferrals;
    private TextView todaysAttendedReferrals;

    @Override
    public void setupViews(View view) {
        this.rootView = view;

        Toolbar toolbar = view.findViewById(R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);

        toolbar.setContentInsetStartWithNavigation(0);

        NavigationMenu.getInstance(this.getActivity(), null, toolbar);

        View navBarContainer = view.findViewById(R.id.register_nav_bar_container);
        navBarContainer.setFocusable(false);

        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setText(R.string.addo_app_home);
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(),
                    titleView.getPaddingBottom());
            titleView.setVisibility(View.GONE);
        }

        pastThreeDaysReferrals = view.findViewById(R.id.three_days_total_referral_count);
        todaysAttendedReferrals = view.findViewById(R.id.attended_referral_count);

        tvNoVillage = view.findViewById(R.id.empty_view);
        emptystateView = view.findViewById(R.id.ev_no_villages);

        addoLocationView = (RecyclerView) view.findViewById(R.id.addo_villages_recycler_view);
        addoLocationView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this.getActivity());
        addoLocationView.setLayoutManager(layoutManager);
        registerAdapter(addoLocationView);

    }

    private void registerAdapter(RecyclerView view) {
        if (view != null) {
            if ( presenter().getLocations() != null ) {
                this.villageLocations = presenter().getLocations();
            }
        }

        if (this.villageLocations.size() == 0) {
            emptystateView.setVisibility(View.VISIBLE);
        }

        AddoLocationRecyclerViewProviderAdapter mAdapter = new AddoLocationRecyclerViewProviderAdapter(villageLocations
                , this.getActivity());
        view.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(village -> {
            // if the selected item is other village then take the user to Advanced search otherwise fp scan
            if (!village.equalsIgnoreCase(String.valueOf(R.string.addo_other_village))) {
                model.setSelectedVillage(village);
                ((BaseRegisterActivity) Objects.requireNonNull(getActivity())).switchToFragment(2);
            } else {
                ((BaseRegisterActivity) Objects.requireNonNull(getActivity())).switchToFragment(3);
            }

        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hf_home, container, false);
        model = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new HomescreenViewModel();
            }
        }).get(HomescreenViewModel.class);
        this.rootView = view;
        this.setupViews(view);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchDashboardMetrics();
    }

    private void fetchDashboardMetrics(){
        model.getMetrics().observe(this, new Observer<DashboardDataModel>() {
            @Override
            public void onChanged(DashboardDataModel dashboardDataModel) {
                pastThreeDaysReferrals.setText(String.valueOf(dashboardDataModel.getLastThreeDaysReferralCount()));
                todaysAttendedReferrals.setText(String.valueOf(dashboardDataModel.getReferralsAttendedTodayCount()));
            }
        });
    }

    @Override
    public void initializeAdapter
            (Set<org.smartregister.configurableviews.model.View> visibleColumns) {
    }

    @Override
    public AddoHomeFragmentContract.Presenter presenter() {
        return (AddoHomeFragmentContract.Presenter) presenter;
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new AddoHomeFragmentPresenter(this, AddoHomeFragmentModel.getInstance(), viewConfigurationIdentifier);
    }

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return null;
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onViewClicked(View view) {

    }

    @Override
    public void showNotFoundPopup(String s) {

    }

}
