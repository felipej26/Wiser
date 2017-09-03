package br.com.wiser.features.conversa;

import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IConversaService {
    @GET("conversa/carregarConversas")
    Call<LinkedList<Conversa>> carregarConversas(@Query("usuario") long userID, @Query("mensagem") long lastMensagem);

    @GET("conversa/carregarConversa")
    Call<Conversa> carregarConversa(@Query("usuario") long usuario, @Query("destinatario") long destinatario);
}
