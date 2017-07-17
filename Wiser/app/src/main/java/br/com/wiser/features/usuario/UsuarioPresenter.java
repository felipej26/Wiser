package br.com.wiser.features.usuario;

import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.facebook.Facebook;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 10/07/2017.
 */

public class UsuarioPresenter {

    public interface ICallback {
        void onFinished(List<Usuario> usuarios);
    }

    private IUsuarioService service;
    private Facebook facebook;

    public UsuarioPresenter() {
        service = APIClient.getClient().create(IUsuarioService.class);
        facebook = new Facebook();
    }

    UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void getInServer(long usuario, final ICallback callback) {
        Set<Long> usuarios = new HashSet<>();
        usuarios.add(usuario);
        getInServer(usuarios, callback);
    }

    public void getInServer(Set<Long> usuarios, final ICallback callback) {

        Call<List<Usuario>> call = service.carregarUsuarios(Sistema.getUsuario().getId(), usuarios.toArray());
        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful()) {
                    final List<Usuario> listaUsuarios = response.body();

                    facebook.carregarUsuarios(listaUsuarios, new br.com.wiser.interfaces.ICallback() {
                        @Override
                        public void onSuccess() {
                            for (Usuario usuario : listaUsuarios) {
                                usuarioDAO.insert(usuario);
                            }

                            callback.onFinished(listaUsuarios);
                        }

                        @Override
                        public void onError(String mensagemErro) {
                            Log.e("Carregar Perfis", "Erro ao carregar os perfis dos usuarios: " + mensagemErro);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                Log.e("Carregar Perfis", "Erro ao carregar os usuarios", t);
            }
        });
    }
}
