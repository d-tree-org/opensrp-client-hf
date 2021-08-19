package org.smartregister.hf.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import org.smartregister.hf.R;
import org.smartregister.hf.contract.ReportsFragmentContract;
import org.smartregister.hf.custom_views.NavigationMenu;
import org.smartregister.hf.presenter.ReportsFragmentPresenter;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Set;

public class ReportFragment extends BaseRegisterFragment implements ReportsFragmentContract.View {

    public ReportFragment(){}

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);

        NavigationMenu.getInstance(getActivity(), null, toolbar);

        View navbarContainer = view.findViewById(R.id.register_nav_bar_container);
        navbarContainer.setFocusable(false);

        qrCodeScanImageView = (ImageView)view.findViewById(R.id.scanQrCode);
        if (qrCodeScanImageView != null) {
            qrCodeScanImageView.setVisibility(View.GONE);
        }

        View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setBackgroundResource(R.color.customAppThemeBlue);
        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, 0, 0);
        }
        searchBarLayout.setVisibility(View.GONE);

        TextView filterView = (TextView)view.findViewById(R.id.filter_text_view);
        if (filterView != null) {
            filterView.setText(this.getString(R.string.sort));
            filterView.setVisibility(View.GONE);
        }

        ImageView logo = (ImageView)view.findViewById(R.id.opensrp_logo_image_view);
        if (logo != null) {
            logo.setVisibility(View.GONE);
        }

        CustomFontTextView titleView = (CustomFontTextView)view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText("Monthly Activity");
            titleView.setFontVariant(FontVariant.REGULAR);
        }

        if (clientsView != null)
            clientsView.setVisibility(View.GONE);

        headerTextDisplay = view.findViewById(R.id.header_text_display);
        filterStatus = view.findViewById(R.id.filter_status);
        filterRelativeLayout = view.findViewById(R.id.filter_display_view);
        syncProgressBar = view.findViewById(R.id.sync_progress_bar);
        syncButton = view.findViewById(R.id.sync_refresh);

        headerTextDisplay.setVisibility(View.GONE);
        filterStatus.setVisibility(View.GONE);
        filterRelativeLayout.setVisibility(View.GONE);
        syncButton.setVisibility(View.GONE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);

        Toolbar toolbar = view.findViewById(R.id.register_toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(activity.getIntent().getStringExtra(TOOLBAR_TITLE));
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        activity.getSupportActionBar().setLogo(R.drawable.round_white_background);
        activity.getSupportActionBar().setDisplayUseLogoEnabled(false);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.rootView = view;
        this.setupViews(rootView);
        return rootView;
    }

    @Override
    public void setTotalPatients() {
        //Stubbed out of the parent calss
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        //Add reports dashboard
    }

    @Override
    protected void initializePresenter() {
        if (this.getActivity() != null){
            presenter = new ReportsFragmentPresenter(this, null);
        }
    }

    @Override
    public void setUniqueID(String s) {
        if (this.getSearchView() != null) {
            this.getSearchView().setText(s);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.reports_activity_fragment;
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

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> var1) {

    }

    @Override
    public ReportsFragmentContract.Presenter presenter() {
        return (ReportsFragmentPresenter) presenter;
    }
}
