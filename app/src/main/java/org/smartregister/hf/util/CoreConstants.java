package org.smartregister.hf.util;


import android.content.res.AssetManager;

import org.json.JSONObject;

import java.util.Locale;

import static org.smartregister.hf.util.CoreConstants.EventType.MALARIA_FOLLOW_UP_VISIT;
import static org.smartregister.hf.util.CoreJsonFormUtils.ENCOUNTER_TYPE;
//import static org.smartregister.chw.malaria.util.Constants.EVENT_TYPE.MALARIA_FOLLOW_UP_VISIT;

public class CoreConstants {

    public static final String ENTITY_ID = "entity_id";
    public static final String REFERRAL_PLAN_ID = "5270285b-5a3b-4647-b772-c0b3c52e2b71";
    public static String EC_CLIENT_FIELDS = "ec_client_fields.json";
    public static String IGNORE = "ignore";

    public enum VisitType {DUE, OVERDUE, LESS_TWENTY_FOUR, VISIT_THIS_MONTH, NOT_VISIT_THIS_MONTH, DONE, EXPIRY}

    public enum ServiceType {DUE, OVERDUE, UPCOMING}

    public enum FamilyServiceType {DUE, OVERDUE, NOTHING}


    public interface DB_CONSTANTS {
        String ID = "_id";
        String FOR = "for";
        String FOCUS = "focus";
        String REQUESTER = "requester";
        String OWNER = "owner";
        String START = "start";
        String ENTRY_POINT = "entry_point";
        String STATUS = "status";
    }

    public interface SERVICE_GROUPS {
        String CHILD = "child";
        String WOMAN = "woman";
        String PNC = "pnc";
        String ANC = "anc";
    }

    public interface SCHEDULE_TYPES {
        String CHILD_VISIT = "CHILD_VISIT";
        String ANC_VISIT = "ANC_VISIT";
        String PNC_VISIT = "PNC_VISIT";
        String WASH_CHECK = "WASH_CHECK";
        String MALARIA_VISIT = "MALARIA_VISIT";
        String FP_VISIT = "FP_VISIT";
    }

    public interface SCHEDULE_GROUPS {
        String HOME_VISIT = "HOME_VISIT";
        String FAMILY = "FAMILY";
    }

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String FAMILY_REGISTER = "family_register";
        public static final String FAMILY_MEMBER_REGISTER = "family_member_register";
        public static final String ANC_REGISTER = "anc_register";
        public static final String MALARIA_REGISTER = "anc_malaria_confirmation";
    }

    public static final class EventType {
        public static final String FAMILY_MEMBER_REGISTRATION = "Family Member Registration";
        public static final String UPDATE_FAMILY_MEMBER_RELATIONS = "Update Family Member Relations";

        public static final String UPDATE_FAMILY_RELATIONS = "Update Family Relations";

        public static final String UPDATE_FAMILY_MEMBER_REGISTRATION = "Update Family Member Registration";

        public static final String REMOVE_MEMBER = "Remove Family Member";
        public static final String REMOVE_CHILD = "Remove Child Under 5";
        public static final String REMOVE_FAMILY = "Remove Family";

        public static final String ANC_REGISTRATION = "ANC Registration";
        public static final String MALARIA_FOLLOW_UP_VISIT = "Malaria Follow-up Visit";
        public static final String UPDATE_ANC_REGISTRATION = "Update ANC Registration";

        public static final String FACILITY_VISIT = "Facility Visit";

        public static final String ANC_HF_VISIT = "ANC Health Facility Visit";
        public static final String PNC_HF_VISIT = "PNC Health Facility Visit";
        public static final String ADOLESCENT_HF_VISIT = "Adolescent Health Facility Visit";
        public static final String CHILD_HF_VISIT = "Child Health Facility Visit";

    }

    public static final class EncounterType {
        public static final String CLOSE_REFERRAL = "Close Referral";
    }

    /**
     * Only access form constants via the getter
     */
    public static class JSON_FORM {
        public static final String BIRTH_CERTIFICATION = "birth_certification";

        public static final String OBS_ILLNESS = "observation_illness";
        public static final String FAMILY_REGISTER = "family_register";
        public static final String FAMILY_MEMBER_REGISTER = "family_member_register";
        public static final String CHILD_REGISTER = "child_enrollment";
        public static final String FAMILY_DETAILS_REGISTER = "family_details_register";
        public static final String FAMILY_DETAILS_REMOVE_MEMBER = "family_details_remove_member";

        public static final String FAMILY_DETAILS_REMOVE_CHILD = "family_details_remove_child";
        public static final String FAMILY_DETAILS_REMOVE_FAMILY = "family_details_remove_family";
        public static final String HOME_VISIT_COUNSELLING = "routine_home_visit";
        public static final String ADDO_RECORD_SERVICE_OTHER = "addo_record_service_provided_other";

        private static final String ANC_REGISTRATION = "anc_member_registration";
        private static final String PREGNANCY_OUTCOME = "anc_pregnancy_outcome";
        private static final String MALARIA_CONFIRMATION = "malaria_confirmation";
        private static final String MALARIA_FOLLOW_UP_VISIT_FORM = "malaria_follow_up_visit";
        private static final String WASH_CHECK = "wash_check";
        private static final String CHILD_REFERRAL_FORM = "child_referral_form";
        private static final String ANC_REFERRAL_FORM = "anc_referral_form";
        private static final String PNC_REFERRAL_FORM = "pnc_referral_form";
        private static final String FAMILY_PLANNING_REGISTRATION_FORM = "family_planning_registration";
        private static final String FAMILY_PLANNING_CHANGE_METHOD_FORM = "family_planning_change_method";
        public static AssetManager assetManager;
        public static Locale locale;

        //Kituoni Specific Form
        public static final String COMPLETE_REFERRAL = "complete_referral_form";

        public static final String FACILITY_VISIT = "facility_visit_form";

        /**
         * NOTE: This method must be called first before using any of the forms. Preferable onCreate()
         * method of Application
         *
         * @param locale       locale used to determine language
         * @param assetManager asset manager used to access Json Forms
         */
        public static void setLocaleAndAssetManager(Locale locale, AssetManager assetManager) {
            JSON_FORM.assetManager = assetManager;
            JSON_FORM.locale = locale;
        }

        public static String getKituoniCompleteReferral(){return COMPLETE_REFERRAL;}

        public static String getFAcilityVisitForm(){return  FACILITY_VISIT;}

        public static String getAddoRecordServiceOther() { return ADDO_RECORD_SERVICE_OTHER; }

        public static String getChildReferralForm() {
            return CHILD_REFERRAL_FORM;
        }

        public static String getAncReferralForm() {
            return ANC_REFERRAL_FORM;
        }

        public static String getPncReferralForm() {
            return PNC_REFERRAL_FORM;
        }

        public static String getBirthCertification() {
            return Utils.getLocalForm(BIRTH_CERTIFICATION, locale, assetManager);
        }

        public static String getObsIllness() {
            return Utils.getLocalForm(OBS_ILLNESS, locale, assetManager);
        }

        public static String getFamilyRegister() {
            return Utils.getLocalForm(FAMILY_REGISTER, locale, assetManager);
        }

        public static String getFamilyMemberRegister() {
            return Utils.getLocalForm(FAMILY_MEMBER_REGISTER, locale, assetManager);
        }

        public static String getChildRegister() {
            return Utils.getLocalForm(CHILD_REGISTER, locale, assetManager);
        }

        public static String getFamilyDetailsRegister() {
            return Utils.getLocalForm(FAMILY_DETAILS_REGISTER, locale, assetManager);
        }

        public static String getFamilyDetailsRemoveMember() {
            return Utils.getLocalForm(FAMILY_DETAILS_REMOVE_MEMBER, locale, assetManager);
        }

        public static String getFamilyDetailsRemoveChild() {
            return Utils.getLocalForm(FAMILY_DETAILS_REMOVE_CHILD, locale, assetManager);
        }

        public static String getFamilyDetailsRemoveFamily() {
            return Utils.getLocalForm(FAMILY_DETAILS_REMOVE_FAMILY, locale, assetManager);
        }

        public static String getHomeVisitCounselling() {
            return Utils.getLocalForm(HOME_VISIT_COUNSELLING, locale, assetManager);
        }

        public static String getAncRegistration() {
            return Utils.getLocalForm(ANC_REGISTRATION, locale, assetManager);
        }

        public static String getPregnancyOutcome() {
            return Utils.getLocalForm(PREGNANCY_OUTCOME, locale, assetManager);
        }

        public static String getMalariaConfirmation() {
            return Utils.getLocalForm(MALARIA_CONFIRMATION, locale, assetManager);
        }

        public static String getFpRegistrationForm() {
            return Utils.getLocalForm(FAMILY_PLANNING_REGISTRATION_FORM, locale, assetManager);
        }

        public static String getFpChengeMethodForm() {
            return Utils.getLocalForm(FAMILY_PLANNING_CHANGE_METHOD_FORM, locale, assetManager);
        }

        public static boolean isMultiPartForm(JSONObject jsonForm) {
            String encounterType = jsonForm.optString(ENCOUNTER_TYPE);
            return !encounterType.equals(MALARIA_FOLLOW_UP_VISIT);
        }

        public static String getMalariaFollowUpVisitForm() {
            return Utils.getLocalForm(MALARIA_FOLLOW_UP_VISIT_FORM, locale, assetManager);
        }

        public static String getWashCheck() {
            return Utils.getLocalForm(WASH_CHECK, locale, assetManager);
        }

        public static class CHILD_HOME_VISIT {
            private static final String VACCINE_CARD = "child_hv_vaccine_card_received";
            private static final String VITAMIN_A = "child_hv_vitamin_a";
            private static final String DEWORMING = "child_hv_deworming";
            private static final String MUAC = "child_hv_muac";
            private static final String DIETARY = "child_hv_dietary_diversity";
            private static final String MNP = "child_hv_mnp";
            private static final String MALARIA_PREVENTION = "child_hv_malaria_prevention";
            private static final String SLEEPING_UNDER_LLITN = "child_hv_sleeping_under_llitn";
            private static final String NUTRITION_STATUS = "child_hv_nutrition_status";

            public static String getVaccineCard() {
                return Utils.getLocalForm(VACCINE_CARD, locale, assetManager);
            }

            public static String getVitaminA() {
                return Utils.getLocalForm(VITAMIN_A, locale, assetManager);
            }

            public static String getDEWORMING() {
                return Utils.getLocalForm(DEWORMING, locale, assetManager);
            }

            public static String getMUAC() {
                return Utils.getLocalForm(MUAC, locale, assetManager);
            }

            public static String getDIETARY() {
                return Utils.getLocalForm(DIETARY, locale, assetManager);
            }

            public static String getMNP() {
                return Utils.getLocalForm(MNP, locale, assetManager);
            }

            public static String getMalariaPrevention() {
                return Utils.getLocalForm(MALARIA_PREVENTION, locale, assetManager);
            }

            public static String getSleepingUnderLlitn() {
                return Utils.getLocalForm(SLEEPING_UNDER_LLITN, locale, assetManager);
            }

            public static String getNutritionStatus() {
                return Utils.getLocalForm(NUTRITION_STATUS, locale, assetManager);
            }
        }

        public static class ANC_HOME_VISIT {
            private static final String DANGER_SIGNS = "anc_hv_danger_signs";
            private static final String ANC_COUNSELING = "anc_hv_counseling";
            private static final String SLEEPING_UNDER_LLITN = "anc_hv_sleeping_under_llitn";
            private static final String ANC_CARD_RECEIVED = "anc_hv_anc_card_received";
            private static final String TT_IMMUNIZATION = "anc_hv_tt_immunization";
            private static final String IPTP_SP = "anc_hv_anc_iptp_sp";

            private static final String HEALTH_FACILITY_VISIT = "anc_hv_health_facility_visit";
            private static final String FAMILY_PLANNING = "anc_hv_family_planning";
            private static final String NUTRITION_STATUS = "anc_hv_nutrition_status";
            private static final String COUNSELLING = "anc_hv_counselling";
            private static final String MALARIA = "anc_hv_malaria";
            private static final String OBSERVATION_AND_ILLNESS = "anc_hv_observations";
            private static final String REMARKS_AND_COMMENTS = "anc_hv_remarks_and_comments";
            private static final String EARLY_CHILDHOOD_DEVELOPMENT = "early_childhood_development";

            public static String getDangerSigns() {
                return Utils.getLocalForm(DANGER_SIGNS, locale, assetManager);
            }

            public static String getAncCounseling() {
                return Utils.getLocalForm(ANC_COUNSELING, locale, assetManager);
            }

            public static String getSleepingUnderLlitn() {
                return Utils.getLocalForm(SLEEPING_UNDER_LLITN, locale, assetManager);
            }

            public static String getAncCardReceived() {
                return Utils.getLocalForm(ANC_CARD_RECEIVED, locale, assetManager);
            }

            public static String getTtImmunization() {
                return Utils.getLocalForm(TT_IMMUNIZATION, locale, assetManager);
            }

            public static String getIptpSp() {
                return Utils.getLocalForm(IPTP_SP, locale, assetManager);
            }

            public static String getHealthFacilityVisit() {
                return Utils.getLocalForm(HEALTH_FACILITY_VISIT, locale, assetManager);
            }

            public static String getFamilyPlanning() {
                return Utils.getLocalForm(FAMILY_PLANNING, locale, assetManager);
            }

            public static String getNutritionStatus() {
                return Utils.getLocalForm(NUTRITION_STATUS, locale, assetManager);
            }

            public static String getCOUNSELLING() {
                return Utils.getLocalForm(COUNSELLING, locale, assetManager);
            }

            public static String getMALARIA() {
                return Utils.getLocalForm(MALARIA, locale, assetManager);
            }

            public static String getObservationAndIllness() {
                return Utils.getLocalForm(OBSERVATION_AND_ILLNESS, locale, assetManager);
            }

            public static String getRemarksAndComments() {
                return Utils.getLocalForm(REMARKS_AND_COMMENTS, locale, assetManager);
            }

            public static String getEarlyChildhoodDevelopment() {
                return Utils.getLocalForm(EARLY_CHILDHOOD_DEVELOPMENT, locale, assetManager);
            }
        }

        public static class PNC_HOME_VISIT {
            private static final String DANGER_SIGNS = "pnc_hv_danger_signs";
            private static final String DANGER_SIGNS_MOTHER = "pnc_danger_signs_mother";
            private static final String DANGER_SIGNS_BABY = "pnc_danger_signs_baby";
            private static final String HEALTH_FACILITY_VISIT = "pnc_health_facility_visit";
            private static final String HEALTH_FACILITY_VISIT_TWO = "pnc_health_facility_visit_two";
            private static final String COUNSELLING = "pnc_counselling";
            private static final String UMBILICAL_CORD = "pnc_umbilical_cord";
            private static final String NUTRITION_STATUS_MOTHER = "pnc_nutrition_status_mother";
            private static final String NUTRITION_STATUS_INFANT = "pnc_nutrition_status_infant";
            private static final String MALARIA_PREVENTION = "pnc_malaria_prevention";
            private static final String FAMILY_PLANNING = "pnc_family_planning";
            private static final String KANGAROO_CARE = "pnc_kangaroo_care";
            private static final String VACCINE_CARD = "pnc_vaccine_card";
            private static final String EXCLUSIVE_BREAST_FEEDING = "pnc_exclusive_breastfeeding";
            private static final String OBSERVATION_AND_ILLNESS_MOTHER = "pnc_hv_observations_mother";
            private static final String OBSERVATION_AND_ILLNESS_INFANT = "pnc_hv_observations_infant";

            public static String getDangerSigns() {
                return Utils.getLocalForm(DANGER_SIGNS, locale, assetManager);
            }

            public static String getDangerSignsMother() {
                return Utils.getLocalForm(DANGER_SIGNS_MOTHER, locale, assetManager);
            }

            public static String getDangerSignsBaby() {
                return Utils.getLocalForm(DANGER_SIGNS_BABY, locale, assetManager);
            }

            public static String getHealthFacilityVisit() {
                return Utils.getLocalForm(HEALTH_FACILITY_VISIT, locale, assetManager);
            }

            public static String getHealthFacilityVisitTwo() {
                return Utils.getLocalForm(HEALTH_FACILITY_VISIT_TWO, locale, assetManager);
            }

            public static String getCOUNSELLING() {
                return Utils.getLocalForm(COUNSELLING, locale, assetManager);
            }

            public static String getUmbilicalCord() {
                return Utils.getLocalForm(UMBILICAL_CORD, locale, assetManager);
            }

            public static String getNutritionStatusMother() {
                return Utils.getLocalForm(NUTRITION_STATUS_MOTHER, locale, assetManager);
            }

            public static String getNutritionStatusInfant() {
                return Utils.getLocalForm(NUTRITION_STATUS_INFANT, locale, assetManager);
            }

            public static String getMalariaPrevention() {
                return Utils.getLocalForm(MALARIA_PREVENTION, locale, assetManager);
            }

            public static String getFamilyPlanning() {
                return Utils.getLocalForm(FAMILY_PLANNING, locale, assetManager);
            }

            public static String getKangarooCare() {
                return Utils.getLocalForm(KANGAROO_CARE, locale, assetManager);
            }

            public static String getVaccineCard() {
                return Utils.getLocalForm(VACCINE_CARD, locale, assetManager);
            }

            public static String getExclusiveBreastFeeding() {
                return Utils.getLocalForm(EXCLUSIVE_BREAST_FEEDING, locale, assetManager);
            }

            public static String getObservationAndIllnessMother() {
                return Utils.getLocalForm(OBSERVATION_AND_ILLNESS_MOTHER, locale, assetManager);
            }

            public static String getObservationAndIllnessInfant() {
                return Utils.getLocalForm(OBSERVATION_AND_ILLNESS_INFANT, locale, assetManager);
            }
        }

        public static class FamilyPlanningFollowUpVisitUtils {
            private static final String FAMILY_PLANNING_FOLLOWUP_COUNSEL = "fp_followup_counsel";
            private static final String FAMILY_PLANNING_FOLLOWUP_RESUPPLY = "fp_followup_resupply";
            private static final String FAMILY_PLANNING_FOLLOWUP_SIDE_EFFECTS = "fp_followup_side_effects";

            public static String getFamilyPlanningFollowupCounsel() {
                return Utils.getLocalForm(FAMILY_PLANNING_FOLLOWUP_COUNSEL, locale, assetManager);
            }
            public static String getFamilyPlanningFollowupResupply() {
                return Utils.getLocalForm(FAMILY_PLANNING_FOLLOWUP_RESUPPLY, locale, assetManager);
            }
            public static String getFamilyPlanningFollowupSideEffects() {
                return Utils.getLocalForm(FAMILY_PLANNING_FOLLOWUP_SIDE_EFFECTS, locale, assetManager);
            }
        }
    }

    public static class RELATIONSHIP {
        public static final String FAMILY = "family";
        public static final String FAMILY_HEAD = "family_head";
        public static final String PRIMARY_CAREGIVER = "primary_caregiver";
    }

    public static class TABLE_NAME {
        public static final String FAMILY = "ec_family";
        public static final String FAMILY_MEMBER = "ec_family_member";
        public static final String CHILD = "ec_child";
        public static final String CHILD_ACTIVITY = "ec_child_activity";
        public static final String ANC_MEMBER = "ec_anc_register";
        public static final String PNC_MEMBER = "ec_pregnancy_outcome";
        public static final String ANC_MEMBER_LOG = "ec_anc_log";
        public static final String MALARIA_CONFIRMATION = "ec_malaria_confirmation";
        public static final String ANC_PREGNANCY_OUTCOME = "ec_pregnancy_outcome";
        public static final String TASK = "task";
        public static final String WASH_CHECK_LOG = "ec_wash_check_log";
        public static final String CHILD_REFERRAL = "ec_child_referral";
        public static final String ANC_REFERRAL = "ec_anc_referral";
        public static final String PNC_REFERRAL = "ec_pnc_referral";
        public static final String CLOSE_REFERRAL = "ec_close_referral";
        public static final String SCHEDULE_SERVICE = "schedule_service";
        public static final String ADOLESCENT = "ec_adolescent";
    }

    public static final class INTENT_KEY {
        public static final String SERVICE_DUE = "service_due";
        public static final String CHILD_BASE_ID = "child_base_id";
        public static final String CHILD_NAME = "child_name";
        public static final String CHILD_DATE_OF_BIRTH = "child_dob";
        public static final String CHILD_LAST_VISIT_DAYS = "child_visit_days";
        public static final String CHILD_VACCINE_LIST = "child_vaccine_list";
        public static final String GROWTH_TITLE = "growth_title";
        public static final String GROWTH_QUESTION = "growth_ques";
        public static final String GROWTH_IMMUNIZATION_TYPE = "growth_type";
        public static final String CHILD_COMMON_PERSON = "child_common_peron";
        public static final String IS_COMES_FROM_FAMILY = "is_comes_from";
        public static final String USERS_TASKS = "tasks";
        public static final String CLASS = "class";
        public static final String VIEW_REGISTER_CLASS = "view_register_class";
        public static final String STARTING_ACTIVITY = "starting_activity";
        public static final String CLIENT = "client";
        public static final String MEMBER_OBJECT = "member_object";
        public static final String FAMILY_HEAD_NAME = "family_head_name";
        public static final String FAMILY_HEAD_PHONE_NUMBER = "family_head_phone_number";
    }

    public static final class IMMUNIZATION_CONSTANT {
        public static final String DATE = "date";
        public static final String VACCINE = "vaccine";
    }

    public static final class DrawerMenu {
        public static final String ALL_CLIENTS = "All Clients";
        public static final String ALL_FAMILIES = "All Families";
        public static final String ANC_CLIENTS = "ANC Clients";
        public static final String CHILD_CLIENTS = "Child Clients";
        public static final String CHILD = "Child";
        public static final String HIV_CLIENTS = "Hiv Clients";
        public static final String ANC = "ANC";
        public static final String LD = "L&D";
        public static final String PNC = "PNC";
        public static final String FAMILY_PLANNING = "Family Planning";
        public static final String MALARIA = "Malaria";
        public static final String REFERRALS = "Referrals";
    }

    public static final class RULE_FILE {
        public static final String HOME_VISIT = "home-visit-rules.yml";
        public static final String ANC_HOME_VISIT = "anc-home-visit-rules.yml";
        public static final String PNC_HOME_VISIT = "pnc-home-visit-rules.yml";
        public static final String IMMUNIZATION_EXPIRED = "immunization-expire-rules.yml";
        public static final String CONTACT_RULES = "contact-rules.yml";
        public static final String PNC_HEALTH_FACILITY_VISIT = "pnc-health-facility-schedule-rule.yml";
        public static final String MALARIA_FOLLOW_UP_VISIT = "malaria-followup-rules.yml";
        public static final String FP_COC_POP_REFILL = "fp-coc-pop-refill-rule.yml";
        public static final String FP_CONDOM_REFILL = "fp-condom-refill-rule.yml";
        public static final String FP_INJECTION_DUE = "fp-injection-due-rule.yml";
        public static final String FP_FEMALE_STERILIZATION = "fp-female-sterilization-rule.yml";
        public static final String FP_IUCD ="fp-iucd-rule.yml";
    }

    public static class PROFILE_CHANGE_ACTION {
        public static final String ACTION_TYPE = "change_action_type";
        public static final String PRIMARY_CARE_GIVER = "change_primary_cg";
        public static final String HEAD_OF_FAMILY = "change_head";
    }

    public static class JsonAssets {
        public static final String DETAILS = "details";
        public static final String FAM_NAME = "fam_name";
        public static final String SURNAME = "surname";
        public static final String PREGNANT_1_YR = "preg_1yr";
        public static final String SEX = "sex";
        public static final String PRIMARY_CARE_GIVER = "primary_caregiver";
        public static final String IS_PRIMARY_CARE_GIVER = "is_primary_caregiver";
        public static final String AGE = "age";
        public static final String ID_AVAIL = "id_avail";
        public static final String NATIONAL_ID = "national_id";
        public static final String VOTER_ID = "voter_id";
        public static final String DRIVER_LICENSE = "driver_license";
        public static final String PASSPORT = "passport";
        public static final String INSURANCE_PROVIDER = "insurance_provider";
        public static final String INSURANCE_PROVIDER_OTHER = "insurance_provider_other";
        public static final String INSURANCE_PROVIDER_NUMBER = "insurance_provider_number";
        public static final String DISABILITIES = "disabilities";
        public static final String DISABILITY_TYPE = "type_of_disability";
        public static final String SERVICE_PROVIDER = "service_provider";
        public static final String LEADER = "leader";
        public static final String OTHER_LEADER = "leader_other";
        public static final String BIRTH_CERT_AVAILABLE = "birth_cert_available";
        public static final String BIRTH_REGIST_NUMBER = "birth_regist_number";
        public static final String RHC_CARD = "rhc_card";
        public static final String NUTRITION_STATUS = "nutrition_status";
        public static final String GESTATION_AGE = "gestation_age";
        public static final String IS_PROBLEM = "is_problem";
        public static final String REFERRAL_CODE = "Referral";


        public static class FAMILY_MEMBER {
            public static final String HIGHEST_EDUCATION_LEVEL = "highest_edu_level";
            public static final String PHONE_NUMBER = "phone_number";
            public static final String OTHER_PHONE_NUMBER = "other_phone_number";
        }
    }

    public static class ProfileActivityResults {
        public static final int CHANGE_COMPLETED = 9090;
    }

    public static class FORM_CONSTANTS {

        public static class REMOVE_MEMBER_FORM {
            public static final String REASON = "remove_reason";
            public static final String DATE_DIED = "date_died";
            public static final String DATE_MOVED = "date_moved";
        }

        public static class CHANGE_CARE_GIVER {
            public static class PHONE_NUMBER {
                public static final String CODE = "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            }

            public static class OTHER_PHONE_NUMBER {
                public static final String CODE = "5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                public static final String PARENT_CODE = "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            }

            public static class HIGHEST_EDU_LEVEL {
                public static final String CODE = "1712AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            }
        }

        public static class ILLNESS_ACTION_TAKEN_LEVEL {
            public static final String CODE = "164378AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        }

        public static class VACCINE_CARD {
            public static final String CODE = "164147AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        }

        public static class MINIMUM_DIETARY {
            public static final String CODE = "";
        }

        public static class MUAC {
            public static final String CODE = "160908AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        }

        public static class LLITN {
            public static final String CODE = "1802AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
        }

        public static class FORM_SUBMISSION_FIELD {
            public static final String TASK_MINIMUM_DIETARY = "diet_diversity";
            public static final String TASK_MUAC = "muac";
            public static final String TASK_LLITN = "llitn";
            public static final String HOME_VISIT_ID = "home_visit_id";
            public static final String HOME_VISIT_DATE_LONG = "home_visit_date";
            public static final String LAST_HOME_VISIT = "last_home_visit";
            public static final String HOME_VISIT_SINGLE_VACCINE = "singleVaccine";
            public static final String HOME_VISIT_GROUP_VACCINE = "groupVaccine";
            public static final String HOME_VISIT_VACCINE_NOT_GIVEN = "vaccineNotGiven";
            public static final String HOME_VISIT_SERVICE = "service";
            public static final String HOME_VISIT_SERVICE_NOT_GIVEN = "serviceNotGiven";
            public static final String HOME_VISIT_BIRTH_CERT = "birth_certificate";
            public static final String HOME_VISIT_ILLNESS = "illness_information";
            public static final String WASH_CHECK_DETAILS = "details_info";
            public static final String WASH_CHECK_LAST_VISIT = "last_visit";
            public static final String FAMILY_ID = "family_id";
            public static final String REFERRAL_TASK = "referral_task";
            public static final String REFERRAL_TASK_PREVIOUS_STATUS = "referral_task_previous_status";
            public static final String REFERRAL_TASK_PREVIOUS_BUSINESS_STATUS = "referral_task_previous_business_status";


        }
    }

    public static class GLOBAL {
        public static final String NAME = "name";
        public static final String MESSAGE = "message";
    }

    public static class MenuType {
        public static final String ChangeHead = "ChangeHead";
        public static final String ChangePrimaryCare = "ChangePrimaryCare";
    }

    public static class IDENTIFIER {
        public static final String UNIQUE_IDENTIFIER_KEY = "opensrp_id";
    }

    public static final class RQ_CODE {
        public static final int STORAGE_PERMISIONS = 1;
    }

    public static final class PEER_TO_PEER {

        public static final String LOCATION_ID = "location-id";
    }

    public static final class ACTIVITY_PAYLOAD {
        public static final String ACTION = "action";
    }

    public static final class ACTION {
        public static final String START_REGISTRATION = "start_registration";
    }

    public static final class VISIT_STATE {
        public static final String EXPIRED = "EXPIRED";
        public static final String DUE = "DUE";
        public static final String OVERDUE = "OVERDUE";
        public static final String VISIT_NOT_DONE = "VISIT_NOT_DONE";
        public static final String VISIT_DONE = "VISIT_DONE";
        public static final String NOT_VISIT_THIS_MONTH = "NOT_VISIT_THIS_MONTH";
        public static final String NOT_DUE_YET = "NOT_DUE_YET";
    }

    public static final class DATE_FORMATS {
        public static final String NATIVE_FORMS = "dd-MM-yyyy";
        public static final String HOME_VISIT_DISPLAY = "dd MMM yyyy";
        public static final String FORMATED_DB_DATE = "yyyy-MM-dd";
        public static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm'Z'";
    }

    public static final class REGISTERED_ACTIVITIES {
        public static final String CHILD_REGISTER_ACTIVITY = "CHILD_REGISTER_ACTIVITY";
        public static final String FAMILY_REGISTER_ACTIVITY = "FAMILY_REGISTER_ACTIVITY";
        public static final String ANC_REGISTER_ACTIVITY = "ANC_REGISTER_ACTIVITY";
        public static final String PNC_REGISTER_ACTIVITY = "PNC_REGISTER_ACTIVITY";
        public static final String REFERRALS_REGISTER_ACTIVITY = "REFERRALS_REGISTER_ACTIVITY";
        public static final String MALARIA_REGISTER_ACTIVITY = "MALARIA_REGISTER_ACTIVITY";
        public static final String FP_REGISTER_ACTIVITY = "FP_REGISTER_ACTIVITY";
        public static final String ALL_CLIENTS_REGISTERED_ACTIVITY = "ALL_CLIENTS";
    }

    public static final class BUSINESS_STATUS {
        public static final String REFERRED = "Referred";
        public static final String IN_PROGRESS = "In-Progress";
        public static final String COMPLETE = "Complete";
        public static final String EXPIRED = "Expired";
    }

    public static final class TASKS_FOCUS {
        public static final String SICK_CHILD = "Sick Child";
        public static final String ANC_DANGER_SIGNS = "ANC Danger Signs";
        public static final String PNC_DANGER_SIGNS = "PNC Danger Signs";
        public static final String ADOLESCENT_DANGER_SIGNS = "Adolescent Danger Signs";
    }
}
