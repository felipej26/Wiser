package br.com.wiser.features.procurardiscussao;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.discussao.Discussao;
import br.com.wiser.features.forum.IForumService;
import br.com.wiser.interfaces.ICallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class ProcurarDiscussaoPresenter {

    private IForumService service;

    private LinkedList<Discussao> listaDiscussoes;

    public ProcurarDiscussaoPresenter() {
        service = APIClient.getClient().create(IForumService.class);
    }

    public Discussao getDiscussao(int posicao) {
        return listaDiscussoes.get(posicao);
    }

    public List<Discussao> getDiscussoes() {
        return listaDiscussoes;
    }

    public void procurarDiscussao(final String chave, final ICallback callback) {

        Call<LinkedList<Discussao>> call = service.procurarDiscussoes(Sistema.getUsuario().getId(), chave);
        call.enqueue(new Callback<LinkedList<Discussao>>() {
            @Override
            public void onResponse(Call<LinkedList<Discussao>> call, Response<LinkedList<Discussao>> response) {
                if (response.isSuccessful()) {
                    listaDiscussoes = response.body();
                    callback.onSuccess();
                }
                else {
                    callback.onError("Erro ao carregar discussões");
                }
            }

            @Override
            public void onFailure(Call<LinkedList<Discussao>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
