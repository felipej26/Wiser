package br.com.wiser.features.perfilcompleto;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.contato.IContatosService;
import br.com.wiser.features.discussao.Discussao;
import br.com.wiser.features.forum.IForumService;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.ICallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 23/01/2017.
 */
public class PerfilCompletoPresenter {

    private IContatosService service;
    private IForumService forumService;

    private Usuario usuario;
    private LinkedList<Discussao> listaDiscussoes;

    public PerfilCompletoPresenter(Usuario usuario) {
        this.usuario = usuario;

        service = APIClient.getClient().create(IContatosService.class);
        forumService = APIClient.getClient().create(IForumService.class);
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<Discussao> getDiscussoes() {
        return listaDiscussoes;
    }

    public void carregarDiscussoes(final ICallback callback) {

        Call<LinkedList<Discussao>> call = forumService.carregarDiscussoes(usuario.getId(), true);
        call.enqueue(new Callback<LinkedList<Discussao>>() {
            @Override
            public void onResponse(Call<LinkedList<Discussao>> call, Response<LinkedList<Discussao>> response) {
                if (response.isSuccessful()) {
                    listaDiscussoes = response.body();
                    callback.onSuccess();
                }
                else {
                    callback.onError("");
                }
            }

            @Override
            public void onFailure(Call<LinkedList<Discussao>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void adicionarContato(final ICallback callback) {
        Call<Object> call = service.adicionarContato(Sistema.getUsuario().getId(), usuario.getId());
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    usuario.setContato(true);
                    callback.onSuccess();
                }
                else {
                    Log.e("Adicionar Contato", response.message());
                    callback.onError("Falha ao adicionar contato");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("Adicionar Contato", t.getMessage(), t);
                callback.onError("Falha ao adicionar contato");
            }
        });
    }
}
