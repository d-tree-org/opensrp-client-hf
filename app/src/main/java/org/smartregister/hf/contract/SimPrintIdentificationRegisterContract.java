package org.smartregister.hf.contract;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.family.contract.FamilyRegisterContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

public interface SimPrintIdentificationRegisterContract {


    interface View extends BaseRegisterContract.View {
        void setNumberClientsFound();
        void setNumberClientsNotFound();
        SimPrintIdentificationRegisterContract.Presenter presenter();
    }


    interface Presenter extends BaseRegisterContract.Presenter {

        void saveLanguage(String language);

        void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void saveForm(String jsonString, boolean isEditMode);

        void closeFamilyRecord(String jsonString);

    }


    interface Model {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        String getLocationId(String locationName);

        List<FamilyEventClient> processRegistration(String jsonString);

        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId) throws Exception;

        String getInitials();

    }


    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void getNextUniqueId(Triple<String, String, String> triple, FamilyRegisterContract.InteractorCallBack callBack);

        void saveRegistration(final List<FamilyEventClient> familyEventClientList, final String jsonString, final boolean isEditMode, final FamilyRegisterContract.InteractorCallBack callBack);

        void removeFamilyFromRegister(String closeFormJsonString, String providerId);

    }

    interface InteractorCallBack {

        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onNoUniqueId();

        void onRegistrationSaved(boolean isEdit);

    }


}
