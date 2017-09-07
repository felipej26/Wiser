package br.com.wiser.features.splashscreen;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import br.com.wiser.AbstractActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.features.login.LoginActivity;
import br.com.wiser.interfaces.ICallback;

public class SplashScreenActivity extends AbstractActivity {

    private final long SPLASH_TIMEOUT = 1000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_splash_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sistema.getMinVersao(new ICallback() {
            @Override
            public void onSuccess() {
                try {
                    PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                    if (!checkVersoes(pInfo.versionName, Sistema.minVersao)) {
                        DialogInformar informar = new DialogInformar(getActivity());
                        informar.setMensagem("É necessário atualizar o app para continuar!");
                        informar.setOkClick(new IDialog() {
                            @Override
                            public void onClick() {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(getString(R.string.sistema_link_playstore)));
                                startActivity(i);
                            }
                        });
                        informar.show();
                    }
                    else {
                        inicializarSistema();
                    }
                }
                catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String mensagemErro) {

            }
        });
    }

    private boolean checkVersoes(String versaoSistema, String minVersao) {

        String versaoApp[] = versaoSistema.replace(".", ",").split(",");
        String versaoMin[] = minVersao.replace(".", ",").split(",");

        return versaoApp[0].trim().equals(versaoMin[0].trim()) &&
                versaoApp[1].trim().equals(versaoMin[1].trim());
    }

    private void inicializarSistema() {
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