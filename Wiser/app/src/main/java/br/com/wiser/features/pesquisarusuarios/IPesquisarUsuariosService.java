package br.com.wiser.features.pesquisarusuarios;

import java.util.ArrayList;
import java.util.Map;

import br.com.wiser.features.usuario.Usuario;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Jefferson on 23/01/2017.
 */
public interface IPesquisarUsuariosService {
    @GET("usuario/procurarUsuarios")
    Call<ArrayList<Usuario>> procurarUsuarios(@QueryMap Map<String, String> parametros);
}
