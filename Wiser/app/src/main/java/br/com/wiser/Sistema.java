package br.com.wiser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.facebook.AccessToken;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.com.wiser.facebook.AccessTokenModel;
import br.com.wiser.features.assunto.Assunto;
import br.com.wiser.features.login.LoginActivity;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.ArmazenarCache;
import br.com.wiser.utils.ComboBoxItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sistema {
    public static final String LOGOUT = "logout";
    public static final String CONVERSA = "conversa";
    public static final String CONTATO = "contato";
    public static final String DISCUSSAO = "discussao";

    public static String minVersao;
    private static String appLinguagem;

    private static Usuario usuario;
    private static AccessToken accessToken;
    private static List<ComboBoxItem> listaIdiomas;
    private static List<ComboBoxItem> listaFluencias;
    private static Set assuntos;

    private static ISistemaService service;

    public static final boolean PERMITIR_PAGINAS_NAO_VERIFICADAS = false;

    static {
        /* Carregar linguagem do celular */
        appLinguagem = WiserApplication.getAppContext().getResources().getConfiguration().locale.getLanguage();
        service = APIClient.getClient().create(ISistemaService.class);
    }

    public static Usuario getUsuario() {
        return (Usuario) get(usuario, R.string.preferences_usuario_key, Usuario.class);
    }

    public static void setUsuario(Usuario usuario) {
        Sistema.usuario = usuario;
        set(usuario, R.string.preferences_usuario_key);
    }

    public static AccessToken getAccessToken() {
        return (AccessToken) get(accessToken, R.string.preferences_accesstoken_key, AccessToken.class);
    }

    public static void setAccessToken(AccessToken accessToken) {
        Sistema.accessToken = accessToken;
        set(accessToken, R.string.preferences_accesstoken_key);
    }

    public static List<ComboBoxItem> getListaIdiomas() {
        Type listType = new TypeToken<List<ComboBoxItem>>(){}.getType();
        return (List<ComboBoxItem>) get(listaIdiomas, R.string.preferences_idiomas_key, listType);
    }

    public static void setListaIdiomas(List<ComboBoxItem> listaIdiomas) {
        Sistema.listaIdiomas = listaIdiomas;
        set(listaIdiomas, R.string.preferences_idiomas_key);
    }

    public static List<ComboBoxItem> getListaFluencias() {
        Type listType = new TypeToken<List<ComboBoxItem>>(){}.getType();
        return (List<ComboBoxItem>) get(listaFluencias, R.string.preferences_fluencias_key, listType);
    }

    public static void setListaFluencias(List<ComboBoxItem> listaFluencias) {
        Sistema.listaFluencias = listaFluencias;
        set(listaFluencias, R.string.preferences_fluencias_key);
    }

    public static Set<Assunto> getAssuntos() {
        Type setType = new TypeToken<Set<Assunto>>(){}.getType();
        return (Set<Assunto>) get(assuntos, R.string.preferences_assuntos_key, setType);
    }

    public static void setAssuntos(Set<Assunto> assuntos) {
        Sistema.assuntos = assuntos;
        set(assuntos, R.string.preferences_assuntos_key);
    }

    private static Object get(Object object, int preferencesKey, Type typeClass) {
        if (object != null) {
            return object;
        }
        else {
            ArmazenarCache armazenarCache = new ArmazenarCache(typeClass);
            return armazenarCache.getObjeto(preferencesKey);
        }
    }

    private static void set(Object object, int preferencesKey) {
        ArmazenarCache armazenarCache = new ArmazenarCache();
        armazenarCache.salvarObjeto(object, preferencesKey);
    }

    public static void getMinVersao(final ICallback callback) {
        Call<Versao> call = service.getMinVersao();
        call.enqueue(new Callback<Versao>() {
            @Override
            public void onResponse(Call<Versao> call, Response<Versao> response) {
                if (response.isSuccessful()) {
                    minVersao = response.body().getMinVersao();
                    callback.onSuccess();
                }
                else {
                    callback.onError(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Versao> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public static void inicializarSistema(final ICallback callback) {

        try {
            /* TODO Carregar de uma unica vez todos os parametros ao inves de um de cada vez */
            carregarAccessToken(new ICallback() {
                @Override
                public void onSuccess() {
                    carregarListaIdiomas(new ICallback() {
                        @Override
                        public void onSuccess() {
                            carregarListaFluencias(new ICallback() {
                                @Override
                                public void onSuccess() {
                                    carregarListaAssuntos(new ICallback() {
                                        @Override
                                        public void onSuccess() {
                                            callback.onSuccess();
                                        }

                                        @Override
                                        public void onError(String mensagemErro) { }
                                    });
                                }

                                @Override
                                public void onError(String mensagemErro) { }
                            });
                        }

                        @Override
                        public void onError(String mensagemErro) { }
                    });
                }

                @Override
                public void onError(String mensagemErro) { }
            });
        }
        catch (Exception e) {
            Log.e("Inicializar Sistema", e.getMessage());
            callback.onError(e.getMessage());
        }
    }

    private static void carregarAccessToken(final ICallback callback) {
        Call<AccessTokenModel> call = service.carregarAccessToken();
        call.enqueue(new Callback<AccessTokenModel>() {
            @Override
            public void onResponse(Call<AccessTokenModel> call, Response<AccessTokenModel> response) {
                if (response.isSuccessful()) {
                    AccessTokenModel model = response.body();

                    setAccessToken(new AccessToken(model.getAccessToken(), model.getApplicationID(),
                            model.getUserID(), Arrays.asList(model.getPermissions()), null, null, null, null));

                    Log.i("Serviço", "Carregou Access Token");
                    callback.onSuccess();
                }
                else {
                    Log.e("Inicializar Sistema", "Erro ao carregar o Access Token. Erro: " + response.message());
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenModel> call, Throwable t) {
                Log.e("Inicializar Sistema", "Erro ao carregar o Access Token", t);
                callback.onError(t.getMessage());
            }
        });
    }

    private static void carregarListaIdiomas(final ICallback callback) {
        listaIdiomas = new LinkedList();

        Call<LinkedList<ComboBoxItem>> call = service.carregarIdiomas(appLinguagem, true);
        call.enqueue(new Callback<LinkedList<ComboBoxItem>>() {
            @Override
            public void onResponse(Call<LinkedList<ComboBoxItem>> call, Response<LinkedList<ComboBoxItem>> response) {
                if (response.isSuccessful()) {
                    setListaIdiomas(response.body());

                    Log.i("Serviço", "Carregou Lista de Idiomas");
                    callback.onSuccess();
                }
                else {
                    Log.e("Inicializar Sistema", "Erro ao carregar a Lista de Idiomas. Erro: " + response.message());
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<LinkedList<ComboBoxItem>> call, Throwable t) {
                Log.e("Inicializar Sistema", "Erro ao carregar a Lista de Idiomas", t);
                callback.onError(t.getMessage());
            }
        });
    }

    private static void carregarListaFluencias(final ICallback callback) {
        listaFluencias = new LinkedList();

        Call<LinkedList<ComboBoxItem>> call = service.carregarFluencias(appLinguagem, true);
        call.enqueue(new Callback<LinkedList<ComboBoxItem>>() {
            @Override
            public void onResponse(Call<LinkedList<ComboBoxItem>> call, Response<LinkedList<ComboBoxItem>> response) {
                if (response.isSuccessful()) {
                    setListaFluencias(response.body());

                    Log.i("Serviço", "Carregou Lista de Fluencias");
                    callback.onSuccess();
                }
                else {
                    Log.e("Inicializar Sistema", "Erro ao carregar a Lista de Fluencias. Erro: " + response.message());
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<LinkedList<ComboBoxItem>> call, Throwable t) {
                Log.e("Inicializar Sistema", "Erro ao carregar a Lista de Fluencias", t);
                callback.onError(t.getMessage());
            }
        });
    }

    private static void carregarListaAssuntos(final ICallback callback) {
        assuntos = new HashSet();

        Call<HashSet<Assunto>> call = service.carregarAssuntos(appLinguagem);
        call.enqueue(new Callback<HashSet<Assunto>>() {
            @Override
            public void onResponse(Call<HashSet<Assunto>> call, Response<HashSet<Assunto>> response) {
                if (response.isSuccessful()) {
                    setAssuntos(response.body());

                    Log.i("Serviço", "Carregou Lista de Assuntos");
                    callback.onSuccess();
                }
                else {
                    Log.e("Inicializar Sistema", "Erro ao carregar a Lista de Assuntos. Erro: " + response.message());
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<HashSet<Assunto>> call, Throwable t) {
                Log.e("Inicializar Sistema", "Erro ao carregar a Lista de Assuntos", t);
                callback.onError(t.getMessage());
            }
        });
    }

    public static void logout(Activity activity) {
        setUsuario(null);

        Intent i = new Intent(activity, LoginActivity.class);

        i.putExtra(LOGOUT, true);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();
    }

    public static int getIDComboBox(Spinner cmb) {
        return ((ComboBoxItem)cmb.getItemAtPosition(cmb.getSelectedItemPosition())).getId();
    }

    public static int getPosicaoItemComboBox(Spinner cmb, int id) {
        for (int i = 0; i < cmb.getAdapter().getCount(); i++) {
            if (((ComboBoxItem) cmb.getItemAtPosition(i)).getId() == id) {
                return i;
            }
        }
        return 0;
    }

    public static String getDescricaoIdioma(int id) {
        for (ComboBoxItem item : getListaIdiomas()) {
            if (item.getId() == id) {
                return item.getDescricao();
            }
        }

        return "";
    }

    public static String getDescricaoFluencia(int id) {
        for (ComboBoxItem item : getListaFluencias()) {
            if (item.getId() == id) {
                return item.getDescricao();
            }
        }

        return "";
    }

    public static void carregarComboIdiomas(Spinner cmbIdioma, Context context) {
        carregarCombo(getListaIdiomas(), cmbIdioma, context);
    }

    public static void carregarComboFluencia(Spinner cmbFluencia, Context context) {
        carregarCombo(getListaFluencias(), cmbFluencia, context);
    }

    private static void carregarCombo(List<ComboBoxItem> itens, Spinner cmb, Context context) {
        ArrayAdapter<ComboBoxItem> dataAdapter = new ArrayAdapter<>(context, R.layout.frm_spinner_text, itens);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmb.setAdapter(dataAdapter);
    }
}