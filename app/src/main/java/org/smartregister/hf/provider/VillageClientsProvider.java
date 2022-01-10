package org.smartregister.hf.provider;

import static org.smartregister.hf.fragment.VillageClientsFragment.CLICK_VIEW_NORMAL;
import static org.smartregister.util.Utils.getName;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.family.util.DBConstants;
import org.smartregister.hf.R;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.util.Set;

import timber.log.Timber;

public class VillageClientsProvider implements RecyclerViewProvider<VillageClientsProvider.RegisterViewHolder> {

    protected static CommonPersonObjectClient client;
    private final LayoutInflater inflater;
    protected View.OnClickListener onClickListener;
    private final View.OnClickListener paginationClickListener;
    private final Context context;
    private final Set<org.smartregister.configurableviews.model.View> visibleColumns;

    public VillageClientsProvider(Context context, View.OnClickListener paginationClickListener, View.OnClickListener onClickListener,
                                  Set<org.smartregister.configurableviews.model.View> vc){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.context = context;
        this.visibleColumns = vc;
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

            String patientName = getName(fname, Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true));
            viewHolder.patientName.setText(patientName);

            setAddressAndGender(pc, viewHolder);

            // add patient listener
            viewHolder.patientColumn.setOnClickListener(onClickListener);
            viewHolder.patientColumn.setTag(pc);
            viewHolder.patientColumn.setTag(R.id.VIEW_ID, CLICK_VIEW_NORMAL);
            viewHolder.registerColumns.setOnClickListener(onClickListener);
            viewHolder.registerColumns.setOnClickListener(v -> viewHolder.patientColumn.performClick());

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void setAddressAndGender(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        String gender_key = org.smartregister.family.util.Utils.getValue(pc.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.GENDER, true);
        String gender = "";
        if (gender_key.equalsIgnoreCase("Male")) {
            gender = context.getString(R.string.male);
        } else if (gender_key.equalsIgnoreCase("Female")) {
            gender = context.getString(R.string.female);
        }
        fillValue(viewHolder.textViewAddressAndGender, gender);
    }

    protected static void fillValue(TextView v, String value) {
        if (v != null) {
            v.setText(value);
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        VillageClientsProvider.FooterViewHolder footerViewHolder = (VillageClientsProvider.FooterViewHolder) viewHolder;
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
    public RegisterViewHolder createViewHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.village_client_list_row, viewGroup, false);
        return new RegisterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.smart_register_pagination, viewGroup, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof VillageClientsProvider.FooterViewHolder;
    }

    public static class RegisterViewHolder extends RecyclerView.ViewHolder{

        public TextView patientName;
        public TextView textViewAddressAndGender;
        public TextView textViewHasReferral;
        public View patientColumn;
        public View registerColumns;
        public View clientAvatarWrapper;

        public RegisterViewHolder(View itemView){
            super(itemView);
            patientName = itemView.findViewById(R.id.textview_village_client_name);
            textViewAddressAndGender = itemView.findViewById(R.id.text_view_address_gender);
            textViewHasReferral = itemView.findViewById(R.id.has_referral);
            patientColumn = itemView.findViewById(R.id.village_client_column);
            registerColumns = itemView.findViewById(R.id.register_columns);
            clientAvatarWrapper = itemView.findViewById(R.id.client_avatar_wrapper);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder{

        public TextView pageInfoView;
        public Button nextPageView;
        public Button previousPageView;

        public FooterViewHolder(View footerView){
            super(footerView);
            nextPageView = footerView.findViewById(org.smartregister.R.id.btn_next_page);
            previousPageView = footerView.findViewById(org.smartregister.R.id.btn_previous_page);
            pageInfoView = footerView.findViewById(org.smartregister.R.id.txt_page_info);
        }

    }

}
