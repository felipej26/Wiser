package br.com.wiser.features.procurarusuarios;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class ProcurarUsuariosPresenter {

    private IProcurarUsuariosService service;
    private ArrayList<Usuario> listaUsuarios;

    public ProcurarUsuariosPresenter() {
        service = APIClient.getClient().create(IProcurarUsuariosService.class);
    }

    public List<Usuario> getUsuarios() {
        return listaUsuarios;
    }

    public void procurarUsuarios(Pesquisa pesquisa, final ICallback callback) {
        listaUsuarios = new ArrayList<>();

        Map<String, String> map = new HashMap<>();
        map.put("usuario", String.valueOf(Sistema.getUsuario().getId()));
        map.put("latitude", String.valueOf(Sistema.getUsuario().getLatitude()));
        map.put("longitude", String.valueOf(Sistema.getUsuario().getLongitude()));
        map.put("distancia", String.valueOf(pesquisa.getDistancia()));

        if (pesquisa.getIdioma() > 0)
            map.put("idioma", String.valueOf(pesquisa.getIdioma()));

        if (pesquisa.getFluencia() > 0)
            map.put("fluencia", String.valueOf(pesquisa.getFluencia()));

        Call<ArrayList<Usuario>> call = service.procurarUsuarios(map);
        call.enqueue(new Callback<ArrayList<Usuario>>() {
            @Override
            public void onResponse(Call<ArrayList<Usuario>> call, Response<ArrayList<Usuario>> response) {
                if (response.isSuccessful()) {
                    listaUsuarios = response.body();
                    callback.onSuccess();
                }
                else {
                    Log.e("Procurar Usuarios", response.message());
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Usuario>> call, Throwable t) {
                Log.e("Procurar Usuarios", t.getMessage(), t);
                callback.onError(t.getMessage());
            }
        });
    }


}
