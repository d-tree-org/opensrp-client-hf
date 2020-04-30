package org.smartregister.hf.baseactivity;

import org.smartregister.hf.contract.AddoHomeContract;
import org.smartregister.view.activity.BaseRegisterActivity;

public abstract class BaseHomeActivity extends BaseRegisterActivity {

    public BaseHomeActivity(){}

    public AddoHomeContract.Presenter getPresenter(){
        return (AddoHomeContract.Presenter) this.presenter;
    }

}
