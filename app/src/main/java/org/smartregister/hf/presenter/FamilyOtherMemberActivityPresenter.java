package org.smartregister.hf.presenter;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.hf.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.hf.contract.FamilyProfileExtendedContract;
import org.smartregister.hf.interactor.FamilyProfileInteractor;
import org.smartregister.hf.model.FamilyProfileModel;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.contract.FamilyOtherMemberContract;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.presenter.BaseFamilyOtherMemberProfileActivityPresenter;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;

import java.lang.ref.WeakReference;
import java.text.MessageFormat;

import timber.log.Timber;

import static org.smartregister.util.Utils.getName;

public class FamilyOtherMemberActivityPresenter extends BaseFamilyOtherMemberProfileActivityPresenter
        implements FamilyOtherMemberProfileExtendedContract.Presenter,
        FamilyProfileContract.InteractorCallBack,
        FamilyProfileExtendedContract.PresenterCallBack {

    private static final String TAG = FamilyOtherMemberActivityPresenter.class.getCanonicalName();

    private WeakReference<FamilyOtherMemberProfileExtendedContract.View> viewReference;
    private String familyBaseEntityId;
    private String familyName;

    private FamilyProfileContract.Interactor profileInteractor;
    private FamilyProfileContract.Model profileModel;

    public FamilyOtherMemberActivityPresenter(FamilyOtherMemberProfileExtendedContract.View view, FamilyOtherMemberContract.Model model,
                                              String viewConfigurationIdentifier, String familyBaseEntityId, String baseEntityId,
                                              String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        super(view, model, viewConfigurationIdentifier, baseEntityId, familyHead, primaryCaregiver, villageTown);
        viewReference = new WeakReference<>(view);
        this.familyBaseEntityId = familyBaseEntityId;
        this.familyName = familyName;

        this.profileInteractor = new FamilyProfileInteractor();
        this.profileModel = new FamilyProfileModel(familyName);

        verifyHasPhone();
        //initializeServiceStatus();
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void updateFamilyMember(String jsonString) {

        try {
            getView().showProgressDialog(org.smartregister.family.R.string.saving_dialog_title);

            FamilyEventClient familyEventClient = profileModel.processUpdateMemberRegistration(jsonString, familyBaseEntityId);
            if (familyEventClient == null) {
                return;
            }
            profileInteractor.saveRegistration(familyEventClient, jsonString, true, this);
        } catch (Exception e) {
            getView().hideProgressDialog();
            Timber.e(e);
        }
    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient client) {
        super.refreshProfileTopSection(client);
        if (client != null && client.getColumnmaps() != null) {
            String firstName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
            String middleName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
            String lastName = Utils.getValue(client.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
            int age = Utils.getAgeFromDate(Utils.getValue(client.getColumnmaps(), DBConstants.KEY.DOB, true));

            this.getView().setProfileName(MessageFormat.format("{0}, {1}", getName(getName(firstName, middleName), lastName), age));
        }
    }

    public void startFormForEdit(CommonPersonObjectClient commonPersonObject) {
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        //TODO Implement
        Timber.d("onUniqueIdFetched unimplemented");
    }

    @Override
    public void onNoUniqueId() {
        //TODO Implement
        Timber.d("onNoUniqueId unimplemented");
    }

    @Override
    public void onRegistrationSaved(boolean b, boolean b1, FamilyEventClient familyEventClient) {

    }

    @Override
    public void verifyHasPhone() {
        ((FamilyProfileInteractor) profileInteractor).verifyHasPhone(familyBaseEntityId, this);
    }

    @Override
    public void notifyHasPhone(boolean hasPhone) {

    }

    public FamilyOtherMemberProfileExtendedContract.View getView() {
        if (viewReference != null) {
            return viewReference.get();
        } else {
            return null;
        }
    }

}