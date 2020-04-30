package org.smartregister.hf.model;

import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.family.model.BaseFamilyRegisterFramentModel;
import org.smartregister.family.util.ConfigHelper;
import org.smartregister.family.util.Utils;

public class FamilyRegisterFragmentModel extends BaseFamilyRegisterFramentModel {

    @Override
    public RegisterConfiguration defaultRegisterConfiguration() {
        return ConfigHelper.defaultRegisterConfiguration(Utils.context().applicationContext());
    }
}
