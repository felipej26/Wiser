package br.com.wiser.features.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.features.principal.PrincipalActivity;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.services.CarregarConversasService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    private LoginPresenter loginPresenter;
    private Intent carregarConversasServices;

    private Facebook facebook;

    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.snackbar_view) View snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginPresenter = new LoginPresenter();

        facebook = new Facebook(getFacebookCallback());

        if (!getIntent().getBooleanExtra(Sistema.LOGOUT, false)) {
            if (facebook.isLogado()) {
                onLoginSuccess();
                return;
            }
        }
        else {
            getIntent().removeExtra(Sistema.LOGOUT);
            logout();
        }

        setContentView(R.layout.app_login);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebook.callbackManager(requestCode, resultCode, data);
    }

    private FacebookCallback getFacebookCallback() {
        return new FacebookCallback() {
            @Override
            public void onSuccess(Object o) {
                onLoginSuccess();
                Log.i("Facebook", "onSuccess");
            }

            @Override
            public void onCancel() {
                Log.i("Facebook", "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("Facebook", "onError");
            }
        };
    }

    private void onLoginSuccess() {
        loginPresenter.gravarLogin(new ICallback() {
            @Override
            public void onSuccess() {
                startService();
                startPrincipalActivity();
            }

            @Override
            public void onError(String mensagemErro) {
                Toast.makeText(LoginActivity.this, getString(R.string.falha_login), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logout() {
        stopService();
        facebook.logout();
    }

    private void startService() {
        carregarConversasServices = new Intent(this, CarregarConversasService.class);
        startService(carregarConversasServices);
    }

    private void stopService() {
        if (carregarConversasServices != null) {
            stopService(carregarConversasServices);
        }
    }

    private void startPrincipalActivity() {
        Intent i = new Intent(this, PrincipalActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.btnLogin)
    public void onLoginClicked() {
        facebook.login(this);
    }
}
