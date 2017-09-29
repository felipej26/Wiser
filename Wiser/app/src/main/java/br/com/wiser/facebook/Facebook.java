package br.com.wiser.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.WiserApplication;
import br.com.wiser.features.assunto.Pagina;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.utils.UtilsDate;

/**
 * Created by Jefferson on 03/04/2016.
 */
public class Facebook {

    public interface ICallbackProfileInfo {
        void onCompleted(String nome, String primeiroNome, Date dataNascimento);
    }

    private interface ICallbackContextID {
        void onSuccess(String userContextID);
        void onError(String error);
    }

    private Context context;
    private static CallbackManager callbackManager;

    private static String nomeIndisponivel = "";

    public Facebook() {
        this.context = WiserApplication.getAppContext();

        try {
            nomeIndisponivel = context.getResources().getString(R.string.destinatario);
        }
        catch (Exception e) {
            nomeIndisponivel = "Usuário";
        }

        getSignatures();

        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();
        }
    }

    public Facebook(FacebookCallback callback) {
        this();
        LoginManager.getInstance().registerCallback(callbackManager, callback);
    }

    private void getSignatures() {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);

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

    public void login(Activity activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile,user_friends,user_likes,user_birthday"));
    }

    /**
     * Funciona apenas para requisições de paginas em comum!
     * @param usuario
     * @param node
     * @param parametros
     * @param callback
     */
    private void requestCallback(final Usuario usuario, final String node, final Bundle parametros, final GraphCallback callback) {
        final GraphRequest.Callback graphCallback = new GraphRequest.Callback() {

            @Override
            public void onCompleted(GraphResponse response) {
                callback.setResponse(response);

                JSONObject nextRequest = response.getJSONObject().optJSONObject("context").optJSONObject("mutual_likes").optJSONObject("paging");
                if (nextRequest != null) {
                    try {
                        String link = new URL(nextRequest.getString("next")).toString();
                        if (link != null) {
                            new GraphRequest(createAccessToken(usuario), link, null, HttpMethod.GET, this).executeAndWait();
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    callback.setResponse(null);
                }
            }
        };

        new Thread() {
            public void run() {
                new GraphRequest(createAccessToken(usuario),
                        "/" + node, parametros, HttpMethod.GET,
                        graphCallback).executeAndWait();
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

    public void getProfile(final ICallbackProfileInfo callbackProfileInfo) {
        GraphRequest request = GraphRequest.newMeRequest(
                getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        try {
                            int anosParaRemover = 18;
                            Date dataNascimento;
                            if (json.has("birthday") && json.optString("birthday").length() == 10) {
                                dataNascimento = UtilsDate.parseDate(json.getString("birthday"), UtilsDate.MMDDYYYY);
                            } else {
                                if (json.has("age_range")) {
                                    anosParaRemover = json.getJSONObject("age_range").getInt("min");
                                }

                                GregorianCalendar gc = new GregorianCalendar();
                                gc.setTime(new Date());
                                gc.set(Calendar.YEAR, gc.get(Calendar.YEAR) - anosParaRemover);
                                dataNascimento = gc.getTime();
                            }

                            callbackProfileInfo.onCompleted(
                                    json.getString("name"),
                                    json.getString("first_name"),
                                    dataNascimento
                            );
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,first_name,birthday,age_range");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void carregarPaginasEmComum(String facebookIDDestinarario, final ICallbackPaginas callback) {
        carregarContextID(facebookIDDestinarario, new ICallbackContextID() {
            @Override
            public void onSuccess(String userContextID) {
                carregarMutualLikes(userContextID, callback);
            }

            @Override
            public void onError(String error) {
                Log.e("Facebook", "Erro ao carregar paginas em comum. Erro: " + error);
            }
        });
    }

    private void carregarContextID(String facebookIDDestinarario, final ICallbackContextID callback) {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                createAccessToken(Sistema.getUsuario()),
                "/" + facebookIDDestinarario,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() == null) {
                            try {
                                callback.onSuccess(response.getJSONObject()
                                        .getJSONObject("context")
                                        .getString("id"));
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            callback.onError(response.getError().getErrorMessage());
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "context.fields(id)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void carregarMutualLikes(final String userContextID, final ICallbackPaginas callbackPaginas) {
        final HashSet<Pagina> paginas = new HashSet<>();
        final Bundle parametros = new Bundle();

        final GraphRequest.Callback graphCallback = new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                JSONArray json;

                try {
                    if (graphResponse != null) {
                        if (graphResponse.getError() != null) {
                            Log.e("Facebook", "Erro ao carregar as Paginas em Comum.", graphResponse.getError().getException());
                            callbackPaginas.setResponse(paginas);
                            return;
                        }

                        json = graphResponse.getJSONObject()
                                .getJSONArray("data");

                        if (json != null) {
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jsonPagina = json.getJSONObject(i);

                                if (Sistema.PERMITIR_PAGINAS_NAO_VERIFICADAS || jsonPagina.getBoolean("is_verified")) {
                                    Pagina pagina = new Pagina();
                                    pagina.setNome(jsonPagina.getString("name"));
                                    pagina.setCategoria(jsonPagina.getString("category"));
                                    pagina.setVerificada(jsonPagina.getBoolean("is_verified"));

                                    paginas.add(pagina);
                                }
                            }
                        }
                    }

                    GraphRequest nextRequest = graphResponse.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
                    if (nextRequest != null) {
                        nextRequest.setCallback(this);
                        nextRequest.executeAndWait();
                    }
                    else {
                        callbackPaginas.setResponse(paginas);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        parametros.putString("fields", "category,name,id,is_verified");

        new Thread(new Runnable() {
            @Override
            public void run() {
                new GraphRequest(createAccessToken(Sistema.getUsuario()),
                        "/" + userContextID + "/mutual_likes",
                        parametros,
                        HttpMethod.GET,
                        graphCallback).executeAndWait();
            }
        }).start();
    }

    private static AccessToken createAccessToken(Usuario usuario) {
        AccessToken mAccessToken = null;

        try {
            mAccessToken = new AccessToken(usuario.getAccessToken(),
                    Sistema.getAccessToken().getApplicationId(),
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
