package br.com.wiser.features.forum;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.discussao.Discussao;
import br.com.wiser.interfaces.ICallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class ForumPresenter {

    private IForumService service;

    private LinkedList<Discussao> listaDiscussoes;

    public ForumPresenter() {
        service = APIClient.getClient().create(IForumService.class);
    }

    public List<Discussao> getDiscussoes() {
        return listaDiscussoes;
    }

    public Discussao getDiscussao(int posicao) {
        return listaDiscussoes.get(posicao);
    }

    public void carregarDiscussoes(final ICallback callback) {
        Call<LinkedList<Discussao>> call = service.carregarDiscussoes(Sistema.getUsuario().getId(), false);
        call.enqueue(new Callback<LinkedList<Discussao>>() {
            @Override
            public void onResponse(Call<LinkedList<Discussao>> call, Response<LinkedList<Discussao>> response) {
                if (response.isSuccessful()) {
                    listaDiscussoes = response.body();
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Call<LinkedList<Discussao>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
