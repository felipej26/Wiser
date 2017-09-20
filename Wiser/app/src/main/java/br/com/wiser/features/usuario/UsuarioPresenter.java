package br.com.wiser.features.usuario;

import android.util.Log;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 19/09/2017.
 */
public class UsuarioPresenter {

    private IUsuarioService usuarioService;

    public UsuarioPresenter() {
        usuarioService = APIClient.getClient().create(IUsuarioService.class);
    }

    public void salvarLocalizacao(final double latitude, final double longitude) {
        Call<Object> call = usuarioService.salvarLocalizacao(Sistema.getUsuario().getId(), latitude, longitude);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    Sistema.getUsuario().setLatitude(latitude);
                    Sistema.getUsuario().setLongitude(longitude);
                    Sistema.setUsuario(Sistema.getUsuario());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("Localizacao", "Erro ao salvar Localização. Erro: " + t.getMessage(), t);
            }
        });
    }

}
