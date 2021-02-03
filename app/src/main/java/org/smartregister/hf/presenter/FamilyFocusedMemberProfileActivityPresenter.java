package org.smartregister.hf.presenter;

import android.app.Activity;
import android.widget.Toast;

import org.smartregister.domain.Task;
import org.smartregister.hf.R;
import org.smartregister.hf.contract.FamilyFocusedMemberProfileContract;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileMemberContract;
import org.smartregister.family.interactor.FamilyOtherMemberProfileInteractor;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.hf.dao.FamilyDao;
import org.smartregister.hf.interactor.FamilyFocusedMemberProfileInteractor;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Map;

import static org.smartregister.util.Utils.getName;

public class FamilyFocusedMemberProfileActivityPresenter
        implements FamilyFocusedMemberProfileContract.Presenter,
        FamilyOtherMemberContract.InteractorCallBack,
        FamilyFocusedMemberProfileContract.InteractorCallBack{

    private WeakReference<FamilyFocusedMemberProfileContract.View> viewReference;
    private String baseEntityId;
    private String primaryCaregiver;
    private String familyBaseEntityId;
    protected String familyHead;
    private String familyName;
    protected String villageTown;
    private FamilyFocusedMemberProfileInteractor interactor;

    public FamilyFocusedMemberProfileActivityPresenter(FamilyFocusedMemberProfileContract.View view, FamilyProfileMemberContract.Model model,
                                                       String viewConfigurationIdentifier, String baseEntityId, String familyBaseEntityId,
                                                       String familyHead, String primaryCaregiver, String familyName, String villageTown) {
        this.familyHead = familyHead;
        this.viewReference = new WeakReference<>(view);
        this.baseEntityId = baseEntityId;
        this.familyBaseEntityId = familyBaseEntityId;
        this.familyName = familyName;
        this.primaryCaregiver = primaryCaregiver;
        this.villageTown = villageTown;
        this.interactor = new FamilyFocusedMemberProfileInteractor();

    }

    public String getFamilyName() {
        return familyName;
    }

    @Override
    public void getReferralData(String baseEntityId) {
        interactor.fetchReferralForClient(baseEntityId, this);
    }

    @Override
    public void markReferralAsDone(String entityId) {
        FamilyDao.completeTasksForEntity(entityId);
        getView().goToFamilyProfile();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        viewReference = null;

        interactor.onDestroy(isChangingConfiguration);

        if (!isChangingConfiguration) {
            interactor = null;
        }

    }

    protected FamilyFocusedMemberProfileContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    @Override
    public void fetchProfileData() {
        interactor.refreshProfileView(baseEntityId, this);
    }

    @Override
    public void refreshProfileView() {
        interactor.refreshProfileView(baseEntityId, this);
    }

    @Override
    public void onReferralTaskFetched(Task referral) {
        if (referral != null){
            getView().setReferralDetailsView(true);
            getView().setReferralDetails(
                    referral.getIdentifier(),
                    referral.getFocus(),
                    referral.getDescription(),
                    referral.getAuthoredOn().toString(),
                    referral.getOwner(),
                    referral.getRequester());
        }else {
            getView().setReferralDetailsView(false);
        }
    }

    @Override
    public void onErrorFetchingReferrals(String error) {

    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {

        if (client == null || client.getColumnmaps() == null) {
            return;
        }

        String firstName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        int age = Utils.getAgeFromDate(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, true));

        getView().setProfileName(MessageFormat.format("{0}, {1}", getName(getName(firstName, middleName),lastName), age));

        String gender = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.GENDER, true);
        getView().setProfileDetailOne(gender);

        getView().setProfileDetailTwo(villageTown);

        String uniqueId = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.UNIQUE_ID, false);
        getView().setProfileDetailThree(String.format(getView().getString(org.smartregister.family.R.string.id_with_value), uniqueId));

        String entityType = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.ENTITY_TYPE, false);

        if (baseEntityId.equals(familyHead)) {
            getView().toggleFamilyHead(true);
        } else {
            getView().toggleFamilyHead(false);
        }

        if (baseEntityId.equals(primaryCaregiver)) {
            getView().togglePrimaryCaregiver(true);
        } else {
            getView().togglePrimaryCaregiver(false);
        }

        getView().setProfileImage(client.getCaseId(), entityType);

    }

    @Override
    public void submitVisit(Map<String, String> formForSubmission) {
        if (viewReference.get() != null) {
            viewReference.get().displayProgressBar(true);
            interactor.submitVisit(false, baseEntityId, formForSubmission, this);
        }
    }

    @Override
    public void onSubmitted(boolean successful) {
        viewReference.get().displayProgressBar(false);
        viewReference.get().completeReferralTask();
    }
}
