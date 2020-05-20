package org.smartregister.hf.interactor;

import org.smartregister.domain.Task;
import org.smartregister.family.interactor.FamilyOtherMemberProfileInteractor;
import org.smartregister.hf.contract.FamilyFocusedMemberProfileContract;
import org.smartregister.hf.util.TaksUtils;

import java.util.Set;

/**
 * Author : Isaya Mollel on 2020-05-18.
 */
public class FamilyFocusedMemberProfileInteractor extends FamilyOtherMemberProfileInteractor
    implements FamilyFocusedMemberProfileContract.Interactor {

    @Override
    public void fetchReferralForClient(String baseEntityId, FamilyFocusedMemberProfileContract.InteractorCallBack callBack) {

        String forId = baseEntityId;

        Set<Task> tasks = TaksUtils.getReferralTask(forId);
        try {
            Task referralTasks = tasks.iterator().next();
            if (referralTasks != null){
                callBack.onReferralTaskFetched(referralTasks);
            }
        }catch (Exception e){
            e.printStackTrace();
            callBack.onErrorFetchingReferrals("Referrals could not be fetched");
        }

    }
}
