package org.smartregister.hf.contract;


import android.content.Context;

import org.smartregister.family.contract.FamilyOtherMemberContract;

public interface FamilyOtherMemberProfileExtendedContract {

    interface Presenter extends FamilyOtherMemberContract.Presenter {

        void updateFamilyMember(String jsonString);

    }

    interface View extends FamilyOtherMemberContract.View {

        void showProgressDialog(int saveMessageStringIdentifier);

        void hideProgressDialog();

        Context getContext();
    }
}
