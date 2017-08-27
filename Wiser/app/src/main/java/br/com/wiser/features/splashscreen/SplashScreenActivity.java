package br.com.wiser.features.splashscreen;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.login.LoginActivity;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.views.AbstractActivity;

public class SplashScreenActivity extends AbstractActivity {

    private final long SPLASH_TIMEOUT = 1000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_splash_screen);

        Sistema.inicializarSistema(this, new ICallback() {

            @Override
            public void onSuccess() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startLoginActivity();
                    }
                }, SPLASH_TIMEOUT);
            }

            @Override
            public void onError(String mensagemErro) {
                showToast(getString(R.string.app_servidor_manutencao));
            }
        });
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}