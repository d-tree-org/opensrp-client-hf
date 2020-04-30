package org.smartregister.hf.form_data;

import android.content.Context;

import org.json.JSONObject;

public interface FormLoader {

    JSONObject getJsonForm(Context context, String fileName) throws Exception;

}
