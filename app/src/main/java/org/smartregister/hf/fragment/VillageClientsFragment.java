package org.smartregister.hf.fragment;

import static android.provider.ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME;
import static org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID;
import static org.smartregister.family.util.Constants.INTENT_KEY.GO_TO_DUE_PAGE;
import static org.smartregister.family.util.Constants.INTENT_KEY.VILLAGE_TOWN;
import static org.smartregister.hf.util.CoreConstants.RELATIONSHIP.FAMILY_HEAD;
import static org.smartregister.hf.util.CoreConstants.RELATIONSHIP.PRIMARY_CAREGIVER;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.family.util.Utils;
import org.smartregister.hf.R;
import org.smartregister.hf.contract.VillageClientsFragmentContract;
import org.smartregister.hf.model.VillageClientFragmentModel;
import org.smartregister.hf.presenter.VillageClientsFragmentPresenter;
import org.smartregister.hf.provider.VillageClientsProvider;
import org.smartregister.hf.util.CoreConstants;
import org.smartregister.hf.viewmodels.HomescreenViewModel;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

import timber.log.Timber;

public class VillageClientsFragment extends BaseRegisterFragment implements VillageClientsFragmentContract.View {

    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";

    private HomescreenViewModel viewModel;
    private android.view.View view;
    private String villageSelected;

    @Nullable
    @Override
    public android.view.View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_village_client_list, container, false);
        this.rootView = view;
        setupViews(view);
        return view;
    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);
        Toolbar toolbar = view.findViewById(R.id.village_client_list_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);

        toolbar.setContentInsetStartWithNavigation(0);

        ImageButton backButton = view.findViewById(R.id.btn_back_to_villages);
        backButton.setOnClickListener(registerActionHandler);

        TextView tvBacktoVillage = view.findViewById(R.id.return_to_village_txt);
        tvBacktoVillage.setOnClickListener(registerActionHandler);

        TextView edSeachView = view.findViewById(R.id.edt_search);
        edSeachView.setHint(R.string.search_bar_hint);

        viewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory(){
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new HomescreenViewModel();
            }
        }).get(HomescreenViewModel.class);

        viewModel.getSelectedVillage().observe(Objects.requireNonNull(getActivity()), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                joinTable = "";
                villageSelected = s;
                presenter().setSelectedVillage(s);
                mainCondition = getMainCondition();
                presenter().initializeQueries(mainCondition);
            }
        });

    }

    @Override
    public void initializeAdapter(Set<View> configurableColumns) {
        VillageClientsProvider clientsProvider = new VillageClientsProvider(getActivity(), paginationViewHandler, registerActionHandler, configurableColumns);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, clientsProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public VillageClientsFragmentContract.Presenter presenter() {
        return (VillageClientsFragmentContract.Presenter) presenter;
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null)
            return;

        String configurationsIdentifiers = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new VillageClientsFragmentPresenter(this, new VillageClientFragmentModel(), configurationsIdentifiers);

    }

    @Override
    public void setUniqueID(String s) {

    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected String getMainCondition() {
        return this.presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected void onViewClicked(android.view.View view) {
        if (view != null){
            int id = view.getId();
            if (id == R.id.btn_back_to_villages) {
                ((BaseRegisterActivity) Objects.requireNonNull(getActivity())).switchToFragment(0);
            } if (view.getTag() instanceof CommonPersonObjectClient && view.getId() == R.id.village_client_column) {
                CommonPersonObjectClient selectedPatient = (CommonPersonObjectClient) view.getTag();
                String familyID = Utils.getValue(selectedPatient, "family_id", false);
                CommonPersonObject patient;

                if (familyID != null) {
                    patient = org.smartregister.family.util.Utils.context().commonrepository(Utils.metadata().familyRegister.tableName)
                            .findByCaseID(familyID);
                    Intent intent = new Intent(getContext(), org.smartregister.family.util.Utils.metadata().profileActivity);
                    intent.putExtra( FAMILY_BASE_ENTITY_ID, patient.getCaseId());
                    intent.putExtra(FAMILY_HEAD,
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "family_head", false));
                    intent.putExtra(PRIMARY_CAREGIVER,
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "primary_caregiver", false));
                    intent.putExtra(VILLAGE_TOWN,
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "village_town", false));
                    intent.putExtra(FAMILY_NAME,
                            org.smartregister.family.util.Utils.getValue(patient.getColumnmaps(), "first_name", false));
                    intent.putExtra(GO_TO_DUE_PAGE, false);
                    Objects.requireNonNull(getContext()).startActivity(intent);

                }
            }
        }
    }

    @Override
    public void showNotFoundPopup(String s) {

    }

    @Override
    protected void onResumption() {
        super.onResumption();
        Toolbar toolbar = view.findViewById(R.id.village_client_list_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            return new CursorLoader(getActivity()) {
                @Override
                public Cursor loadInBackground() {
                    // Count query
                    final String COUNT = "count_execute";
                    if (args != null && args.getBoolean(COUNT)) {
                        countExecute();
                    }
                    String query = defaultFilterAndSortQuery();
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }
        return super.onCreateLoader(id, args);
    }

    private String defaultFilterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        String customFilter = getFilterString();
        try {
            sqb.addCondition(customFilter);
            query = sqb.orderbyCondition(Sortqueries);
            query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));

        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }

    private String getFilterString() {
        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(MessageFormat.format(" and ( {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.LAST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ) ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.UNIQUE_ID, filters));
        }

        return customFilter.toString();
    }

    @Override
    public void countExecute() {
        Cursor cursor = null;
        try {

            String query = presenter().getCountSelect();

            if (StringUtils.isNotBlank(filters))
                query = query + getFilterString();

            cursor = commonRepository().rawCustomQueryForAdapter(query);
            cursor.moveToFirst();
            clientAdapter.setTotalcount(cursor.getInt(0));
            Timber.v("total count here %d", clientAdapter.getTotalcount());

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);


        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}


