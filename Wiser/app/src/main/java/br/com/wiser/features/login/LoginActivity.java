package br.com.wiser.features.login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import br.com.wiser.BuildConfig;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.services.CarregarConversasService;
import br.com.wiser.views.principal.PrincipalActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    private LoginPresenter loginPresenter;
    private Intent carregarConversasServices;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private Facebook facebook;

    private final int REQUEST_LOCATION_PERMISSION = 1; // Valor deve ser maior ou igual a 0

    @BindView(R.id.btnLogin) Button btnLogin;
    @BindView(R.id.snackbar_view) View snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_login);

        loginPresenter = new LoginPresenter();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        facebook = new Facebook(getFacebookCallback());

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
        if (!checkPermissions()) {
            requestPermissions();
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

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length <= 0) {
                Log.i(this.getClass().getSimpleName(), "User interaction was cancelled.");
            }
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onLoginSuccess();
            }
            else {
                showSnackBar(R.string.necessario_permitir, R.string.app_configuracoes_title,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
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

    private void showSnackBar(final int mainTextStringId, final int actionStringId, final View.OnClickListener listener) {
        /*Snackbar snack = Snackbar.make(snackbar, getString(mainTextStringId), Snackbar.LENGTH_INDEFINITE);
        snack.setAction(actionStringId, listener);
        snack.show();
        */

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(getString(mainTextStringId));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onClick(null);
            }
        });
        builder.show();
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

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            showSnackBar(R.string.necessario_permitir, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startLocationPermissionRequest();
                        }
                    });
        }
        else {
            startLocationPermissionRequest();
        }
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this,
                new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
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
        facebook.login(this);
    }
}
