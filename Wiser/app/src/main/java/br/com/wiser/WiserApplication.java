package br.com.wiser;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Jefferson on 08/07/2017.
 */
public class WiserApplication extends Application {

    private static WiserApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());
        application = this;
    }


    public static Context getAppContext() {
        return application;
    }
}
