package br.com.wiser.models.contatos;

import java.util.ArrayList;
import java.util.List;

import br.com.wiser.models.usuario.Usuario;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Jefferson on 23/01/2017.
 */
public interface IContatosService {
    @GET("contato/carregarContatos")
    Call<ArrayList<Contato>> carregarContatos(@Query("usuario") long userID);

    @FormUrlEncoded
    @POST("contato/adicionarContato")
    Call<Object> adicionarContato(@Field("usuario") long userID, @Field("contato") long contatoID);
}
