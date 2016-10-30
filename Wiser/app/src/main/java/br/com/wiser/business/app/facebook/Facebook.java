package br.com.wiser.business.app.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Date;

import br.com.wiser.Sistema;
import br.com.wiser.R;
import br.com.wiser.business.app.perfil.Perfil;
import br.com.wiser.business.app.usuario.Usuario;
import br.com.wiser.utils.UtilsDate;

/**
 * Created by Jefferson on 03/04/2016.
 */
public class Facebook {
    private Context contexto;
    private static CallbackManager callbackManager;

    private String nomeIndisponivel = "";

    public Facebook(Context contexto) {
        this.contexto = contexto;

        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(contexto);
            getSignatures();
        }

        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }

        while (!FacebookSdk.isInitialized());

        nomeIndisponivel = contexto.getResources().getString(R.string.usuario);
    }

    private void getSignatures() {
        try {
            PackageInfo info = contexto.getPackageManager().getPackageInfo(
                    contexto.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callbackManager(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void setBtnLogin(final Activity loginActivity, Button loginButton) {
        Button btnLogin = loginButton;

        try {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) { }

                @Override
                public void onCancel() { }

                @Override
                public void onError(FacebookException error) {
                    error.printStackTrace();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(loginActivity, Sistema.ACCESS_TOKEN.getPermissions());
            }
        });
    }

    private void request(final Usuario usuario, final String node, final Bundle parametros, final ICallback callback) {

        new Thread() {
            public void run() {
                new GraphRequest(createAccessToken(usuario),
                        "/" + usuario.getFacebookID() + "/" + node, parametros, HttpMethod.GET,
                        new GraphRequest.Callback() {

                            @Override
                            public void onCompleted(GraphResponse graphResponse) {
                                if (graphResponse != null) {
                                    callback.setResponse(graphResponse);
                                }
                            }

                        }).executeAndWait();
            }
        }.start();
    }

    private void requestViaAppAccessToken(final String node, final Bundle parametros, final ICallback callback) {

        new Thread() {
            public void run() {
                new GraphRequest(Sistema.ACCESS_TOKEN, "/" + node, parametros, HttpMethod.GET,
                        new GraphRequest.Callback() {

                            @Override
                            public void onCompleted(GraphResponse graphResponse) {
                                if (graphResponse != null) {
                                    callback.setResponse(graphResponse);
                                }
                            }

                        }).executeAndWait();
            }
        }.start();
    }

    public AccessToken getAccessToken() {
        try {
            return AccessToken.getCurrentAccessToken();
        }
        catch (Exception e) {
            return null;
        }
    }

    public void carregarPerfil(final Usuario usuario) {
        Bundle parametros = new Bundle();
        final Perfil perfil = new Perfil();

        try {
            perfil.setFirstName(nomeIndisponivel);
            perfil.setFullName(nomeIndisponivel);

            ICallback callback = new ICallback() {
                @Override
                public void setResponse(GraphResponse response) {
                    JSONObject json = response.getJSONObject();

                    try {
                        if (json.has(usuario.getFacebookID()))
                            json = json.getJSONObject(usuario.getFacebookID());

                        if (json.has("first_name"))
                            perfil.setFirstName(json.getString("first_name"));

                        if (json.has("name"))
                            perfil.setFullName(json.getString("name"));

                        if (json.has("picture"))
                            perfil.setUrlProfilePicture(json.getJSONObject("picture")
                                    .getJSONObject("data").optString("url"));

                        if (json.has("birthday") && json.optString("birthday").length() == 10) {
                            long diferenca = new Date().getTime() - UtilsDate.parseDate(json.getString("birthday"), UtilsDate.MMDDYYYY).getTime();
                            perfil.setIdade((int) Math.floor(diferenca / 86400000 / 365));
                        }
                        else {
                            if (json.has("age_range"))
                                perfil.setIdade(json.getJSONObject("age_range").getInt("min"));
                        }

                        usuario.setPerfil(perfil);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };

            if (usuario.getAccessToken().equals("") || usuario.getAccessToken().equals("null")) {
                parametros.putString("ids", usuario.getFacebookID());
                parametros.putString("fields", "first_name,name,picture.width(1000).height(1000){url},age_range");

                requestViaAppAccessToken("", parametros, callback);
            }
            else {
                parametros.putString("fields", "first_name,name,picture.width(1000).height(1000){url},birthday,age_range");

                request(usuario, "", parametros, callback);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static AccessToken createAccessToken(Usuario usuario) {
        AccessToken mAccessToken = null;

        try {
            mAccessToken = new AccessToken(usuario.getAccessToken(),
                    Sistema.ACCESS_TOKEN.getApplicationId(),
                    usuario.getFacebookID(),
                    null, null, null, null, null);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return mAccessToken;
    }

    public boolean isLogado() {
        try {
            return AccessToken.getCurrentAccessToken() != null;
        }
        catch (Exception e) {
            return false;
        }
    }

    public void logout() {
        try {
            LoginManager.getInstance().logOut();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
