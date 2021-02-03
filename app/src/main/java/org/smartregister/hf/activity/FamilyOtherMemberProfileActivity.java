package org.smartregister.hf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;
import org.smartregister.hf.R;
import org.smartregister.hf.contract.FamilyOtherMemberProfileExtendedContract;
import org.smartregister.hf.custom_views.FamilyMemberFloatingMenu;
import org.smartregister.hf.dataloader.AncMemberDataLoader;
import org.smartregister.hf.dataloader.FamilyMemberDataLoader;
import org.smartregister.hf.form_data.NativeFormsDataBinder;
import org.smartregister.hf.fragment.FamilyOtherMemberProfileFragment;
import org.smartregister.hf.listeners.FloatingMenuListener;
import org.smartregister.hf.listeners.OnClickFloatingMenu;
import org.smartregister.hf.presenter.FamilyOtherMemberActivityPresenter;
import org.smartregister.hf.util.CoreConstants;
import org.smartregister.hf.util.CoreJsonFormUtils;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.activity.BaseFamilyOtherMemberProfileActivity;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.util.FormUtils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import timber.log.Timber;

public class FamilyOtherMemberProfileActivity extends BaseFamilyOtherMemberProfileActivity implements FamilyOtherMemberProfileExtendedContract.View {

    private String familyBaseEntityId;
    private String baseEntityId;
    private String familyHead;
    private String primaryCaregiver;
    private String villageTown;
    private String familyName;
    private String PhoneNumber;
    private CommonPersonObjectClient commonPersonObject;
    private FamilyMemberFloatingMenu familyFloatingMenu;
    protected MemberObject memberObject;
    private FormUtils formUtils;

    private OnClickFloatingMenu onClickFloatingMenu;


    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_family_other_member_profile_addo);

        Toolbar toolbar = findViewById(R.id.addo_other_family_member_toolbar);
        this.setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            backArrow.setColorFilter(getResources().getColor(R.color.addo_primary), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(backArrow);
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.appBarLayout = findViewById(R.id.toolbar_appbarlayout_addo_non_focused);

        this.imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();

        setupViews();
    }

    @Override
    protected void initializePresenter() {
        commonPersonObject = (CommonPersonObjectClient) getIntent().getSerializableExtra(org.smartregister.hf.util.Constants.INTENT_KEY.CHILD_COMMON_PERSON);
        familyBaseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID);
        baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        familyHead = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_HEAD);
        primaryCaregiver = getIntent().getStringExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER);
        villageTown = getIntent().getStringExtra(Constants.INTENT_KEY.VILLAGE_TOWN);
        familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        PhoneNumber = commonPersonObject.getColumnmaps().get(org.smartregister.hf.util.Constants.JsonAssets.FAMILY_MEMBER.PHONE_NUMBER);
        memberObject = new MemberObject(commonPersonObject);
        presenter = new FamilyOtherMemberActivityPresenter(this, new BaseFamilyOtherMemberProfileActivityModel(), null, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);

        //TODO: Include flavor to implement
        //onClickFloatingMenu = flavor.getOnClickFloatingMenu(this, familyBaseEntityId);

    }

    @Override
    protected void setupViews() {
        super.setupViews();

        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        if(presenter().getFamilyName() == null) {
            toolbarTitle.setText(getString(R.string.search_results_return));
        } else {
            toolbarTitle.setText(String.format(getString(R.string.return_to_family_name), presenter().getFamilyName()));
        }


        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorHeight(0);

        findViewById(R.id.viewpager).setVisibility(View.GONE);

        // add floating menu
        familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        addContentView(familyFloatingMenu, linearLayoutParams);

        familyFloatingMenu.setClickListener(onClickFloatingMenu);

    }

    @Override
    public Context getContext() {
        return this;
    }


    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        BaseFamilyOtherMemberProfileFragment profileOtherMemberFragment = FamilyOtherMemberProfileFragment.newInstance(this.getIntent().getExtras());
        adapter.addFragment(profileOtherMemberFragment, "");

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem addMember = menu.findItem(R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        menu.removeItem(R.id.switchLanguageMenuItem);
        menu.removeItem(R.id.updateMenuItem);
        menu.removeItem(MENU_ITEM_LOGOUT);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public FamilyOtherMemberActivityPresenter presenter() {
        return (FamilyOtherMemberActivityPresenter) presenter;
    }

    public void startFormActivity(JSONObject jsonForm) {
        startActivityForResult(CoreJsonFormUtils.getJsonIntent(this, jsonForm, Utils.metadata().familyMemberFormActivity),
                JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case org.smartregister.hf.util.Constants.ProfileActivityResults.CHANGE_COMPLETED:
                if (resultCode == Activity.RESULT_OK) {
                    Intent intent = new Intent(FamilyOtherMemberProfileActivity.this, FamilyProfileActivity.class);
                    intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                    finish();
                }
                break;
            case JsonFormUtils.REQUEST_CODE_GET_JSON:
                if (resultCode == RESULT_OK) {
                    try {
                        String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                        JSONObject form = new JSONObject(jsonString);
                        if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyMemberRegister.updateEventType)) {
                            presenter().updateFamilyMember(jsonString);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        FloatingMenuListener.getInstance(this, presenter().getFamilyBaseEntityId());
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.family_has_row:
                //TODO: Uncomment to implement viewing family due screen
                //openFamilyDueTab();
                break;

            case R.id.textview_ds_screening:
                // Technically here we can implement the logic to check whether they are ANC or PNC and handle the danger signs for them
                // This line checks whether the woman is already registered as ANC
                startRecordServiceProvided();

            default:
                super.onClick(view);
                break;
        }
    }

    private void startRecordServiceProvided() {
        startFormActivity(getFormUtils().getFormJson(CoreConstants.JSON_FORM.getAddoRecordServiceOther()));
    }

    private FormUtils getFormUtils() {
        if (formUtils == null) {
            try {
                formUtils = FormUtils.getInstance(Utils.context().applicationContext());
            } catch (Exception e) {
                Timber.e(e);
            }
        }
        return formUtils;
    }

}
