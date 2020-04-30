package org.smartregister.hf.presenter;

import org.smartregister.hf.contract.FamilyCallDialogContract;
import org.smartregister.hf.interactor.FamilyCallDialogInteractor;

import java.lang.ref.WeakReference;

public class FamilyCallDialogPresenter implements FamilyCallDialogContract.Presenter {
    private WeakReference<FamilyCallDialogContract.View> mView;
    private FamilyCallDialogContract.Interactor mInteractor;

    public FamilyCallDialogPresenter(FamilyCallDialogContract.View view, String familyBaseEntityId) {
        mView = new WeakReference<>(view);
        mInteractor = new FamilyCallDialogInteractor(familyBaseEntityId);
        initalize();
    }

    @Override
    public void updateHeadOfFamily(FamilyCallDialogContract.Model model) {
        if (mView.get() != null) {
            mView.get().refreshHeadOfFamilyView(model);
        }
    }

    @Override
    public void updateCareGiver(FamilyCallDialogContract.Model model) {
        if (mView.get() != null) {
            mView.get().refreshCareGiverView(model);
        }
    }

    @Override
    public void initalize() {
        mView.get().refreshHeadOfFamilyView(null);
        mView.get().refreshCareGiverView(null);
        mInteractor.getHeadOfFamily(this, mView.get().getCurrentContext());
    }
}

