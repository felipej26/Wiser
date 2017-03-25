package br.com.wiser.features.mensagem;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Jefferson on 25/01/2017.
 */
public interface IMensagemService {
    @POST("conversa/enviarMensagem")
    Call<Mensagem> enviarMensagem(@Body Map<String, String> parametros);

    @POST("conversa/atualizarLidas")
    Call<Object> atualizarMensagensLidas(@Body Map<String, Long> parametros);
}
