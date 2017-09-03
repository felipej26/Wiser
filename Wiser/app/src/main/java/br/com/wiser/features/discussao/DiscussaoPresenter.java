package br.com.wiser.features.discussao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.forum.IForumService;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 23/01/2017.
 */
public class DiscussaoPresenter {
    private IForumService service;
    private Discussao discussao;

    public DiscussaoPresenter(Discussao discussao) {
        this.discussao = discussao;
        service = APIClient.getClient().create(IForumService.class);
    }

    public Discussao getDiscussao() {
        return discussao;
    }

    public void enviarResposta(String resposta, final ICallback callback) {
        Map<String, String> map = new HashMap<>();

        if (resposta.trim().isEmpty()) {
            return;
        }

        map.put("id", String.valueOf(discussao.getId()));
        map.put("usuario", String.valueOf(Sistema.getUsuario().getId()));
        map.put("data", new Date().toString());
        map.put("resposta", Utils.encode(resposta.trim()));

        Call<Resposta> call = service.responderDiscussao(map);
        call.enqueue(new Callback<Resposta>() {
            @Override
            public void onResponse(Call<Resposta> call, Response<Resposta> response) {
                if (response.isSuccessful()) {
                    discussao.getListaRespostas().add(response.body());
                    callback.onSuccess();
                }
                else {
                    callback.onError("");
                }
            }

            @Override
            public void onFailure(Call<Resposta> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void desativarDiscussao(final ICallback callback) {
        Call<Object> call = service.desativarDiscussao(discussao.getId(), !discussao.isAtiva());
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                discussao.setAtiva(!discussao.isAtiva());
                callback.onSuccess();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
