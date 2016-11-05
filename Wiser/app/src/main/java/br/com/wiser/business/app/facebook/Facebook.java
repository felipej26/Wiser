package br.com.wiser.business.app.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

import android.text.format.DateUtils;
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
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import br.com.wiser.Sistema;
import br.com.wiser.R;
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
                public void onSuccess(LoginResult loginResult) {
                }

                @Override
                public void onCancel() {
                }

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

    private JSONObject request(final Usuario usuario, final String node, final Bundle parametros) {
        final JSONObject[] json = {new JSONObject()};
        final boolean[] requisitando = {true};

        new Thread() {
            public void run() {
                new GraphRequest(createAccessToken(usuario),
                        "/" + usuario.getFacebookID() + "/" + node, parametros, HttpMethod.GET,
                        new GraphRequest.Callback() {

                            @Override
                            public void onCompleted(GraphResponse graphResponse) {
                                if (graphResponse != null) {
                                    json[0] = graphResponse.getJSONObject();
                                }
                                requisitando[0] = false;
                            }

                        }).executeAndWait();
            }
        }.start();

        while (requisitando[0]);

        return json[0];
    }

    private JSONObject requestViaAppAccessToken(final String node, final Bundle parametros) {
        final JSONObject[] json = {new JSONObject()};
        final boolean[] requisitando = {true};

        new Thread() {
            public void run() {
                new GraphRequest(Sistema.ACCESS_TOKEN, "/" + node, parametros, HttpMethod.GET,
                        new GraphRequest.Callback() {

                            @Override
                            public void onCompleted(GraphResponse graphResponse) {
                                if (graphResponse != null) {
                                    json[0] = graphResponse.getJSONObject();
                                }
                                requisitando[0] = false;
                            }

                        }).executeAndWait();
            }
        }.start();

        while (requisitando[0]);

        return json[0];
    }

    public AccessToken getAccessToken() {
        try {
            return AccessToken.getCurrentAccessToken();
        }
        catch (Exception e) {
            return null;
        }
    }

    public void getProfile(Usuario usuario) {
        Bundle parametros = new Bundle();

        try {
            usuario.setFirstName(nomeIndisponivel);
            usuario.setFullName(nomeIndisponivel);

            /* TODO
                Fazer com que informações como first_name, name, picture sejam pegas com o App Access Token
                E informações como birthday venham com o User Access Token
             */

            JSONObject json;

            if (usuario.getAccessToken().equals("") || usuario.getAccessToken().equals("null")) {
                parametros.putString("ids", usuario.getFacebookID());
                parametros.putString("fields", "first_name,name,picture.width(1000).height(1000){url},age_range");

                json = requestViaAppAccessToken("", parametros);
                json = json.getJSONObject(usuario.getFacebookID());
            }
            else {
                parametros.putString("fields", "first_name,name,picture.width(1000).height(1000){url},birthday,age_range");

                json = request(usuario, "", parametros);
            }

            if (json != null) {
                if (json.has("first_name"))
                    usuario.setFirstName(json.getString("first_name"));

                if (json.has("name"))
                    usuario.setFullName(json.getString("name"));

                if (json.has("picture"))
                    usuario.setUrlProfilePicture(json.getJSONObject("picture")
                        .getJSONObject("data").optString("url"));


                // TODO colocar o Age_Range caso não tenha o Birthday ou não tenha o ano do Birthday,
                if (json.has("birthday")) {
                    long diferenca = new Date().getTime() - UtilsDate.parseDate(json.getString("birthday"), UtilsDate.MMDDYYYY).getTime();
                    usuario.setIdade((int) Math.floor(diferenca / 86400000 / 365));
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public LinkedList<Usuario> getProfiles(final LinkedList<Usuario> usuarios) {
        String listUsersID = "";

        for (Usuario c : usuarios) {
            c.setFullName(nomeIndisponivel);
            c.setFirstName(nomeIndisponivel);

            listUsersID += c.getFacebookID() + ",";
        }
        listUsersID = listUsersID.substring(0, listUsersID.length() - 1);

        Bundle parametros = new Bundle();
        parametros.putString("ids", listUsersID);
        parametros.putString("fields", "name,first_name,picture.width(1000).height(1000){url},birthday");

        try {
            JSONObject jsonUsuarios = null; //request("", parametros);
            JSONObject json;

            if (jsonUsuarios != null) {
                for (Usuario c : usuarios) {
                    if (jsonUsuarios.has(c.getFacebookID())) {
                        json = jsonUsuarios.getJSONObject(c.getFacebookID());

                        if (json.has("name")) {
                            c.setFullName(json.getString("name"));
                        }


                        if (json.has("first_name")) {
                            c.setFirstName(json.getString("first_name"));
                        }

                        if (json.has("picture")) {
                            c.setUrlProfilePicture(json
                                    .getJSONObject("picture")
                                    .getJSONObject("data")
                                    .optString("url"));
                        }

                        if (json.has("birthday")) {
                            long diferenca = new Date().getTime() - UtilsDate.parseDate(json.getString("birthday"), UtilsDate.MMDDYYYY).getTime();
                            c.setIdade((int) Math.floor(diferenca / 86400000 / 365));
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return usuarios;
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
