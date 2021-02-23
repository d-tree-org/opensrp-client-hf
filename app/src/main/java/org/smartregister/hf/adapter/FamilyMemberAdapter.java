package org.smartregister.hf.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.event.Listener;
import org.smartregister.family.util.Constants;
import org.smartregister.hf.R;
import org.smartregister.hf.util.PullEventClientRecordUtil;
import org.smartregister.hf.activity.FamilyFocusedMemberProfileActivity;
import org.smartregister.hf.activity.FamilyOtherMemberProfileActivity;
import org.smartregister.hf.dao.AdolescentDao;
import org.smartregister.hf.dao.AncDao;
import org.smartregister.hf.dao.PNCDao;
import org.smartregister.hf.domain.Entity;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.hf.util.ChildDBConstants;
import org.smartregister.hf.util.CoreConstants;

import java.util.List;
import java.util.Map;

import static org.smartregister.hf.util.JsonFormUtils.getCommonRepository;

public class FamilyMemberAdapter extends ArrayAdapter<Entity> {

    private boolean isLocal = false;

    public FamilyMemberAdapter(Context context, List<Entity> members, boolean isLocal) {
        super(context, 0, members);
        this.isLocal = isLocal;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Entity member = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.family_member_register_list_row, parent, false);
        }

        // Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.patient_name_age);
        TextView tvGender = convertView.findViewById(R.id.gender);
        ImageView profile = convertView.findViewById(org.smartregister.family.R.id.profile);
        // Populate the data into the template view using the data object

        String fullName = String.format("%s %s %s", isNull(member.getFirstName()), isNull(member.getMiddleName()), isNull(member.getLastName()));
        tvName.setText(fullName);

        tvGender.setText(member.getGender());
        new ImageRenderHelper(getContext()).refreshProfileImage("8e3738ba-c510-44ba-92d2-49e3938d2415", profile,
                Utils.getMemberProfileImageResourceIDentifier(""));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String familyId = member.getFamilyId();
                CommonPersonObject patient= null;
                if (familyId != null) {
                    Log.d("Family", familyId);
                    patient = org.smartregister.family.util.Utils.context().commonrepository(Utils.metadata().familyRegister.tableName)
                            .findByCaseID(familyId);
                }
                if(isLocal && patient != null) {
                    // show family profile
                    Intent intent = new Intent(getContext(), org.smartregister.family.util.Utils.metadata().profileActivity);
                    intent.putExtra("family_base_entity_id", patient.getCaseId());
                    intent.putExtra("family_head",
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "family_head", false));
                    intent.putExtra("primary_caregiver",
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "primary_caregiver", false));
                    intent.putExtra("village_town",
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "village_town", false));
                    intent.putExtra("family_name",
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "first_name", false));
                    intent.putExtra("go_to_due_page", false);
                    getContext().startActivity(intent);
                } else {
                    // check if member exists locally
                    CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyMemberRegister.tableName).findByBaseEntityId(member.getBaseEntityId());

                    if(personObject == null) {
                        // pull client record from server
                        ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        PullEventClientRecordUtil.pullEventClientRecord(member.getBaseEntityId(), pullEventClientRecordListener, progressDialog, "");

                    } else {
                        // show member profile
                        showProfile(personObject, v);
                    }
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    private String isNull(String string) {
        if (string == null) {
            return "";
        } else {
            return string.trim();
        }
    }

    public void goToProfileActivity(View view, Bundle fragmentArguments) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) view.getTag();
            String entityType = Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);
            if (CoreConstants.TABLE_NAME.FAMILY_MEMBER.equals(entityType)) {

                /*if (!(isAncMember(commonPersonObjectClient.entityId()) || isPncMember(commonPersonObjectClient.entityId()) || isAdolescent(commonPersonObjectClient.entityId()))) {
                    goToOtherMemberProfileActivity(commonPersonObjectClient, fragmentArguments);
                } else {
                    goToFocusMemberProfileActivity(commonPersonObjectClient, fragmentArguments);
                }*/

                /**
                 * Changed to open the same screen for both focused and non focused group members
                 */
                goToFocusMemberProfileActivity(commonPersonObjectClient, fragmentArguments);

            } else {
                goToFocusMemberProfileActivity(commonPersonObjectClient, fragmentArguments);
            }
        }
    }

    private void goToFocusMemberProfileActivity(CommonPersonObjectClient patient, Bundle fragmentArguments) {
        Intent intent = new Intent(getContext(), FamilyFocusedMemberProfileActivity.class);
        if (fragmentArguments != null) {
            intent.putExtras(fragmentArguments);
        }
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.hf.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN,
                org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), Constants.INTENT_KEY.VILLAGE_TOWN, false));
        getContext().startActivity(intent);
    }

    public void goToOtherMemberProfileActivity(CommonPersonObjectClient patient, Bundle fragmentArguments) {
        Intent intent = new Intent(getContext(), FamilyOtherMemberProfileActivity.class);
        if (fragmentArguments != null) {
            intent.putExtras(fragmentArguments);
        }
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.hf.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN,
                org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), Constants.INTENT_KEY.VILLAGE_TOWN, false));
        getContext().startActivity(intent);
    }

    private boolean isPncMember(String entityId) {
        return PNCDao.isPNCMember(entityId);
    }

    private boolean isAncMember(String entityId) {
        return AncDao.isANCMember(entityId);
    }

    private boolean isAdolescent(String entityId) { return AdolescentDao.isAdolescentMember(entityId); }

    private final Listener<String> pullEventClientRecordListener = new Listener<String>() {
        public void onEvent(final String baseEntityId) {
            if (baseEntityId != null) {
                // show profile view
                CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyMemberRegister.tableName).findByBaseEntityId(baseEntityId);

                if(personObject == null) return;

                View v = new View(getContext());
                showProfile(personObject, v);

            } else {
                Utils.showShortToast(getContext(), "Error pulling record from server");
            }
        }
    };

    private void showProfile(CommonPersonObject personObject, View v) {
        final CommonPersonObjectClient client = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");

        String relationId = org.smartregister.family.util.Utils.getValue(personObject.getColumnmaps(), "relational_id", false);
        CommonPersonObject family = getCommonRepository(Utils.metadata().familyRegister.tableName).findByBaseEntityId(relationId);

        if(family == null) {
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setCancelable(false);
            PullEventClientRecordUtil.pullEventClientRecord(relationId, pullEventClientRecordListener, progressDialog, personObject.getCaseId());
        }
        else {
            String village = org.smartregister.family.util.Utils.getValue(family.getColumnmaps(), Constants.INTENT_KEY.VILLAGE_TOWN, false);

            Map<String, String> columnMaps = personObject.getColumnmaps();
            columnMaps.put(Constants.INTENT_KEY.VILLAGE_TOWN, village);
            client.setColumnmaps(columnMaps);

            v.setTag(client);

            Bundle s = new Bundle();
            goToProfileActivity(v, s);
        }
    }

}
