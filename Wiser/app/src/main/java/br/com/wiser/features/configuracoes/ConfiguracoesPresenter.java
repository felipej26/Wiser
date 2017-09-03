package br.com.wiser.features.configuracoes;

import android.util.Log;

import java.net.HttpURLConnection;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.usuario.IUsuarioService;
import br.com.wiser.interfaces.ICallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 22/01/2017.
 */
public class ConfiguracoesPresenter {

    private IUsuarioService service;

    public ConfiguracoesPresenter() {
        this.service = APIClient.getClient().create(IUsuarioService.class);
    }

    public void salvar(final Configuracoes configuracoes){
        salvar(configuracoes, new ICallback() {
            @Override
            public void onSuccess() {
                Sistema.getUsuario().setIdioma(configuracoes.getIdioma());
                Sistema.getUsuario().setFluencia(configuracoes.getFluencia());
                Sistema.getUsuario().setStatus(configuracoes.getStatus());
            }

            @Override
            public void onError(String mensagemErro) {
                Log.e("Salvar Configurações", mensagemErro);
            }
        });
    }

    private void salvar(Configuracoes configuracoes, final ICallback callback) {
        Call<Object> call = service.salvarConfiguracoes(configuracoes);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                }
                else {
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void desativarConta(long userID, final ICallback callback) {
        Call<Object> call = service.desativarConta(userID);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess();
                }
                else {
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
