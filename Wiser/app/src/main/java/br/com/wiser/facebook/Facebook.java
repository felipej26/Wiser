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
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestBatch;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.models.assunto.Pagina;
import br.com.wiser.models.usuario.Perfil;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.utils.UtilsDate;

/**
 * Created by Jefferson on 03/04/2016.
 */
public class Facebook {
    private Context context;
    private static CallbackManager callbackManager;

    private static String nomeIndisponivel = "";

    public Facebook(Context context) {
        this.context = context;

        try {
            nomeIndisponivel = context.getResources().getString(R.string.usuario);
        }
        catch (Exception e) {
            nomeIndisponivel = "Usuário";
        }

        getSignatures();

        if (callbackManager == null) {
            callbackManager = CallbackManager.Factory.create();

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
        LoginManager.getInstance().logInWithReadPermissions(activity, Sistema.getAccessToken().getPermissions());
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

    private void requestBatch(Collection<GraphRequest> graphRequests, final ICallback callback) {

        GraphRequestBatch batch = new GraphRequestBatch(graphRequests);
        batch.addCallback(new GraphRequestBatch.Callback() {
            @Override
            public void onBatchCompleted(GraphRequestBatch batch) {
                callback.onSuccess();
            }
        });
        batch.executeAsync();
    }

    public AccessToken getAccessToken() {
        try {
            return AccessToken.getCurrentAccessToken();
        }
        catch (Exception e) {
            return null;
        }
    }

    public void carregarPerfil(final Usuario usuario, final ICallback callback) {

        usuario.setPerfil(new Perfil());

        new Thread(new Runnable() {
            @Override
            public void run() {
                GraphRequest graphRequest = getGraphRequestCarregarPerfil(usuario, new ICallback() {
                    @Override
                    public void onSuccess() {
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onError(String mensagemErro) {
                        if (callback != null) {
                            callback.onError(mensagemErro);
                        }
                    }
                });
                graphRequest.executeAndWait();
            }
        }).start();
    }

    public void carregarUsuarios(Collection<Usuario> listaUsuarios, ICallback callback) {

        List<GraphRequest> listaRequest = new ArrayList<>();

        if (listaUsuarios.size() == 0) {
            callback.onSuccess();
            return;
        }

        for (Usuario usuario : listaUsuarios) {
            if (usuario.getPerfil() == null) {
                usuario.setPerfil(new Perfil());
            }

            if (!usuario.isPerfilLoaded()) {
                listaRequest.add(getGraphRequestCarregarPerfil(usuario, null));
            }
        }

        if (listaRequest.size() == 0) {
            callback.onSuccess();
        }
        else {
            requestBatch(listaRequest, callback);
        }
    }

    public void carregarPaginasEmComum(final long destinatario, final ICallbackPaginas callbackPaginas) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                carregarContextID(destinatario, new ICallbackContextID() {
                    @Override
                    public void onSuccess(String userContextID) {
                        carregarPaginasEmComum(userContextID, callbackPaginas);
                    }

                    @Override
                    public void onError(String mensagemErro) {

                    }
                });
            }
        }).start();
    }

    private void carregarPaginasEmComum(final String userContextID, final ICallbackPaginas callbackPaginas) {
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

    private void carregarContextID(Long destinatario, final ICallbackContextID callback) {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                createAccessToken(Sistema.getUsuario()),
                "/" + Sistema.getListaUsuarios().get(destinatario).getFacebookID(),
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

    private GraphRequest getGraphRequestCarregarPerfil(final Usuario usuario, final ICallback callback) {
        Bundle parametros = new Bundle();
        AccessToken accessToken;
        String node;

        parametros.putString("fields", "first_name,name,picture.width(1000).height(1000){url},birthday,age_range");

        if (usuario.getAccessToken() == null) {
            parametros.putString("ids", usuario.getFacebookID());

            accessToken = Sistema.getAccessToken();
            node = "/";
        }
        else {
            accessToken = createAccessToken(usuario);
            node = "/" + usuario.getFacebookID();
        }

        return new GraphRequest(
                accessToken, node, parametros, HttpMethod.GET,
                new GraphRequest.Callback() {

                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Perfil perfil = new Perfil();
                        perfil.setFirstName(nomeIndisponivel);
                        perfil.setFullName(nomeIndisponivel);

                        if (graphResponse != null) {
                            if (graphResponse.getError() != null) {

                                /* Significa que o erro pode ter sido a data de expiração do Access Token */
                                if (usuario.getAccessToken() != null) {
                                    refazerCarregarPerfil(usuario, callback);
                                    return;
                                }
                            }

                            try {
                                JSONObject json = graphResponse.getJSONObject();

                                if (json != null) {
                                    if (usuario.getAccessToken() == null) {
                                        json = json.getJSONObject(usuario.getFacebookID());
                                    }

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
                                    } else {
                                        if (json.has("age_range"))
                                            perfil.setIdade(json.getJSONObject("age_range").getInt("min"));
                                    }
                                }
                            }
                            catch (Exception e) {
                                Log.e("Facebook", "Erro ao carregar o Perfil do Usuario: " + usuario.getUserID(), e);
                            }

                            usuario.setPerfilLoaded(true);
                            usuario.setPerfil(perfil);

                            if (callback != null) {
                                callback.onSuccess();
                            }
                        }
                    }
                });
    }

    private void refazerCarregarPerfil(Usuario usuario, ICallback callback) {
        usuario.setAccessToken(null);
        carregarPerfil(usuario, callback);
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

    private interface ICallbackContextID {
        void onSuccess(String userContextID);
        void onError(String mensagemErro);
    }
}
