package br.com.wiser;

import javax.inject.Singleton;

import br.com.wiser.features.login.LoginModule;
import dagger.Component;

/**
 * Created by Jefferson on 12/03/2017.
 */

@Component(modules = ApiModule.class)
@Singleton
public interface ApiComponent {
    void inject(LoginModule loginModule);
}
