package br.com.wiser.features.mensagem;

import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class MensagemPresenter {

    private IMensagemService service;

    private Conversa conversa;

    public MensagemPresenter(Conversa conversa) {
        this.conversa = conversa;
        service = APIClient.getClient().create(IMensagemService.class);
    }

    public Conversa getConversa() {
        return conversa;
    }

    public void enviarMensagem(String textoMensagem, final ICallback callback) {
        Map<String, String> map = new HashMap<>();

        if (!textoMensagem.trim().isEmpty()) {
            map.put("conversa", String.valueOf(conversa.getId()));
            map.put("usuario", String.valueOf(Sistema.getUsuario().getId()));
            map.put("destinatario", String.valueOf(conversa.getDestinatario().getId()));
            map.put("data", new Date().toString());
            map.put("mensagem", Utils.encode(textoMensagem));

            Call<Mensagem> call = service.enviarMensagem(map);
            call.enqueue(new Callback<Mensagem>() {
                @Override
                public void onResponse(Call<Mensagem> call, Response<Mensagem> response) {
                    if (response.isSuccessful()) {
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

    public void atualizarMensagensLidas(final ICallback callback) {
        Map<String, Long> map = new HashMap<>();

        map.put("conversa", conversa.getId());
        map.put("usuario", Sistema.getUsuario().getId());
        map.put("mensagem", conversa.getMensagens().getLast().getId());

        Call<Object> call = service.atualizarMensagensLidas(map);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    for (Mensagem mensagem : conversa.getMensagens()) {
                        mensagem.setLida(true);
                    }
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
