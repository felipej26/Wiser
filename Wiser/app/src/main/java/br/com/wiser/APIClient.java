package br.com.wiser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.utils.ComboDeserializer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jefferson on 22/01/2017.
 */
public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        Gson gson;

        if (retrofit == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(ComboBoxItem.class, new ComboDeserializer())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(WiserApplication.getAppContext().getString(R.string.link_servidor))
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }
}
