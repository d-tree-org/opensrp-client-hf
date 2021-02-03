package org.smartregister.hf.interactor;

import androidx.annotation.VisibleForTesting;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.anc.util.Constants;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.Task;
import org.smartregister.family.interactor.FamilyOtherMemberProfileInteractor;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.hf.contract.FamilyFocusedMemberProfileContract;
import org.smartregister.hf.dao.AdolescentDao;
import org.smartregister.hf.dao.AncDao;
import org.smartregister.hf.dao.PNCDao;
import org.smartregister.hf.util.CoreConstants;
import org.smartregister.hf.util.TaksUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * Author : Isaya Mollel on 2020-05-18.
 */
public class FamilyFocusedMemberProfileInteractor implements FamilyFocusedMemberProfileContract.Interactor {

    private AppExecutors appExecutors;
    private String relationalId;

    @VisibleForTesting
    FamilyFocusedMemberProfileInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public FamilyFocusedMemberProfileInteractor() {
        this(new AppExecutors());
    }

    public void onDestroy(boolean isChangingConfiguration) {
    }

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

    @Override
    public void refreshProfileView(String baseEntityId, FamilyFocusedMemberProfileContract.InteractorCallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyMemberRegister.tableName).findByBaseEntityId(baseEntityId);
                final CommonPersonObjectClient pClient = new CommonPersonObjectClient(personObject.getCaseId(),
                        personObject.getDetails(), "");
                pClient.setColumnmaps(personObject.getColumnmaps());

                if (pClient != null)
                    relationalId = Utils.getValue(pClient.getColumnmaps(), DBConstants.KEY.RELATIONAL_ID, false);

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.refreshProfileTopSection(pClient);
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    public CommonRepository getCommonRepository(String tableName) {
        return Utils.context().commonrepository(tableName);
    }

    @Override
    public void submitVisit(boolean editMode, String memberID, Map<String, String> formForSubmission, FamilyFocusedMemberProfileContract.InteractorCallBack callBack) {
        final Runnable runnable = () -> {
            boolean result = true;
            try {
                submitVisit(editMode, memberID, formForSubmission, "");
            } catch (Exception e) {
                Timber.e(e);
                result = false;
            }

            final boolean finalResult = result;
            appExecutors.mainThread().execute(() -> callBack.onSubmitted(finalResult));
        };

        appExecutors.diskIO().execute(runnable);
    }

    protected void submitVisit(final boolean editMode, final String memberID, final Map<String, String> dsForm, String parentEventType) throws Exception {
        // create a map of the different types
        String payloadType = null;
        String payloadDetails = null;

        if (!dsForm.isEmpty()) {
            String type = StringUtils.isBlank(parentEventType) ? getEncounterType(memberID) : getEncounterType(memberID);

            // persist to database
            Visit visit = saveVisit(editMode, memberID, type, dsForm, parentEventType);
            if (visit != null) {
                saveVisitDetails(visit, payloadType, payloadDetails);
            }
        }
    }

    protected @Nullable Visit saveVisit(boolean editMode, String memberID, String encounterType,
                                        final Map<String, String> jsonString,
                                        String parentEventType
    ) throws Exception {

        AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().context().allSharedPreferences();

        String derivedEncounterType = StringUtils.isBlank(parentEventType) ? encounterType : "";
        Event baseEvent = JsonFormUtils.processVisitJsonForm(allSharedPreferences, memberID, derivedEncounterType, jsonString, getTableName(memberID));

        // only tag the first event with the date
        if (StringUtils.isBlank(parentEventType)) {
            prepareEvent(baseEvent);
        }

        if (baseEvent != null) {
            baseEvent.setFormSubmissionId(JsonFormUtils.generateRandomUUIDString());
            JsonFormUtils.tagEvent(allSharedPreferences, baseEvent);
            baseEvent.setLocationId(getClientLocationId(relationalId));

            String visitID = (editMode) ?
                    visitRepository().getLatestVisit(memberID, getEncounterType(memberID)).getVisitId() :
                    JsonFormUtils.generateRandomUUIDString();

            Visit visit = NCUtils.eventToVisit(baseEvent, visitID);
            visit.setPreProcessedJson(new Gson().toJson(baseEvent));
            visit.setParentVisitID(getParentVisitEventID(visit, parentEventType));

            visitRepository().addVisit(visit);
            return visit;
        }
        return null;
    }

    protected void saveVisitDetails(Visit visit, String payloadType, String payloadDetails) {
        if (visit.getVisitDetails() == null) return;

        for (Map.Entry<String, List<VisitDetail>> entry : visit.getVisitDetails().entrySet()) {
            if (entry.getValue() != null) {
                for (VisitDetail d : entry.getValue()) {
                    d.setPreProcessedJson(payloadDetails);
                    d.setPreProcessedType(payloadType);
                    AncLibrary.getInstance().visitDetailsRepository().addVisitDetails(d);
                }
            }
        }
    }

    protected String getParentVisitEventID(Visit visit, String parentEventType) {
        return visitRepository().getParentVisitEventID(visit.getBaseEntityId(), parentEventType, visit.getDate());
    }

    protected void prepareEvent(Event baseEvent) {
        if (baseEvent != null) {
            // add anc date obs and last
            List<Object> list = new ArrayList<>();
            list.add(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
            baseEvent.addObs(new Obs("concept", "text", "facility_visit_date", "",
                    list, new ArrayList<>(), null, "facility_visit_date"));
        }
    }

    public VisitRepository visitRepository() {
        return AncLibrary.getInstance().visitRepository();
    }

    protected String getEncounterType(String baseEntityId) {
        if (AncDao.isANCMember(baseEntityId)) {
            return CoreConstants.EventType.ANC_HF_VISIT;
        } else if (PNCDao.isPNCMember(baseEntityId)) {
            return CoreConstants.EventType.PNC_HF_VISIT;
        } else if (AdolescentDao.isAdolescentMember(baseEntityId)) {
            return CoreConstants.EventType.ADOLESCENT_HF_VISIT;
        } else {
            return CoreConstants.EventType.CHILD_HF_VISIT;
        }
    }

    protected String getTableName(String baseEntityId) {
        return Constants.TABLES.FAMILY_MEMBER;
    }

    private String getClientLocationId(String baseEntityId) {
        final CommonPersonObject familyObject = getCommonRepository(Utils.metadata().familyRegister.tableName).findByBaseEntityId(baseEntityId);
        final CommonPersonObjectClient pClient = new CommonPersonObjectClient(familyObject.getCaseId(),
                familyObject.getDetails(), "");
        pClient.setColumnmaps(familyObject.getColumnmaps());

        String villageTown = Utils.getValue(pClient.getColumnmaps(), DBConstants.KEY.VILLAGE_TOWN, false);

        return LocationHelper.getInstance().getOpenMrsLocationId(villageTown);
    }

}
