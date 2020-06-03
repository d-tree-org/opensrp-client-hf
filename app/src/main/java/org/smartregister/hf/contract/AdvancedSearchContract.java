package org.smartregister.hf.contract;

import org.smartregister.hf.domain.Entity;
import org.smartregister.domain.Response;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Map;

public interface AdvancedSearchContract  {
    interface Presenter {
        void search(String searchText);
    }

    interface View extends BaseRegisterFragmentContract.View {
        void showResults(List<Entity> members);
    }

    interface Interactor {
        void search(String searchText, InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void onResultsFound(List<Entity> members);
    }
}
