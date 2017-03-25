package br.com.wiser.views.splashscreen;

import android.os.Bundle;

import br.com.wiser.R;
import br.com.wiser.features.splashscreen.SplashScreenPresenter;
import br.com.wiser.views.AbstractActivity;

public class SplashScreenActivity extends AbstractActivity implements ISplashScreenView {

    private SplashScreenPresenter splashScreenPresenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_splash_screen);

        splashScreenPresenter = new SplashScreenPresenter();
        splashScreenPresenter.onCreate(this);
    }
}