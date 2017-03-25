package br.com.wiser.features.login;

import br.com.wiser.ApiModule;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

/**
 * Created by Jefferson on 06/03/2017.
 */

@Module(includes = ApiModule.class)
public class LoginModule {

    @Provides
    public ILoginService getILoginService(Retrofit retrofit) {
        return retrofit.create(ILoginService.class);
    }
}
