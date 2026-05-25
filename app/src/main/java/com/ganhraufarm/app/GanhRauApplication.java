package com.ganhraufarm.app;

import android.app.Application;
import android.util.Log;

public class GanhRauApplication extends Application {
    private static final String TAG = "GanhRauApp";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");
    }
}
