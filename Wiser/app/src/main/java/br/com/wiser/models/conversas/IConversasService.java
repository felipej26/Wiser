package br.com.wiser.models.conversas;

import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IConversasService {
    @GET("conversa/carregarConversas")
    Call<LinkedList<Conversas>> carregarConversas(@Query("usuario") long userID, @Query("mensagem") long lastMensagem);
}
