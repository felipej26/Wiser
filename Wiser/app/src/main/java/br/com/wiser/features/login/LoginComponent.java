package br.com.wiser.features.login;

import dagger.Component;

/**
 * Created by Jefferson on 06/03/2017.
 */

@Component(modules = LoginModule.class)
public interface LoginComponent {
    void inject(LoginPresenter loginPresenter);
}
