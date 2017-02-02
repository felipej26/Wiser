package br.com.wiser;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.facebook.AccessToken;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.wiser.facebook.AccessTokenModel;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.models.assunto.Assunto;
import br.com.wiser.models.sistema.ISistemaService;
import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.views.login.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Sistema {

    public static final String SERVIDOR_WS = "http://nodejs-wiserserver.rhcloud.com/";
    public static final int PERMISSION_ALL = 0;
    public static final boolean PERMITIR_PAGINAS_NAO_VERIFICADAS = false;

    public static final String LOGOUT = "logout";
    public static final String CONVERSA = "conversa";
    public static final String CONTATO = "contato";
    public static final String LISTAUSUARIOS = "listausuarios";
    public static final String DISCUSSAO = "discussao";

    private static final int NUM_LISTAS_PARA_CARREGAR = 4;
    private static int NUM_LISTAS_CARREGADAS = 0;

    private static AccessToken accessToken;
    private static String appLinguagem;

    private static Usuario usuario;
    private static Set assuntos;
    private static List listaIdiomas;
    private static List listaFluencias;
    private static Map<Long, Usuario> listaUsuarios;

    private static ISistemaService service;

    public static void inicializarSistema(Context context, final ICallback callback) {

        listaUsuarios = new HashMap<>();

        try {
            service = APIClient.getClient().create(ISistemaService.class);

            /* Carrega linguagem do celular */
            appLinguagem = context.getResources().getConfiguration().locale.getLanguage();

            ICallback callbackList = new ICallback() {
                @Override
                public void onSuccess() {
                    checkListas(callback);
                }

                @Override
                public void onError(String mensagemErro) {
                    callback.onError(mensagemErro);
                }
            };

            carregarAccessToken(callbackList);
            carregarListaIdiomas(callbackList);
            carregarListaFluencias(callbackList);
            carregarListaAssuntos(callbackList);
        }
        catch (Exception e) {
            Log.e("Inicializar Sistema", e.getMessage());
            callback.onError(e.getMessage());
        }
    }

    private static void checkListas(ICallback callback) {
        NUM_LISTAS_CARREGADAS++;

        if (NUM_LISTAS_CARREGADAS == NUM_LISTAS_PARA_CARREGAR) {
            callback.onSuccess();
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
                    callback.onSuccess();
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
                    callback.onSuccess();
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
                    callback.onSuccess();
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
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<AccessTokenModel> call, Throwable t) {
                Log.e("Inicializar Sistema", "Erro ao carregar o Access Token", t);
                callback.onError(t.getMessage());
            }
        });
    }

    public static boolean checkPermissoes(Context context) {
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

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
            int status;

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

    public static void logout(Context context) {
        new Facebook(context).logout();
        Sistema.usuario = null;
    }

    public static Usuario getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario usuario) {
        Sistema.usuario = usuario;
    }

    public static void logout(Activity activity) {
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

    public static String getDescricaoFluencia(int id) {
        List<ComboBoxItem> listaAux;

        listaAux = new LinkedList<>(listaFluencias);
        listaAux.remove(0);

        for (ComboBoxItem item : listaAux) {
            if (item.getId() == id) {
                return item.getDescricao();
            }
        }

        return "";
    }

    public static String getDescricaoIdioma(int id) {
        List<ComboBoxItem> listaAux;

        listaAux = new LinkedList<>(listaIdiomas);
        listaAux.remove(0);

        for (ComboBoxItem item : listaAux) {
            if (item.getId() == id) {
                return item.getDescricao();
            }
        }

        return "";
    }

    public static void carregarComboFluencia(Spinner cmbFluencia, Context context, boolean itemTodos) {
        List<ComboBoxItem> listaAux;

        if (!itemTodos) {
            listaAux = new LinkedList<>(listaFluencias);
            listaAux.remove(0);
            carregarCombo(listaAux, cmbFluencia, context);
        }
        else {
            carregarCombo(listaFluencias, cmbFluencia, context);
        }
    }

    public static void carregarComboIdiomas(Spinner cmbIdioma, Context context, boolean itemTodos) {
        List<ComboBoxItem> listaAux;

        if (!itemTodos) {
            listaAux = new LinkedList<>(listaIdiomas);
            listaAux.remove(0);
            carregarCombo(listaAux, cmbIdioma, context);
        }
        else {
            carregarCombo(listaIdiomas, cmbIdioma, context);
        }
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

    public static Map<Long, Usuario> getListaUsuarios() {
        return listaUsuarios;
    }
}