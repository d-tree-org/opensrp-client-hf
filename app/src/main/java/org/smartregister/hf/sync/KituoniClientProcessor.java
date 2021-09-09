package org.smartregister.hf.sync;

import android.content.ContentValues;
import android.content.Context;

import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.hf.application.HfApplication;
import org.smartregister.hf.util.Constants;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.family.util.DBConstants;
import org.smartregister.hf.util.CoreConstants;
import org.smartregister.immunization.service.intent.RecurringIntentService;
import org.smartregister.immunization.service.intent.VaccineIntentService;
import org.smartregister.sync.ClientProcessorForJava;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class KituoniClientProcessor extends ClientProcessorForJava {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.getDefault());

    private ClientClassification classification;
    private Table vaccineTable;
    private Table serviceTable;

    private KituoniClientProcessor(Context context) {
        super(context);
    }

    public static ClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new KituoniClientProcessor(context);
        }
        return instance;
    }

    private Table getVaccineTable() {
        if (vaccineTable == null) {
            vaccineTable = assetJsonToJava("ec_client_vaccine.json", Table.class);
        }
        return vaccineTable;
    }

    private Table getServiceTable() {
        if (serviceTable == null) {
            serviceTable = assetJsonToJava("ec_client_service.json", Table.class);
        }
        return serviceTable;
    }

    private ClientClassification getClassification() {
        if (classification == null) {
            classification = assetJsonToJava("ec_client_classification.json", ClientClassification.class);
        }
        return classification;
    }

    @Override
    public synchronized void processClient(List<EventClient> eventClients) throws Exception {
        Timber.d("%s : processClient. Payload size %s", sdf.format(new Date()), eventClients.size());

        ClientClassification clientClassification = getClassification();
        Table vaccineTable = getVaccineTable();
        Table serviceTable = getServiceTable();

        Timber.d("%s : Deserialize assets ", sdf.format(new Date()));
        if (!eventClients.isEmpty()) {
            List<Event> unsyncEvents = new ArrayList<>();
            for (EventClient eventClient : eventClients) {
                Event event = eventClient.getEvent();
                if (event == null) {
                    return;
                }

                Timber.d("%s : Processing %s . BEID %s ", sdf.format(new Date()), event.getEventType(), event.getBaseEntityId());
                String eventType = event.getEventType();
                if (eventType == null) {
                    continue;
                }

                processEvents(clientClassification, vaccineTable, serviceTable, eventClient, event, eventType);

                Timber.d("%s : Processing %s . BEID %s  completed ", sdf.format(new Date()), event.getEventType(), event.getBaseEntityId());
            }

        }
    }

    protected void processEvents(ClientClassification clientClassification, Table vaccineTable, Table serviceTable, EventClient eventClient, Event event, String eventType) throws Exception {
        if (eventType.equals(VaccineIntentService.EVENT_TYPE) || eventType.equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT)) {
            if (vaccineTable == null) {
                return;
            }
            processVaccine(eventClient, vaccineTable, eventType.equals(VaccineIntentService.EVENT_TYPE_OUT_OF_CATCHMENT));
        } else if (eventType.equals(RecurringIntentService.EVENT_TYPE)) {
            if (serviceTable == null) {
                return;
            }
            processService(eventClient, serviceTable);
        } //else if (eventType.equals(HomeVisitRepository.EVENT_TYPE) || eventType.equals(HomeVisitRepository.NOT_DONE_EVENT_TYPE)) {
        //processHomeVisit(eventClient);
        //processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);}
        else if (eventType.equals(Constants.EventType.MINIMUM_DIETARY_DIVERSITY)
                || eventType.equals(Constants.EventType.MUAC) ||
                eventType.equals(Constants.EventType.LLITN) ||
                eventType.equals(Constants.EventType.ECD)) {
            processHomeVisitService(eventClient);
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
        } else if (eventType.equals(Constants.EventType.ANC_HOME_VISIT) && eventClient.getEvent() != null) {
            processAncHomeVisit(eventClient);
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
        } else if (eventType.equals(Constants.EventType.REMOVE_FAMILY) && eventClient.getClient() != null) {
            processRemoveFamily(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate());
        } else if (eventType.equals(Constants.EventType.REMOVE_MEMBER) && eventClient.getClient() != null) {
            processRemoveMember(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate());
        } else if (eventType.equals(Constants.EventType.REMOVE_CHILD) && eventClient.getClient() != null) {
            processRemoveChild(eventClient.getClient().getBaseEntityId(), event.getEventDate().toDate());
        } else if (eventType.equals(Constants.EventType.VACCINE_CARD_RECEIVED) && eventClient.getClient() != null) {
            processVaccineCardEvent(eventClient);
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
        } else if (eventType.equals(CoreConstants.EventType.ADOLESCENT_HF_VISIT) ||
                eventType.equals(CoreConstants.EventType.ANC_HF_VISIT) ||
                eventType.equals(CoreConstants.EventType.PNC_HF_VISIT) ||
                eventType.equals(CoreConstants.EventType.CHILD_HF_VISIT) ||
                eventType.equals(CoreConstants.EventType.FACILITY_VISIT)
                ){
            if (eventClient.getEvent() == null){
                return;
            }
            processVisitEvent(eventClient);
            processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
        }
        else {
            if (eventClient.getClient() != null) {
                if (eventType.equals(Constants.EventType.UPDATE_FAMILY_RELATIONS) && event.getEntityType().equalsIgnoreCase(Constants.TABLE_NAME.FAMILY_MEMBER)) {
                    event.setEventType(Constants.EventType.UPDATE_FAMILY_MEMBER_RELATIONS);
                }
                processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
            }
        }
    }

    private void processVisitEvent(List<EventClient> eventClients, String parentEventName) {
        for (EventClient eventClient : eventClients) {
            processVisitEvent(eventClient, parentEventName); // save locally
        }
    }

    // possible to delegate
    private void processVisitEvent(EventClient eventClient) {
        try {
            NCUtils.processHomeVisit(eventClient); // save locally
        } catch (Exception e) {
            String formID = (eventClient != null && eventClient.getEvent() != null) ? eventClient.getEvent().getFormSubmissionId() : "no form id";
            Timber.e("Form id " + formID + ". " + e.toString());
        }
    }

    private void processVisitEvent(EventClient eventClient, String parentEventName) {
        try {
            NCUtils.processSubHomeVisit(eventClient, parentEventName); // save locally
        } catch (Exception e) {
            String formID = (eventClient != null && eventClient.getEvent() != null) ? eventClient.getEvent().getFormSubmissionId() : "no form id";
            Timber.e("Form id " + formID + ". " + e.toString());
        }
    }

    private void processVaccineCardEvent(EventClient eventClient) {
        List<Obs> observations = eventClient.getEvent().getObs();
//        ChildUtils.addToChildTable(eventClient.getEvent().getBaseEntityId(), observations);
    }

    private void processHomeVisitService(EventClient eventClient) {
//        ChildUtils.addToHomeVisitService(eventClient.getEvent().getEventType(), eventClient.getEvent().getObs(), eventClient.getEvent().getEventDate().toDate(), ChildUtils.gsonConverter.toJson(eventClient.getEvent()));
    }

    // possible to delegate
    private void processAncHomeVisit(EventClient eventClient) {
//        org.smartregister.chw.anc.util.Util.processAncHomeVisit(eventClient); // save locally
    }

    // possible to delegate
    private Boolean processVaccine(EventClient vaccine, Table vaccineTable, boolean outOfCatchment) {
        return true;
    }

    // possible to delegate
    private Boolean processService(EventClient service, Table serviceTable) {
        return true;
    }

    private void processRemoveMember(String baseEntityId, Date eventDate) {

        Date myEventDate = eventDate;
        if (myEventDate == null) {
            myEventDate = new Date();
        }

        if (baseEntityId == null) {
            return;
        }

        AllCommonsRepository commonsRepository = HfApplication.getInstance().getAllCommonsRepository(Constants.TABLE_NAME.FAMILY_MEMBER);
        if (commonsRepository != null) {

            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(myEventDate));
            values.put("is_closed", 1);

            HfApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.FAMILY_MEMBER, values,
                    DBConstants.KEY.BASE_ENTITY_ID + " = ?  ", new String[]{baseEntityId});

            // clean fts table
            HfApplication.getInstance().getRepository().getWritableDatabase().update(CommonFtsObject.searchTableName(Constants.TABLE_NAME.FAMILY_MEMBER), values,
                    " object_id  = ?  ", new String[]{baseEntityId});

            // Utils.context().commonrepository(Constants.TABLE_NAME.FAMILY_MEMBER).populateSearchValues(baseEntityId, DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(eventDate), null);

        }
    }

    private void processRemoveChild(String baseEntityId, Date eventDate) {

        Date myEventDate = eventDate;
        if (myEventDate == null) {
            myEventDate = new Date();
        }

        if (baseEntityId == null) {
            return;
        }

        AllCommonsRepository commonsRepository = HfApplication.getInstance().getAllCommonsRepository(Constants.TABLE_NAME.CHILD);
        if (commonsRepository != null) {

            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(myEventDate));
            values.put("is_closed", 1);

            HfApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.CHILD, values,
                    DBConstants.KEY.BASE_ENTITY_ID + " = ?  ", new String[]{baseEntityId});

            // clean fts table
            HfApplication.getInstance().getRepository().getWritableDatabase().update(CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD), values,
                    CommonFtsObject.idColumn + "  = ?  ", new String[]{baseEntityId});

            // Utils.context().commonrepository(Constants.TABLE_NAME.CHILD).populateSearchValues(baseEntityId, DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(eventDate), null);

        }
    }

    /**
     * Update the family members
     *
     * @param familyID
     */
    private void processRemoveFamily(String familyID, Date eventDate) {

        Date myEventDate = eventDate;
        if (myEventDate == null) {
            myEventDate = new Date();
        }

        if (familyID == null) {
            return;
        }

        AllCommonsRepository commonsRepository = HfApplication.getInstance().getAllCommonsRepository(Constants.TABLE_NAME.FAMILY);
        if (commonsRepository != null) {

            ContentValues values = new ContentValues();
            values.put(DBConstants.KEY.DATE_REMOVED, new SimpleDateFormat("yyyy-MM-dd").format(myEventDate));
            values.put("is_closed", 1);

            HfApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.FAMILY, values,
                    DBConstants.KEY.BASE_ENTITY_ID + " = ?  ", new String[]{familyID});

            HfApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.CHILD, values,
                    DBConstants.KEY.RELATIONAL_ID + " = ?  ", new String[]{familyID});

            HfApplication.getInstance().getRepository().getWritableDatabase().update(Constants.TABLE_NAME.FAMILY_MEMBER, values,
                    DBConstants.KEY.RELATIONAL_ID + " = ?  ", new String[]{familyID});

            // clean fts table
            HfApplication.getInstance().getRepository().getWritableDatabase().update(CommonFtsObject.searchTableName(Constants.TABLE_NAME.FAMILY), values,
                    CommonFtsObject.idColumn + " = ?  ", new String[]{familyID});

            HfApplication.getInstance().getRepository().getWritableDatabase().update(CommonFtsObject.searchTableName(Constants.TABLE_NAME.CHILD), values,
                    String.format(" %s in (select base_entity_id from %s where relational_id = ? )  ", CommonFtsObject.idColumn, Constants.TABLE_NAME.CHILD), new String[]{familyID});

            HfApplication.getInstance().getRepository().getWritableDatabase().update(CommonFtsObject.searchTableName(Constants.TABLE_NAME.FAMILY_MEMBER), values,
                    String.format(" %s in (select base_entity_id from %s where relational_id = ? )  ", CommonFtsObject.idColumn, Constants.TABLE_NAME.FAMILY_MEMBER), new String[]{familyID});

        }
    }

    @Override
    public void updateClientDetailsTable(Event event, Client client) {
        Timber.d("Started updateClientDetailsTable");
        event.addDetails("detailsUpdated", Boolean.TRUE.toString());
        Timber.d("Finished updateClientDetailsTable");
    }

}