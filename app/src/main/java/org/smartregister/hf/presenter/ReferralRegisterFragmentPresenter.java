package org.smartregister.hf.presenter;

import static org.apache.commons.lang3.StringUtils.trim;

import org.smartregister.chw.referral.contract.BaseReferralRegisterFragmentContract;
import org.smartregister.chw.referral.presenter.BaseReferralRegisterFragmentPresenter;
import org.smartregister.chw.referral.util.DBConstants;
import org.smartregister.domain.Task;
import org.smartregister.hf.R;
import org.smartregister.hf.util.Constants;

public class ReferralRegisterFragmentPresenter extends BaseReferralRegisterFragmentPresenter {

    public ReferralRegisterFragmentPresenter(BaseReferralRegisterFragmentContract.View view, BaseReferralRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainCondition() {
        return " " + Constants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.KEY.DATE_REMOVED + " is null " +
                "AND status  in ('" + Task.TaskStatus.READY + "', '" + Task.TaskStatus.IN_PROGRESS + "')";
    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public String getMainTable() {
        return "task";
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = getMainTable();
        String condition = trim(getMainCondition()).equals("") ? mainCondition : getMainCondition();
        String countSelect = model.countSelect(tableName, condition);
        String mainSelect = model.mainSelect(tableName, condition);

        if (getView() != null) {

            getView().initializeQueryParams(tableName, countSelect, mainSelect);
            getView().initializeAdapter(visibleColumns);

            getView().countExecute();
            getView().filterandSortInInitializeQueries();
        }
    }

    public String getDefaultSortQuery() {
        return "";
    }

    public String getDueFilterCondition() {
        return "";
    }
}
