package org.smartregister.hf.presenter;

import org.smartregister.configurableviews.model.View;
import org.smartregister.family.util.DBConstants;
import org.smartregister.hf.contract.VillageClientsFragmentContract;
import org.smartregister.hf.util.CoreConstants;

import java.lang.ref.WeakReference;
import java.util.Set;

public class VillageClientsFragmentPresenter implements VillageClientsFragmentContract.Presenter {

    private WeakReference<VillageClientsFragmentContract.View> viewReference;
    private VillageClientsFragmentContract.Model model;

    private Set<View> visibleColumns;
    private String viewConfigurationsIdentifier;
    private String selectedVillage;

    public VillageClientsFragmentPresenter(VillageClientsFragmentContract.View viewReference,
                                           VillageClientsFragmentContract.Model model, String viewConfigurationsIdentifier){
        this.viewReference = new WeakReference<>(viewReference);
        this.model = model;
        this.viewConfigurationsIdentifier = viewConfigurationsIdentifier;
    }

    @Override
    public void processViewConfigurations() {

    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = getMainTable();
        //mainCondition = trim(getMainCondition()).equals("") ? mainCondition : getMainCondition();
        String countSelect = this.model.countSelect(tableName, mainCondition);
        String mainSelect = this.model.mainSelect(tableName, mainCondition);

        if (getView() != null) {
            this.getView().initializeQueryParams(tableName, countSelect, mainSelect);
            this.getView().initializeAdapter(this.visibleColumns);
            this.getView().countExecute();
            this.getView().filterandSortInInitializeQueries();
            this.getView().setTotalPatients();
        }
    }

    @Override
    public String getCountSelect() {
        return this.model.countSelect(getMainTable(), getMainCondition());
    }

    @Override
    public String getMainCondition() {
        return String.format("%s.%s is null AND %s.%s like '%%%s%%' ",
                CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.DOD,
                CoreConstants.TABLE_NAME.FAMILY, DBConstants.KEY.VILLAGE_TOWN, selectedVillage
        );
    }

    @Override
    public String getMainTable() {
        return CoreConstants.TABLE_NAME.FAMILY_MEMBER;
    }

    @Override
    public void setSelectedVillage(String selectedVillage) {
        this.selectedVillage = selectedVillage;
    }

    private VillageClientsFragmentContract.View getView(){
        return this.viewReference != null ? (VillageClientsFragmentContract.View) this.viewReference.get() : null;
    }

    @Override
    public void startSync() {

    }

    @Override
    public void searchGlobally(String s) {

    }
}
