package br.com.wiser.features.contato;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.ICallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 23/01/2017.
 */
public class ContatosPresenter {

    private IContatosService service;
    private List<Usuario> listaContatos;

    public ContatosPresenter() {
        service = APIClient.getClient().create(IContatosService.class);
        listaContatos = new ArrayList<>();
    }

    public Usuario getContato(int posicao) {
        return listaContatos.get(posicao);
    }

    public List<Usuario> getContatos() {
        return listaContatos;
    }

    public void carregarContatos(final ICallback callback) {
        Call<ArrayList<Usuario>> call = service.carregarContatos(Sistema.getUsuario().getId());
        call.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                if (response.isSuccessful()) {
                    listaContatos = response.body();
                    callback.onSuccess();
                }
                else {
                    Log.e("Contatos", "Erro ao carregar os Contatos. " + response.message());
                    callback.onError("Erro ao carregar os Contatos");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
                Log.e("Contatos", "Erro ao carregar os Contatos", t);
                callback.onError("Erro ao carregar os Contatos");
            }
        });
    }
}
