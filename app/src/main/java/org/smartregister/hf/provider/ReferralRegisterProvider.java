package org.smartregister.hf.provider;

import static org.smartregister.hf.util.Constants.DB_CONSTANTS.TASK_STATUS_IN_PROGRESS;
import static org.smartregister.hf.util.Constants.DB_CONSTANTS.TASK_STATUS_READY;
import static org.smartregister.util.Utils.getName;

import android.content.Context;
import android.database.Cursor;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;
import org.opensrp.api.constants.Gender;
import org.smartregister.chw.referral.fragment.BaseReferralRegisterFragment;
import org.smartregister.chw.referral.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.hf.R;
import org.smartregister.hf.util.Constants;
import org.smartregister.hf.util.CoreConstants;
import org.smartregister.util.DateUtil;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Set;

import timber.log.Timber;

public class ReferralRegisterProvider implements RecyclerViewProvider<ReferralRegisterProvider.RegisterViewHolder> {
    protected static CommonPersonObjectClient client;
    private final LayoutInflater inflater;
    protected View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Context context;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    public ReferralRegisterProvider(Context context, View.OnClickListener paginationClickListener, View.OnClickListener onClickListener, Set visibleColumns) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.paginationClickListener = paginationClickListener;
        this.onClickListener = onClickListener;
        this.visibleColumns = visibleColumns;
        this.context = context;

    }

    private static String getCategory(Context context, String problem, String referralTypeArg) {
        int referralType = Integer.parseInt(referralTypeArg);
        if (problem.equalsIgnoreCase(CoreConstants.TASKS_FOCUS.SICK_CHILD)) {
            if (referralType == 1) {
                return context.getString(R.string.child_hf_referral_text);
            }
            return context.getString(R.string.child_addo_linkage_text);
        } else if (problem.equalsIgnoreCase(CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS)) {
            if (referralType == 1) {
                return context.getString(R.string.anc_hf_referral_text);
            }
            return context.getString(R.string.anc_addo_linkage_text);
        } else if (problem.equalsIgnoreCase(CoreConstants.TASKS_FOCUS.ADOLESCENT_DANGER_SIGNS)) {
            if (referralType == 1) {
                return context.getString(R.string.adolescent_hf_referral_text);
            }
            return context.getString(R.string.adolescent_addo_linkage_text);
        } else {
            if (referralType == 1) {
                return context.getString(R.string.pnc_hf_referral_text);
            }
            return context.getString(R.string.pnc_addo_linkage_text);
        }
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient smartRegisterClient, RegisterViewHolder registerViewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, registerViewHolder);
        }
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, final RegisterViewHolder viewHolder) {
        try {
            String fname = getName(
                    Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true),
                    Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true));

            String dobString = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false);
            int age = new Period(new DateTime(dobString), new DateTime()).getYears();

            String focus = Utils.getValue(pc.getColumnmaps(), CoreConstants.DB_CONSTANTS.FOCUS, true);
            String priority = Utils.getValue(pc.getColumnmaps(), Constants.DB_CONSTANTS.PRIORITY, true);
            String status = Utils.getValue(pc.getColumnmaps(), CoreConstants.DB_CONSTANTS.STATUS, true);
            Long authoredOn = Long.parseLong(Utils.getValue(pc.getColumnmaps(), Constants.DB_CONSTANTS.AUTHORED_ON, true));

            String patientName = getName(fname, Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true));
            viewHolder.patientName.setText(String.format(Locale.getDefault(), "%s, %d", patientName, age));
            viewHolder.textViewGender.setText(getTranslatedGender(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.GENDER, false)));
            viewHolder.textViewService.setText(getCategory(context, focus, priority));
            viewHolder.textViewFacility.setText(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.REFERRAL_HF, true));

            viewHolder.patientColumn.setOnClickListener(onClickListener);
            viewHolder.patientColumn.setTag(pc);
            viewHolder.patientColumn.setTag(R.id.VIEW_ID, BaseReferralRegisterFragment.CLICK_VIEW_NORMAL);

            viewHolder.textReferralStatus.setOnClickListener(onClickListener);
            viewHolder.textReferralStatus.setTag(pc);
            viewHolder.textReferralStatus.setTag(R.id.VIEW_ID, BaseReferralRegisterFragment.FOLLOW_UP_VISIT);
            viewHolder.registerColumns.setOnClickListener(onClickListener);

            viewHolder.registerColumns.setOnClickListener(v -> viewHolder.patientColumn.performClick());
            setReferralStatusColor(context, viewHolder, status, priority, authoredOn);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getTranslatedGender(String gender) {
        return gender.equalsIgnoreCase(Gender.FEMALE.toString()) ? context.getResources().getString(org.smartregister.family.R.string.female) : context.getResources().getString(org.smartregister.family.R.string.male);
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(MessageFormat.format(context.getString(org.smartregister.R.string.str_page_info), currentPageCount, totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption filterOption, ServiceModeOption serviceModeOption, FilterOption filterOption1, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
//        implement
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String s, String s1, String s2) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public RegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.referral_register_list_row_item, parent, false);
        return new RegisterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }

    private void setReferralStatusColor(Context context, RegisterViewHolder
            viewHolder, String status, String priority, Long authoredOn) {

        int days = Math.abs(Days.daysBetween(DateUtil.getDateTimeFromMillis(authoredOn), DateTime.now()).getDays());
        if ((days >= 1 && priority.equals("1")) || days >= 3) {
            setTasksOverdueStatus(context, viewHolder);
        } else {
            setTasksDueStatus(context, viewHolder);
        }

        switch (status) {
            case TASK_STATUS_READY:
                markTaskReady(context, viewHolder);
                break;
            case TASK_STATUS_IN_PROGRESS:
                markTaskInProgress(context, viewHolder);
                break;
            default:
                break;
        }
    }

    private void setTasksOverdueStatus(Context context, ReferralRegisterProvider.RegisterViewHolder viewHolder) {
        viewHolder.textReferralStatus.setText(context.getText(R.string.overdue));
        viewHolder.textReferralStatus.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.arrivedMessage.setTextColor(context.getResources().getColor(R.color.white));
        viewHolder.splitLine.setColorFilter(context.getResources().getColor(R.color.white));
        viewHolder.doneIcon.setColorFilter(context.getResources().getColor(R.color.white));
        viewHolder.dueWrapper.setBackgroundResource(R.drawable.overdue_red_btn_selector);
    }

    private void setTasksDueStatus(Context context, ReferralRegisterProvider.RegisterViewHolder viewHolder) {
        viewHolder.textReferralStatus.setText(context.getText(R.string.due));
        viewHolder.textReferralStatus.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
        viewHolder.arrivedMessage.setTextColor(context.getResources().getColor(R.color.alert_in_progress_blue));
        viewHolder.splitLine.setColorFilter(context.getResources().getColor(R.color.alert_in_progress_blue));
        viewHolder.doneIcon.setColorFilter(context.getResources().getColor(R.color.alert_in_progress_blue));
        viewHolder.dueWrapper.setBackgroundResource(R.drawable.blue_btn_selector);
    }

    private void markTaskReady(Context context, ReferralRegisterProvider.RegisterViewHolder viewHolder) {
        viewHolder.arrivedMessage.setVisibility(View.GONE);
        viewHolder.arrivedLayout.setVisibility(View.GONE);
        viewHolder.textReferralStatus.setGravity(Gravity.CENTER);
        viewHolder.splitLine.setVisibility(View.GONE);

    }

    private void markTaskInProgress(Context context, ReferralRegisterProvider.RegisterViewHolder viewHolder) {
        viewHolder.arrivedMessage.setVisibility(View.VISIBLE);
        viewHolder.arrivedLayout.setVisibility(View.VISIBLE);
        viewHolder.textReferralStatus.setGravity(Gravity.CENTER_HORIZONTAL);
        viewHolder.splitLine.setVisibility(View.VISIBLE);

    }

    public class RegisterViewHolder extends RecyclerView.ViewHolder {
        public TextView patientName;
        public TextView textViewGender;
        public TextView textReferralStatus;
        public View patientColumn;
        public TextView textViewService;
        public TextView textViewFacility;
        public View registerColumns;
        public View dueWrapper;
        public TextView arrivedMessage;
        public ImageView doneIcon;
        public LinearLayout arrivedLayout;
        public ImageView splitLine;

        public RegisterViewHolder(View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.patient_name_age);
            textViewGender = itemView.findViewById(R.id.text_view_gender);
            textReferralStatus = itemView.findViewById(R.id.text_view_referral_status);
            patientColumn = itemView.findViewById(R.id.patient_column);
            registerColumns = itemView.findViewById(R.id.register_columns);
            dueWrapper = itemView.findViewById(R.id.due_button_wrapper);
            textViewService = itemView.findViewById(R.id.text_view_service);
            textViewFacility = itemView.findViewById(R.id.text_view_facility);
            arrivedMessage = itemView.findViewById(R.id.arrived_message);
            splitLine = itemView.findViewById(R.id.split_line);
            arrivedLayout = itemView.findViewById(R.id.arrived_layout);
            doneIcon = itemView.findViewById(R.id.done_icon);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView pageInfoView;
        public Button nextPageView;
        public Button previousPageView;

        public FooterViewHolder(View view) {
            super(view);

            nextPageView = view.findViewById(org.smartregister.R.id.btn_next_page);
            previousPageView = view.findViewById(org.smartregister.R.id.btn_previous_page);
            pageInfoView = view.findViewById(org.smartregister.R.id.txt_page_info);
        }
    }
}
