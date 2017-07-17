package br.com.wiser;

import android.app.Application;
import android.content.Context;

/**
 * Created by Jefferson on 08/07/2017.
 */
public class WiserApplication extends Application {

    private static WiserApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }


    public static Context getAppContext() {
        return application;
    }
}
