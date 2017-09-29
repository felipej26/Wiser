package br.com.wiser.features.mensagem;

import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.Utils;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class MensagemPresenter {

    private IMensagemService service;
    private Realm realm;
    private Conversa conversa;
    private Usuario contato;

    public MensagemPresenter(long idConversa, Usuario contato) {
        this.service = APIClient.getClient().create(IMensagemService.class);
        this.realm = Realm.getDefaultInstance();
        this.conversa = realm.where(Conversa.class).equalTo("id", idConversa).findFirst();
        this.contato = contato;
    }

    public Conversa getConversa() {
        return conversa;
    }

    public Usuario getContato() {
        return contato;
    }

    public RealmList<Mensagem> getMensagens() {
        try {
            return conversa.getMensagens();
        }
        catch (Exception e) {
            return new RealmList<Mensagem>();
        }
    }

    public void enviarMensagem(String textoMensagem, final ICallback callback) {
        Map<String, String> map = new HashMap<>();

        if (!textoMensagem.trim().isEmpty()) {
            map.put("conversa", String.valueOf(conversa.getId()));
            map.put("usuario", String.valueOf(Sistema.getUsuario().getId()));
            map.put("destinatario", String.valueOf(conversa.getDestinatario()));
            map.put("data", new Date().toString());
            map.put("mensagem", Utils.encode(textoMensagem));

            Call<Mensagem> call = service.enviarMensagem(map);
            call.enqueue(new Callback<Mensagem>() {
                @Override
                public void onResponse(Call<Mensagem> call, Response<Mensagem> response) {
                    if (response.isSuccessful()) {

                        if (conversa.getId() == 0) {
                            checkAndUpdateConversa(response.body().getConversa());
                        }

                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(Call<Mensagem> call, Throwable t) {
                    callback.onError(t.getMessage());
                }
            });
        }
    }

    private void checkAndUpdateConversa(long idConversa) {
        Conversa checkConversa = realm.where(Conversa.class).equalTo("id", conversa.getId()).findFirst();

        if (checkConversa == null) {
            Conversa conversa = new Conversa();
            conversa.setId(idConversa);
            conversa.setDestinatario(this.conversa.getDestinatario());

            realm.beginTransaction();
            realm.copyToRealm(conversa);
            realm.commitTransaction();

            this.conversa = realm.where(Conversa.class).equalTo("id", conversa.getId()).findFirst();
        }
    }

    public void atualizarMensagensLidas(final ICallback callback) {
        Map<String, Long> map = new HashMap<>();

        map.put("conversa", conversa.getId());
        map.put("usuario", Sistema.getUsuario().getId());
        map.put("mensagem", conversa.getMensagens().last().getId());

        Call<Object> call = service.atualizarMensagensLidas(map);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    realm.beginTransaction();
                    for (Mensagem mensagem : conversa.getMensagens().where().equalTo("lida", false).findAll()) {
                        mensagem.setLida(true);
                    }
                    realm.commitTransaction();
                    callback.onSuccess();
                }
                else {
                    callback.onError("");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("Atualizar Lidas", "Erro ao fazer a requisição", t);
                callback.onError(t.getMessage());
            }
        });
    }
}
