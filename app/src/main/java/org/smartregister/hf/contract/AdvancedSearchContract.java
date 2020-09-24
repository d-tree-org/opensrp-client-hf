package org.smartregister.hf.contract;

import org.smartregister.hf.domain.Entity;
import org.smartregister.domain.Response;
import org.smartregister.view.contract.BaseRegisterFragmentContract;

import java.util.List;
import java.util.Map;

public interface AdvancedSearchContract  {
    interface Presenter {
        void search(Map<String, String> searchMap,  boolean isLocal);
    }

    interface View extends BaseRegisterFragmentContract.View {
        void showResults(List<Entity> members, boolean isLocal);
    }

    interface Interactor {
        void search(Map<String, String> editMap, boolean isLocal, InteractorCallBack callBack);
    }

    interface InteractorCallBack {
        void onResultsFound(List<Entity> members, boolean isLocal);
    }
}
