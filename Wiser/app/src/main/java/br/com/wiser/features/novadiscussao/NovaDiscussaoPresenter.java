package br.com.wiser.features.novadiscussao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.discussao.Discussao;
import br.com.wiser.features.forum.IForumService;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class NovaDiscussaoPresenter {

    private IForumService service;

    public NovaDiscussaoPresenter() {
        service = APIClient.getClient().create(IForumService.class);
    }

    public void salvarDiscussao(String titulo, String descricao, final ICallback callback) {
        Map<String, String> parametros = new HashMap<>();

        parametros.put("id", "0");
        parametros.put("usuario", String.valueOf(Sistema.getUsuario().getId()));
        parametros.put("titulo", Utils.encode(titulo));
        parametros.put("descricao", Utils.encode(descricao));
        parametros.put("data", new Date().toString());

        Call<Discussao> call = service.salvarDiscussao(parametros);
        call.enqueue(new Callback<Discussao>() {
            @Override
            public void onResponse(Call<Discussao> call, Response<Discussao> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                }
                else {
                    callback.onError("");
                }
            }

            @Override
            public void onFailure(Call<Discussao> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
