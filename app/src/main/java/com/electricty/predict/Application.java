package com.electricty.predict;
import android.content.Context;

import androidx.multidex.MultiDex;


public class Application extends android.app.Application {
     private static Application mInstance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;




    }
    public static synchronized Application getInstance() {
        return mInstance;
    }





}