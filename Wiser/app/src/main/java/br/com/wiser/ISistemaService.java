package br.com.wiser;

import java.util.HashSet;
import java.util.LinkedList;

import br.com.wiser.facebook.AccessTokenModel;
import br.com.wiser.features.assunto.Assunto;
import br.com.wiser.utils.ComboBoxItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Jefferson on 26/01/2017.
 */
public interface ISistemaService {
    @GET("sistema/getMinVersao")
    Call<Versao> getMinVersao();

    @GET("idioma/getIdiomas")
    Call<LinkedList<ComboBoxItem>> carregarIdiomas(@Query("linguagem") String linguagem, @Query("todos") boolean todos);

    @GET("fluencia/getFluencias")
    Call<LinkedList<ComboBoxItem>> carregarFluencias(@Query("linguagem") String linguagem, @Query("todos") boolean todos);

    @GET("facebook/getAccessToken")
    Call<AccessTokenModel> carregarAccessToken();

    @GET("assuntos/carregarAssuntos")
    Call<HashSet<Assunto>> carregarAssuntos(@Query("linguagem") String linguagem);
}
