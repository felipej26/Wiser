package br.com.wiser.features.mensagem;

import android.util.Log;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.interfaces.ICallbackFinish;
import br.com.wiser.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class MensagemPresenter {

    private IMensagemService service;
    private MensagemDAO mensagensDAO = new MensagemDAO();

    public MensagemPresenter() {
        service = APIClient.getClient().create(IMensagemService.class);
    }

    public List<Mensagem> carregarMensagens(Conversa conversa) throws ParseException {
        return mensagensDAO.get(conversa.getId());
    }

    public void atualizarMensagensLidas(Conversa conversa, final ICallbackFinish callback) {
        Map<String, Long> map = new HashMap<>();

        map.put("conversa", conversa.getId());
        map.put("usuario", Sistema.getUsuario().getId());
        map.put("mensagem", mensagensDAO.getMaxId(conversa.getId()));

        Call<Object> call = service.atualizarMensagensLidas(map);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    callback.onFinish();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("Atualizar Lidas", "Erro ao fazer a requisição", t);
            }
        });
    }

    public void enviarMensagem(long conversa, long destinatario, String textoMensagem, final ICallback callback) {
        Map<String, String> map = new HashMap<>();
        final Mensagem mensagem = new Mensagem();

        mensagem.setConversa(conversa);
        mensagem.setUsuario(Sistema.getUsuario().getId());
        mensagem.setEstado(Mensagem.Estado.ENVIANDO);
        mensagem.setData(new Date());
        mensagem.setMensagem(textoMensagem.trim());
        mensagem.setLida(true);

        if (!textoMensagem.trim().isEmpty()) {
            map.put("conversa", String.valueOf(conversa));
            map.put("usuario", String.valueOf(mensagem.getUsuario()));
            map.put("destinatario", String.valueOf(destinatario));
            map.put("data", mensagem.getData().toString());
            map.put("mensagem", Utils.encode(mensagem.getMensagem()));

            Call<Mensagem> call = service.enviarMensagem(map);
            call.enqueue(new Callback<Mensagem>() {
                @Override
                public void onResponse(Call<Mensagem> call, Response<Mensagem> response) {
                    if (response.isSuccessful()) {
                        mensagem.setId(response.body().getId());
                        mensagensDAO.insert(mensagem);
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(Call<Mensagem> call, Throwable t) {
                    //view.showToast("Falha ao enviar");
                    // TODO reenviar a mensagem
                    callback.onError(t.getMessage());
                }
            });
        }
    }
}
