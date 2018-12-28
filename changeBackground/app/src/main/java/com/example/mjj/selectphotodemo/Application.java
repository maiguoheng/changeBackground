package com.example.mjj.selectphotodemo;

import android.content.Context;

import com.example.mjj.selectphotodemo.dimens.utils.Density;

/**
 * Description :
 */
public class Application extends android.app.Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        Density.setDensity(this);
    }

    public static Context getContext() {
        return context;
    }

}
