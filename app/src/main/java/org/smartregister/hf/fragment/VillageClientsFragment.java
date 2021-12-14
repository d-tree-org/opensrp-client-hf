package org.smartregister.hf.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import org.smartregister.configurableviews.model.View;
import org.smartregister.hf.contract.VillageClientsFragmentContract;
import org.smartregister.hf.viewmodels.HomescreenViewModel;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Set;

public class VillageClientsFragment extends BaseRegisterFragment implements VillageClientsFragmentContract.View {

    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";

    private HomescreenViewModel viewModel;
    private android.view.View view;
    private String villageSelected;

    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initializeAdapter(Set<View> configurableColumns) {

    }

    @Override
    public VillageClientsFragmentContract.Presenter presenter() {
        return null;
    }

    @Override
    protected void initializePresenter() {

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
    protected void onViewClicked(android.view.View view) {

    }

    @Override
    public void showNotFoundPopup(String s) {

    }
}
