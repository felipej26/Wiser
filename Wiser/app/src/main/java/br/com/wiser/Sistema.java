package br.com.wiser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.facebook.AccessToken;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.com.wiser.facebook.AccessTokenModel;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.features.assunto.Assunto;
import br.com.wiser.features.login.LoginActivity;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.ComboBoxItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sistema {

    public static String SERVIDOR_WS;
    public static final boolean PERMITIR_PAGINAS_NAO_VERIFICADAS = false;

    public static final String LOGOUT = "logout";
    public static final String CONVERSA = "conversa";
    public static final String CONTATO = "contato";
    public static final String DISCUSSAO = "discussao";

    public static String minVersao;
    private static AccessToken accessToken;
    private static String appLinguagem;

    private static Usuario usuario;
    private static Set assuntos;
    private static List<ComboBoxItem> listaIdiomas;
    private static List<ComboBoxItem> listaFluencias;

    private static ISistemaService service;

    static {
        if (BuildConfig.FLAVOR.equals("production")) {
            SERVIDOR_WS = "http://wiser-server.herokuapp.com/";
        }
        else {
            SERVIDOR_WS = "http://192.168.0.10:1337/";
        }
    }

    public static void getMinVersao(final ICallback callback) {
        service = APIClient.getClient().create(ISistemaService.class);

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

    public static void inicializarSistema(Context context, final ICallback callback) {

        try {
            new Facebook();

            /* Carregar linguagem do celular */
            appLinguagem = context.getResources().getConfiguration().locale.getLanguage();

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
                                        public void onError(String mensagemErro) {

                                        }
                                    });
                                }

                                @Override
                                public void onError(String mensagemErro) {

                                }
                            });
                        }

                        @Override
                        public void onError(String mensagemErro) {

                        }
                    });
                }

                @Override
                public void onError(String mensagemErro) {

                }
            });
        }
        catch (Exception e) {
            Log.e("Inicializar Sistema", e.getMessage());
            callback.onError(e.getMessage());
        }
    }

    private static void carregarListaAssuntos(final ICallback callback) {
        assuntos = new HashSet();

        Call<HashSet<Assunto>> call = service.carregarAssuntos(appLinguagem);
        call.enqueue(new Callback<HashSet<Assunto>>() {
            @Override
            public void onResponse(Call<HashSet<Assunto>> call, Response<HashSet<Assunto>> response) {
                if (response.isSuccessful()) {
                    assuntos = response.body();
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

    private static void carregarListaIdiomas(final ICallback callback) {
        listaIdiomas = new LinkedList();

        Call<LinkedList<ComboBoxItem>> call = service.carregarIdiomas(appLinguagem, true);
        call.enqueue(new Callback<LinkedList<ComboBoxItem>>() {
            @Override
            public void onResponse(Call<LinkedList<ComboBoxItem>> call, Response<LinkedList<ComboBoxItem>> response) {
                if (response.isSuccessful()) {
                    listaIdiomas.addAll(response.body());
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
                    listaFluencias.addAll(response.body());
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

    private static void carregarAccessToken(final ICallback callback) {
        Call<AccessTokenModel> call = service.carregarAccessToken();
        call.enqueue(new Callback<AccessTokenModel>() {
            @Override
            public void onResponse(Call<AccessTokenModel> call, Response<AccessTokenModel> response) {
                if (response.isSuccessful()) {
                    AccessTokenModel model = response.body();

                    accessToken = new AccessToken(model.getAccessToken(), model.getApplicationID(),
                            model.getUserID(), Arrays.asList(model.getPermissions()), null, null, null, null);
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

    public static void logout(Activity activity) {
        Sistema.usuario = null;

        Intent i = new Intent(activity, LoginActivity.class);

        i.putExtra(LOGOUT, true);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(i);
        activity.finish();
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario usuario) {
        Sistema.usuario = usuario;
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

    public static List<ComboBoxItem> getListaIdiomas() {
        return listaIdiomas;
    }

    public static List<ComboBoxItem> getListaFluencias() {
        return listaFluencias;
    }

    public static String getDescricaoFluencia(int id) {
        for (ComboBoxItem item : listaFluencias) {
            if (item.getId() == id) {
                return item.getDescricao();
            }
        }

        return "";
    }

    public static String getDescricaoIdioma(int id) {
        for (ComboBoxItem item : listaIdiomas) {
            if (item.getId() == id) {
                return item.getDescricao();
            }
        }

        return "";
    }

    public static void carregarComboIdiomas(Spinner cmbIdioma, Context context) {
        carregarCombo(listaIdiomas, cmbIdioma, context);
    }

    public static void carregarComboFluencia(Spinner cmbFluencia, Context context) {
        carregarCombo(listaFluencias, cmbFluencia, context);
    }

    private static void carregarCombo(List<ComboBoxItem> itens, Spinner cmb, Context context) {
        ArrayAdapter<ComboBoxItem> dataAdapter = new ArrayAdapter<ComboBoxItem>(context, R.layout.frm_spinner_text, itens);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmb.setAdapter(dataAdapter);
    }

    public static AccessToken getAccessToken() {
        return accessToken;
    }

    public static Set<Assunto> getAssuntos() {
        return assuntos;
    }
}