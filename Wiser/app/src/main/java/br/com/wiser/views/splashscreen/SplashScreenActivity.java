package br.com.wiser.views.splashscreen;

import android.content.Intent;
import android.os.Bundle;

import br.com.wiser.R;
import br.com.wiser.views.login.LoginActivity;
import br.com.wiser.presenters.splashscreen.SplashScreenPresenter;
import br.com.wiser.views.AbstractActivity;

public class SplashScreenActivity extends AbstractActivity implements ISplashScreenView {

    private SplashScreenPresenter splashScreenPresenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_splash_screen);

        splashScreenPresenter = new SplashScreenPresenter();
        splashScreenPresenter.onCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        splashScreenPresenter.onResume();
    }
}