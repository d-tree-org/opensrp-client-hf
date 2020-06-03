package org.smartregister.hf.activity;

import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.viewpager.widget.ViewPager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.hf.R;
import org.smartregister.hf.contract.FamilyFocusedMemberProfileContract;
import org.smartregister.hf.custom_views.FamilyMemberFloatingMenu;
import org.smartregister.hf.presenter.FamilyFocusedMemberProfileActivityPresenter;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.model.BaseFamilyProfileMemberModel;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.Utils;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyFocusedMemberProfileActivity extends BaseProfileActivity implements FamilyFocusedMemberProfileContract.View {

    private TextView nameView;
    private TextView detailOneView;
    private TextView detailTwoView;
    private TextView detailThreeView;
    private CircleImageView imageView;
    private CustomFontTextView markAsDone;

    //Referral Details Views
    private View referralDetailsSection;
    private TextView referralReason;
    private TextView referralIndicators;
    private TextView referralDate;
    private TextView referralSource;
    private TextView referee;

    private View familyHeadView;
    private View primaryCaregiverView;

    protected ViewPagerAdapter adapter;
    private String familyBaseEntityId;
    private String baseEntityId;
    private String familyHead;
    private String primaryCaregiver;
    private String villageTown;
    private String familyName;
    private String PhoneNumber;
    private CommonPersonObjectClient commonPersonObject;
    protected MemberObject memberObject;
    private FamilyMemberFloatingMenu familyFloatingMenu;

    private String referralEntityId;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_focused_member_profile);

        Toolbar toolbar = findViewById(R.id.addo_focused_family_member_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable backArrow = getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp);
            backArrow.setColorFilter(getResources().getColor(R.color.addo_primary), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(backArrow);
            actionBar.setTitle("");
        }
        this.appBarLayout = findViewById(R.id.toolbar_appbarlayout_addo_focused);

        this.imageRenderHelper = new ImageRenderHelper(this);

        initializePresenter();

        setupViews();

        presenter().getReferralData(baseEntityId);

    }

    @Override
    protected void setupViews() {
        super.setupViews();
        TextView toolBarTitle = findViewById(R.id.toolbar_title_focused);
        toolBarTitle.setText(String.format(getString(R.string.return_to_family_name), presenter().getFamilyName()));

        detailOneView = findViewById(R.id.textview_detail_one_focused);
        detailTwoView = findViewById(R.id.textview_detail_two_focused);
        detailThreeView = findViewById(R.id.textview_detail_three_focused);

        familyHeadView = findViewById(R.id.family_head_focused);
        primaryCaregiverView = findViewById(R.id.primary_caregiver_focused);

        nameView = findViewById(R.id.textview_name_focused);

        imageView = findViewById(R.id.imageview_profile);
        imageView.setBorderWidth(2);

        markAsDone = findViewById(R.id.tv_mark_as_done);
        markAsDone.setOnClickListener(this);

        referralDetailsSection  = findViewById(R.id.referral_details_section);
        referralDetailsSection.setVisibility(View.GONE);

        referralReason = findViewById(R.id.referral_focus);
        referralIndicators = findViewById(R.id.referral_danger_signs);
        referralDate = findViewById(R.id.referral_date);
        referralSource = findViewById(R.id.referral_source);
        referee = findViewById(R.id.referee);

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
        presenter = new FamilyFocusedMemberProfileActivityPresenter(this, new BaseFamilyProfileMemberModel(), null, baseEntityId,
                familyBaseEntityId, familyHead, primaryCaregiver, familyName, villageTown);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        return null;
    }

    @Override
    protected void fetchProfileData() {
        presenter().fetchProfileData();
    }

    @Override
    public FamilyFocusedMemberProfileContract.Presenter presenter() {
        return (FamilyFocusedMemberProfileContract.Presenter) presenter;
    }

    @Override
    public void setProfileImage(String baseEntityId, String entityType) {
        imageRenderHelper.refreshProfileImage(baseEntityId, imageView, Utils.getMemberProfileImageResourceIDentifier(entityType));
    }

    @Override
    public void setProfileName(String fullName) {
        nameView.setText(fullName);
    }

    @Override
    public void setProfileDetailOne(String detailOne) {
        detailOneView.setText(detailOne);
    }

    @Override
    public void setProfileDetailTwo(String detailTwo) {
        detailTwoView.setText(detailTwo);
    }

    @Override
    public void setProfileDetailThree(String detailThree) {
        detailThreeView.setText(detailThree);
    }

    @Override
    public void setReferralDetailsView(boolean hasReferral) {
        referralDetailsSection.setVisibility( hasReferral ? View.VISIBLE : View.GONE);
    }

    @Override
    public void goToFamilyProfile() {
        finish();
    }

    @Override
    public void setReferralDetails(String entityId, String focus, String indicators, String date, String source, String referredBy) {
        referralReason.setText(focus);
        referralIndicators.setText(indicators);

        SimpleDateFormat sdf = new SimpleDateFormat("E MMM d, hh:mm aa", Locale.getDefault());
        Date dateTime;
        String formattedDate = "";

        try {
            dateTime = DateTime.parse(date).toDate();
            formattedDate = sdf.format(dateTime);
        }catch (Exception e){
            e.printStackTrace();
        }

        referralDate.setText(formattedDate);
        referralSource.setText(source);
        referee.setText(referredBy);

        this.referralEntityId = entityId;
    }

    @Override
    public void toggleFamilyHead(boolean show) {
        if (show) {
            familyHeadView.setVisibility(View.VISIBLE);
        } else {
            familyHeadView.setVisibility(View.GONE);
        }
    }

    @Override
    public void togglePrimaryCaregiver(boolean show) {
        if (show) {
            primaryCaregiverView.setVisibility(View.VISIBLE);
        } else {
            primaryCaregiverView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        presenter().refreshProfileView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_mark_as_done:
                confirmMarkingAsDone();
                break;
            default:
                super.onClick(view);
                break;
        }
    }

    void confirmMarkingAsDone(){
        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_complete_referral_confirmation)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        presenter().markReferralAsDone(baseEntityId);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.alert_light_frame)
                .show();
    }

}
