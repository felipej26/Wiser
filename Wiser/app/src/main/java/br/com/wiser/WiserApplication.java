package br.com.wiser;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

/**
 * Created by Jefferson on 08/07/2017.
 */
public class WiserApplication extends Application {

  private static WiserApplication application;

  @Override public void onCreate() {
    super.onCreate();
    application = this;

    if (!BuildConfig.DEBUG) {
      Fabric.with(this, new Crashlytics(), new Answers());
    }

    Realm.init(this);
  }

  public static Context getAppContext() {
    return application;
  }
}
