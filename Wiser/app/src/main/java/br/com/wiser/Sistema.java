package br.com.wiser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

import com.facebook.AccessToken;

import java.util.Date;
import java.util.HashSet;

import br.com.wiser.business.app.facebook.Facebook;
import br.com.wiser.business.app.servidor.Servidor;
import br.com.wiser.business.app.usuario.UsuarioDAO;
import br.com.wiser.business.chat.assunto.Assunto;
import br.com.wiser.utils.UtilsLocation;

public class Sistema {

    public static String SERVIDOR_WS = "nodejs-wiserserver.rhcloud.com";

    public static AccessToken ACCESS_TOKEN;
    public static String APP_LINGUAGEM;

    private static UsuarioDAO USUARIO;
    public static HashSet<Assunto> ASSUNTOS;

    public static final int PERMISSION_ALL = 0;

    public static boolean PERMITIR_PAGINAS_NAO_VERIFICADAS = false;

    public static boolean inicializar(Context context) {

        final Servidor.App servidorApp = new Servidor().new App();

        try {
            APP_LINGUAGEM = context.getResources().getConfiguration().locale.getLanguage();

            if (ACCESS_TOKEN == null) {
                ACCESS_TOKEN = servidorApp.getAccessToken();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ASSUNTOS = new HashSet<>();
                    servidorApp.getAssuntos(ASSUNTOS);
                }
            }).start();

            new Facebook(context);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean checkPermissoes(Context context) {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!hasPermissions(context, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) context, PERMISSIONS, PERMISSION_ALL);
            return false;
        }
        else {
            return true;
        }
    }

    private static boolean hasPermissions(Context context, String ... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        else {
            int status = 0;

            for (String permission : permissions) {
                status = context.getPackageManager().checkPermission(
                        permission, context.getPackageName());

                if (status != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean logar(Context context, boolean salvarLogin) {
        UsuarioDAO usuario = new UsuarioDAO(0);

        UtilsLocation.atualizarLocalizacao(context);
        UtilsLocation.atualizarCoordenadas(context);

        Facebook facebook = new Facebook(context);

        if (facebook.getAccessToken() == null) {
            return false;
        }

        usuario.setFacebookID(facebook.getAccessToken().getUserId());
        usuario.setAccessToken(facebook.getAccessToken().getToken());
        usuario.setDataUltimoAcesso(new Date());
        usuario.setLatitude(UtilsLocation.getLatitude());
        usuario.setLongitude(UtilsLocation.getLongitude());

        if (salvarLogin) {
            if (!usuario.salvarLogin(context)) {
                return false;
            }
        }

        facebook.carregarPerfil(usuario);
        Sistema.USUARIO = usuario;
        return true;
    }

    public static void logout(Context context) {
        new Facebook(context).logout();
        Sistema.USUARIO = null;
    }

    public static UsuarioDAO getUsuario(Context context) {
        if (USUARIO != null) {
            return USUARIO;
        }

        logar(context, false);
        return USUARIO;
    }
}