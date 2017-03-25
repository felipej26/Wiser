package br.com.wiser;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.wiser.facebook.Facebook;
import br.com.wiser.utils.ComboBoxItem;
import br.com.wiser.utils.ComboDeserializer;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jefferson on 08/03/2017.
 * Classe especializada em criar as dependencias
 */

@Module
public final class ApiModule {

    private Context context;

    public ApiModule(Context context) {
        this.context = context;
    }

    @Provides
    Retrofit provideRetrofit() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ComboBoxItem.class, new ComboDeserializer())
                .create();

        return new Retrofit.Builder()
                .baseUrl(Sistema.SERVIDOR_WS)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides
    Facebook provideFacebook() {
        return new Facebook(context);
    }
}
