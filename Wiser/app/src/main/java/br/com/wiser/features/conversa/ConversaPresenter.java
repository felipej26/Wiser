package br.com.wiser.features.conversa;

import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.usuario.IUsuarioService;
import br.com.wiser.features.usuario.Usuario;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class ConversaPresenter {

    public interface ICallbackUsuarios {
        void onSuccess(List<Usuario> listaUsuarios);
    }

    public interface ICallbackIdConversa {
        void onSuccess(long idConversa);
    }

    private IConversaService conversaService;
    private IUsuarioService usuarioService;
    private Realm realm;
    private RealmResults<Conversa> listaConversas;

    private ICallbackUsuarios callbackUsuarios;

    public ConversaPresenter() {
        conversaService = APIClient.getClient().create(IConversaService.class);
        usuarioService = APIClient.getClient().create(IUsuarioService.class);
        realm = Realm.getDefaultInstance();
        listaConversas = realm.where(Conversa.class).findAll();
        listaConversas.addChangeListener(new RealmChangeListener<RealmResults<Conversa>>() {
            @Override
            public void onChange(RealmResults<Conversa> conversas) {
                if (callbackUsuarios != null) {
                    carregarUsuarios(conversas, callbackUsuarios);
                }
            }
        });
    }

    public RealmResults<Conversa> getConversas() {
        return listaConversas.where().isNotEmpty("mensagens").findAll();
    }

    public void getIdConversa(long destinatario, ICallbackIdConversa callback) {
        try {
            callback.onSuccess(listaConversas.where().equalTo("destinatario", destinatario).findFirst().getId());
        }
        catch (Exception e) {
            getIdConversaServer(destinatario, callback);
        }
    }

    private void getIdConversaServer(long destinatario, final ICallbackIdConversa callback) {

        Call<Conversa> call = conversaService.carregarConversa(Sistema.getUsuario().getId(), destinatario);
        call.enqueue(new Callback<Conversa>() {
            @Override
            public void onResponse(Call<Conversa> call, Response<Conversa> response) {
                if (response.isSuccessful()) {
                    realm.beginTransaction();
                    realm.insert(response.body());
                    realm.commitTransaction();

                    callback.onSuccess(response.body().getId());
                }
            }

            @Override
            public void onFailure(Call<Conversa> call, Throwable t) {

            }
        });

    }

    private void carregarUsuarios(RealmResults<Conversa> listaConversas, final ICallbackUsuarios callback) {
        Set<Long> usuarios = new HashSet<>();

        for (Conversa conversa : listaConversas) {
            usuarios.add(conversa.getDestinatario());
        }

        if (usuarios.size() > 0) {
            Call<List<Usuario>> call = usuarioService.carregarUsuarios(Sistema.getUsuario().getId(), usuarios.toArray());
            call.enqueue(new Callback<List<Usuario>>() {
                @Override
                public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(response.body());
                    } else {
                        Log.e("ConversaPresenter", "Erro ao carregar Usuarios. " + response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<List<Usuario>> call, Throwable t) {
                    Log.e("ConversaPresenter", "Erro ao carregar Usuarios", t);
                }
            });
        }
    }

    public void addUsuariosListener(final ICallbackUsuarios callbackUsuarios) {
        this.callbackUsuarios = callbackUsuarios;
        carregarUsuarios(listaConversas, callbackUsuarios);
    }
}
