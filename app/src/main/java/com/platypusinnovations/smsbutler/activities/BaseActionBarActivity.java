package com.platypusinnovations.smsbutler.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.platypusinnovations.smsbutler.events.BusProvider;

public class BaseActionBarActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.instance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.instance().unregister(this);
    }
    
}
