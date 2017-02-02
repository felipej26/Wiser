package br.com.wiser.models.mensagens;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IMensagensService {
    @POST("conversa/enviarMensagem")
    Call<Object> enviarMensagem(@Body Map<String, String> parametros);

    @POST("conversa/atualizarLidas")
    Call<Object> atualizarMensagensLidas(@Body Map<String, Long> parametros);
}
