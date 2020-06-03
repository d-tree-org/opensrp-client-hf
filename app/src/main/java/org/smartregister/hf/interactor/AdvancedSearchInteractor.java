package org.smartregister.hf.interactor;

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.hf.contract.AdvancedSearchContract;
import org.smartregister.domain.Response;
import org.smartregister.family.util.AppExecutors;
import org.smartregister.hf.dao.FamilyDao;
import org.smartregister.hf.domain.Entity;
import org.smartregister.service.HTTPAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class AdvancedSearchInteractor implements AdvancedSearchContract.Interactor {
    private AppExecutors appExecutors;

    public AdvancedSearchInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    AdvancedSearchInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void search(final String searchText, final AdvancedSearchContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final List<Entity> response = localSearch(searchText);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onResultsFound(response);
                    }
                });
            }
        };

        appExecutors.networkIO().execute(runnable);
    }
    private List<Entity> localSearch(String searchText) {
        return FamilyDao.search(searchText);
    }
}
