package br.com.wiser.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import br.com.wiser.BuildConfig;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.dialogs.IDialog;

/**
 * Created by Jefferson on 16/09/2017.
 */

public class CheckPermissao {

    private final int REQUEST_PERMISSION = 1; // Valor deve ser maior ou igual a 0
    private String permissao;
    private String solicitarPermissao;

    public CheckPermissao(String permissao, String solicitarPermissao) {
        this.permissao = permissao;
        this.solicitarPermissao = solicitarPermissao;
    }

    public boolean checkPermissions(Context context) {
        int permissionState = ActivityCompat.checkSelfPermission(context, permissao.toString());
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissions(final Activity activity) {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(activity, permissao);

        if (shouldProvideRationale) {
            showRequestMessage(activity, new IDialog() {
                @Override
                public void onClick() {
                    startLocationPermissionRequest(activity);
                }
            });
        }
        else {
            startLocationPermissionRequest(activity);
        }
    }

    private void startLocationPermissionRequest(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[] {permissao}, REQUEST_PERMISSION);
    }

    public boolean onRequestPermissionsResult(final Activity activity, int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length <= 0) {
                Log.i(this.getClass().getSimpleName(), "User interaction was cancelled.");
            }
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else {
                showRequestMessage(activity, new IDialog() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                });
            }
        }

        return false;
    }

    private void showRequestMessage(Activity activity, IDialog listener) {
        DialogInformar informar = new DialogInformar(activity);
        informar.setMensagem(solicitarPermissao);
        informar.setOkClick(listener);
        informar.show();
    }
}
