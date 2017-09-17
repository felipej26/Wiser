package br.com.wiser.features.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.features.principal.PrincipalActivity;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.services.CarregarConversasService;
import br.com.wiser.utils.CheckPermissao;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    private LoginPresenter loginPresenter;
    private Intent carregarConversasServices;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Facebook facebook;
    private CheckPermissao checkPermissaoLocalizacao;

    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.snackbar_view) View snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_login);

        loginPresenter = new LoginPresenter();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        facebook = new Facebook(getFacebookCallback());
        checkPermissaoLocalizacao = new CheckPermissao(Manifest.permission.ACCESS_COARSE_LOCATION,
                getString(R.string.solicitar_permissao_localizacao));

        if (getIntent().getBooleanExtra(Sistema.LOGOUT, false)) {
            getIntent().removeExtra(Sistema.LOGOUT);
            logout();
        }

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (facebook.isLogado()) {
            checkPermissionAndLogin();
        }
    }

    private void checkPermissionAndLogin() {
        if (!checkPermissaoLocalizacao.checkPermissions(this)) {
            checkPermissaoLocalizacao.requestPermissions(this);
        } else {
            onLoginSuccess();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebook.callbackManager(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (checkPermissaoLocalizacao.onRequestPermissionsResult(this, requestCode, permissions, grantResults)) {
            onLoginSuccess();
        }
    }

    private FacebookCallback getFacebookCallback() {
        return new FacebookCallback() {
            @Override
            public void onSuccess(Object o) {
                checkPermissionAndLogin();
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

    @SuppressWarnings("MissingPermission")
    private void updateLocation(final ICallback callback) {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            loginPresenter.gravarLogin(location.getLatitude(), location.getLongitude(), callback);
                        }
                    }
                });
    }

    private void onLoginSuccess() {
        updateLocation(new ICallback() {
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
        if (!checkPermissaoLocalizacao.checkPermissions(this)) {
            checkPermissaoLocalizacao.requestPermissions(this);
        }
        else {
            facebook.login(this);
        }
    }
}
