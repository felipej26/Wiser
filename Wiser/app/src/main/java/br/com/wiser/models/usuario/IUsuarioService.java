package br.com.wiser.models.usuario;

import java.util.List;

import br.com.wiser.models.configuracoes.Configuracoes;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Jefferson on 22/01/2017.
 */
public interface IUsuarioService {
    @GET("usuario/carregarUsuarios")
    Call<List<Usuario>> carregarUsuarios(@Query("id") long userID, @Query("usuario") Object[] usuario);

    @POST("usuario/salvarConfiguracoes")
    Call<Object> salvarConfiguracoes(@Body Configuracoes configuracoes);

    @FormUrlEncoded
    @POST("usuario/desativarConta")
    Call<Object> desativarConta(@Field("id") long userID);
}
