package br.com.wiser.features.splashscreen;

import android.app.Activity;
import android.content.Intent;

import java.util.Timer;
import java.util.TimerTask;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.features.login.LoginActivity;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.IView;

/**
 * Created by Jefferson on 22/01/2017.
 */
public class SplashScreenPresenter extends Presenter<IView> {

    private final int SPLASH_TIMEOUT = 1000;

    public void onCreate() {

        Sistema.inicializarSistema(getContext(), new ICallback() {

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
                view.showToast(view.getContext().getString(R.string.app_servidor_manutencao));
            }
        });
    }

    private void startLoginActivity() {
        getContext().startActivity(new Intent(getContext(), LoginActivity.class));

        ((Activity) getContext()).finish();
    }
}
