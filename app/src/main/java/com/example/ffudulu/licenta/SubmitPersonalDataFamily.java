package com.example.ffudulu.licenta;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class SubmitPersonalDataFamily extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_submit_personal_data_family);
    }
}
