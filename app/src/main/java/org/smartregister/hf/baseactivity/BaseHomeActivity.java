package org.smartregister.hf.baseactivity;

import org.smartregister.hf.contract.HomeContract;
import org.smartregister.view.activity.BaseRegisterActivity;

public abstract class BaseHomeActivity extends BaseRegisterActivity {

    public BaseHomeActivity(){}

    public HomeContract.Presenter getPresenter(){
        return (HomeContract.Presenter) this.presenter;
    }

}
