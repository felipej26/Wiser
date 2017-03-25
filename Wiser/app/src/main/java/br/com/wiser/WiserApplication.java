package br.com.wiser;

import android.app.Application;
import android.content.Context;

import br.com.wiser.features.login.DaggerLoginComponent;
import br.com.wiser.features.login.LoginComponent;

/**
 * Created by Jefferson on 06/03/2017.
 */
public class WiserApplication extends Application {
    private ApiComponent apiComponent;
    private LoginComponent loginComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        apiComponent = DaggerApiComponent.builder()
                .build();

        loginComponent = DaggerLoginComponent.builder()
                .apiModule(new ApiModule(this))
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public ApiComponent getApiComponent() {
        return apiComponent;
    }

    public LoginComponent getLoginComponent() {
        return loginComponent;
    }
}
