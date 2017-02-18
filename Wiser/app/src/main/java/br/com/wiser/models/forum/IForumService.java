package br.com.wiser.models.forum;

import java.util.LinkedList;
import java.util.Map;

import br.com.wiser.views.IView;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IForumService {
    @GET("discussao/carregarDiscussoes")
    Call<LinkedList<Discussao>> carregarDiscussoes(@Query("usuario") long userID, @Query("minhasDiscussoes") boolean minhasDiscussoes);

    @GET("discussao/procurarDiscussoes")
    Call<LinkedList<Discussao>> procurarDiscussoes(@Query("usuario") long userID, @Query("chave") String chave);

    @POST("discussao/updateOrCreate")
    Call<Discussao> salvarDiscussao(@Body Map<String, String> parametros);

    @POST("discussao/responderDiscussao")
    Call<Resposta> responderDiscussao(@Body Map<String, String> parametros);

    @POST("discussao/desativarDiscussao")
    Call<>
}
