package org.smartregister.hf.contract;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Task;
import org.smartregister.view.contract.BaseProfileContract;

public interface FamilyFocusedMemberProfileContract {

    interface View extends BaseProfileContract.View {


        FamilyFocusedMemberProfileContract.Presenter presenter();

        void setProfileImage(String baseEntityId, String entityType);

        void setProfileName(String fullName);

        void setProfileDetailOne(String detailOne);

        void setProfileDetailTwo(String detailTwo);

        void setProfileDetailThree(String detailThree);

        void toggleFamilyHead(boolean show);

        void togglePrimaryCaregiver(boolean show);

        String getString(int id_with_value);

        void setReferralDetails(String focus, String indicators, String date, String source, String referredBy);

        void setReferralDetailsView(boolean hasReferral);
    }

    interface Presenter extends BaseProfileContract.Presenter {

        void fetchProfileData();

        void refreshProfileView();

        String getFamilyName();

        void getReferralData(String baseEntityId);

    }

    interface Interactor {
        void fetchReferralForClient(String baseEntityId, InteractorCallBack callBack);
    }

    interface InteractorCallBack {

        //void refreshProfileTopSection(CommonPersonObjectClient client);

        void onReferralTaskFetched(Task referral);

        void onErrorFetchingReferrals(String error);

    }
}
