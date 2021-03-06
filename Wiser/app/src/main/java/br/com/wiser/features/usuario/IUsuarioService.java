package br.com.wiser.features.usuario;

import java.util.List;

import br.com.wiser.features.configuracoes.Configuracoes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jefferson on 22/01/2017.
 */
public interface IUsuarioService {
    @GET("usuario/carregarUsuario")
    Call<Usuario> carregarUsuario(@Query("id") long userID, @Query("usuario") long usuario);

    @GET("usuario/carregarUsuarios")
    Call<List<Usuario>> carregarUsuarios(@Query("id") long userID, @Query("usuario") Object[] usuario);

    @POST("usuario/salvarConfiguracoes")
    Call<Object> salvarConfiguracoes(@Body Configuracoes configuracoes);

    @FormUrlEncoded
    @POST("usuario/desativarConta")
    Call<Object> desativarConta(@Field("id") long userID);

    @PUT("usuario/{id}")
    Call<Object> salvarLocalizacao(@Path("id") long userID, @Query("latitude") double latitude, @Query("longitude") double longitude);
}
