package org.smartregister.hf.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.smartregister.hf.R;
import org.smartregister.hf.contract.FamilyProfileExtendedContract;
import org.smartregister.hf.custom_views.FamilyFloatingMenu;
import org.smartregister.hf.dao.AncDao;
import org.smartregister.hf.dao.PNCDao;
import org.smartregister.hf.event.PermissionEvent;
import org.smartregister.hf.fragment.FamilyProfileActivityFragment;
import org.smartregister.hf.fragment.FamilyProfileMemberFragment;
import org.smartregister.hf.listeners.FloatingMenuListener;
import org.smartregister.hf.model.FamilyProfileModel;
import org.smartregister.hf.presenter.FamilyProfilePresenter;
import org.smartregister.hf.util.ChildDBConstants;
import org.smartregister.hf.util.CoreConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.FetchStatus;
import org.smartregister.family.activity.BaseFamilyProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyProfileMemberFragment;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.PermissionUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;


public class FamilyProfileActivity extends BaseFamilyProfileActivity implements FamilyProfileExtendedContract.View {

    private static final String TAG = FamilyProfileActivity.class.getCanonicalName();
    private String familyBaseEntityId;
    private String familyHead;
    private String primaryCaregiver;
    private String familyName;

    private FamilyFloatingMenu familyFloatingMenu;

    @Override
    protected void onCreation() {
        super.onCreation();
        setContentView(R.layout.activity_family_profile);

        Toolbar toolbar =  findViewById(R.id.family_toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        this.appBarLayout = this.findViewById(R.id.toolbar_appbarlayout);
        this.imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();
        setupViews();
    }

    @Override
    protected void initializePresenter() {
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        familyHead = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);
        primaryCaregiver = getIntent().getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        // Update profile border
        CircleImageView profileView = findViewById(R.id.imageview_profile);
        profileView.setBorderWidth(2);

        // add floating menu
        familyFloatingMenu = new FamilyFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        addContentView(familyFloatingMenu, linearLayoutParams);
        familyFloatingMenu.setClickListener(
                FloatingMenuListener.getInstance(this, presenter().familyBaseEntityId())
        );
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        BaseFamilyProfileMemberFragment profileMemberFragment = FamilyProfileMemberFragment.newInstance(this.getIntent().getExtras());
        //BaseFamilyProfileDueFragment profileDueFragment = FamilyProfileDueFragment.newInstance(this.getIntent().getExtras());
        //BaseFamilyProfileActivityFragment profileActivityFragment = FamilyProfileActivityFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileMemberFragment, this.getString(R.string.family_members).toUpperCase());
        //adapter.addFragment(profileDueFragment, this.getString(org.smartregister.family.R.string.due).toUpperCase());
        //adapter.addFragment(profileActivityFragment, this.getString(R.string.med_history).toUpperCase());

        viewPager.setAdapter(adapter);

        if (getIntent().getBooleanExtra(org.smartregister.hf.util.Constants.INTENT_KEY.SERVICE_DUE, true) || getIntent().getBooleanExtra(Constants.INTENT_KEY.GO_TO_DUE_PAGE, false)) {
            viewPager.setCurrentItem(0);
        }

        return viewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem addMember = menu.findItem(R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        menu.removeItem(R.id.add_member);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_family_details:
            case R.id.action_item1_placeholder:
            case R.id.action_item2_placeholder:

                // TODO Add Menu Item here

                break;
            default:
                super.onOptionsItemSelected(item);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                boolean granted = (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (granted) {
                    PermissionEvent event = new PermissionEvent(requestCode, granted);
                    EventBus.getDefault().post(event);
                } else {
                    Toast.makeText(this, getText(R.string.allow_calls_denied), Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                break;
        }
    }

    public String getFamilyBaseEntityId() {
        return familyBaseEntityId;
    }

    public void startFormForEdit() {
        if (familyBaseEntityId != null) {
            presenter().fetchProfileData();
        }
    }

    // Child Form

    @Override
    public void startChildForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        presenter().startChildForm(formName, entityId, metadata, currentLocationId);
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        if (familyFloatingMenu != null) {
            familyFloatingMenu.reDraw(hasPhone);
        }
    }

    private void refreshPresenter() {
        presenter = new FamilyProfilePresenter(this, new FamilyProfileModel(familyName), familyBaseEntityId, familyHead, primaryCaregiver, familyName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            Log.v("SIMPRINT_SDK","HNPPMemberJsonFormActivity >>requestCode:"+requestCode+":resultCode:"+resultCode+":intent:"+data);

            switch (requestCode) {
                case org.smartregister.family.util.JsonFormUtils.REQUEST_CODE_GET_JSON:
                    try {
                        String jsonString = data.getStringExtra(org.smartregister.family.util.Constants.JSON_FORM_EXTRA.JSON);
                        System.err.println(jsonString);
                        Log.d("Turnit", jsonString);
                        //Timber.d("JSONResult", jsonString);

                        JSONObject form = new JSONObject(jsonString);
                        String encounter_type = form.getString(org.smartregister.family.util.JsonFormUtils.ENCOUNTER_TYPE);
                        // process child registration
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.updateEventType)) {

                            presenter().updateFamilyRegister(jsonString);
                            presenter().verifyHasPhone();

                        } else if (encounter_type.equals(org.smartregister.hf.util.Constants.EventType.CHILD_REGISTRATION)) {

                            presenter().saveChildForm(jsonString, false);

                        } else if (encounter_type.equals(Utils.metadata().familyMemberRegister.registerEventType)) {

                            String careGiver = presenter().saveChwFamilyMember(jsonString);
                            if (presenter().updatePrimaryCareGiver(getApplicationContext(), jsonString, familyBaseEntityId, careGiver)) {
                                setPrimaryCaregiver(careGiver);
                                refreshPresenter();
                                refreshMemberFragment(careGiver, null);
                            }

                            presenter().verifyHasPhone();
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case org.smartregister.hf.util.Constants.ProfileActivityResults.CHANGE_COMPLETED:
                    try {

                        String careGiverID = data.getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
                        String familyHeadID = data.getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);

                        setPrimaryCaregiver(careGiverID);
                        setFamilyHead(familyHeadID);

                        refreshMemberFragment(careGiverID, familyHeadID);
                        presenter().verifyHasPhone();

                    } catch (Exception e) {
                        Timber.e(e);
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case org.smartregister.hf.util.Constants.SIMPRINTS_IDENTIFICATION.IDENTIFY_RESULT_CODE:
                    Boolean check = data.getBooleanExtra("biometricsComplete" , false);
            }
        }
    }

    private void refreshMemberFragment(String careGiverID, String familyHeadID) {
        BaseFamilyProfileMemberFragment memberFragment = this.getProfileMemberFragment();
        if (memberFragment != null) {
            if (StringUtils.isNotBlank(careGiverID)) {
                memberFragment.setPrimaryCaregiver(careGiverID);
            }
            if (StringUtils.isNotBlank(familyHeadID)) {
                memberFragment.setFamilyHead(familyHeadID);
            }
            refreshMemberList(FetchStatus.fetched);
        }
    }

    @Override
    public void refreshMemberList(FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            for (int i = 0; i < adapter.getCount(); i++) {
                refreshList(adapter.getItem(i));
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        refreshList(adapter.getItem(i));
                    }
                }
            });
        }
    }

    private void refreshList(Fragment fragment) {
        if (fragment != null && fragment instanceof BaseRegisterFragment) {
            if (fragment instanceof FamilyProfileMemberFragment) {
                FamilyProfileMemberFragment familyProfileMemberFragment = ((FamilyProfileMemberFragment) fragment);
                if (familyProfileMemberFragment.presenter() != null) {
                    familyProfileMemberFragment.refreshListView();
                }
            }
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        FloatingMenuListener.getInstance(this, presenter().familyBaseEntityId());
    }

    public void setPrimaryCaregiver(String caregiver) {
        if (StringUtils.isNotBlank(caregiver)) {
            this.primaryCaregiver = caregiver;
            getIntent().putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, caregiver);
        }
    }

    public String getPrimaryCaregiver() {
        return primaryCaregiver;
    }

    public String getFamilyHead(){
        return this.familyHead;
    }

    public void setFamilyHead(String head) {
        if (StringUtils.isNotBlank(head)) {
            this.familyHead = head;
            getIntent().putExtra(Constants.INTENT_KEY.FAMILY_HEAD, head);
        }
    }

    @Override
    public FamilyProfileExtendedContract.Presenter presenter() {
        return (FamilyProfilePresenter) presenter;
    }

    public void goToProfileActivity(View view, Bundle fragmentArguments) {
        if (view.getTag() instanceof CommonPersonObjectClient) {
            CommonPersonObjectClient commonPersonObjectClient = (CommonPersonObjectClient) view.getTag();
            String entityType = Utils.getValue(commonPersonObjectClient.getColumnmaps(), ChildDBConstants.KEY.ENTITY_TYPE, false);
            if (CoreConstants.TABLE_NAME.FAMILY_MEMBER.equals(entityType)) {

                /// Removed separate screens for focused group members and the rest of the members
                /*if (!(isAncMember(commonPersonObjectClient.entityId()) || isPncMember(commonPersonObjectClient.entityId()))) {
                    goToOtherMemberProfileActivity(commonPersonObjectClient, fragmentArguments);
                } else {
                    goToFocusMemberProfileActivity(commonPersonObjectClient, fragmentArguments);
                }*/

                goToFocusMemberProfileActivity(commonPersonObjectClient, fragmentArguments);

            } else {
                goToFocusMemberProfileActivity(commonPersonObjectClient, fragmentArguments);
            }
        }
    }

    private void goToFocusMemberProfileActivity(CommonPersonObjectClient patient, Bundle fragmentArguments) {
        Intent intent = new Intent(this, FamilyFocusedMemberProfileActivity.class);
        if (fragmentArguments != null) {
            intent.putExtras(fragmentArguments);
        }
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.hf.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, getFamilyHead());
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, getPrimaryCaregiver());
        startActivity(intent);
    }

    public void goToOtherMemberProfileActivity(CommonPersonObjectClient patient, Bundle fragmentArguments) {
        Intent intent = new Intent(this, FamilyOtherMemberProfileActivity.class);
        if (fragmentArguments != null) {
            intent.putExtras(fragmentArguments);
        }
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.hf.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, getFamilyHead());
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, getPrimaryCaregiver());
        startActivity(intent);
    }

    private boolean isPncMember(String entityId) {
        return PNCDao.isPNCMember(entityId);
    }

    private boolean isAncMember(String entityId) {
        return AncDao.isANCMember(entityId);
    }
}
