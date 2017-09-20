package br.com.wiser.features.login;

import java.util.Date;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.ICallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 22/01/2017.
 */
public class LoginPresenter {

    private ILoginService service;
    private Facebook facebook;

    public LoginPresenter() {
        service = APIClient.getClient().create(ILoginService.class);
        facebook = new Facebook();
    }

    public void gravarLogin(final ICallback callback) {

        facebook.getProfile(new Facebook.ICallbackProfileInfo() {
            @Override
            public void onCompleted(String nome, String primeiroNome, Date dataNascimento) {
                Login login = new Login();

                login.setNome(nome);
                login.setPrimeiroNome(primeiroNome);
                login.setDataNascimento(dataNascimento);
                login.setFacebookID(facebook.getAccessToken().getUserId());
                login.setAccessToken(facebook.getAccessToken().getToken());
                login.setDataUltimoAcesso(new Date());

                Call<Usuario> call = service.salvar(login);
                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(final Call<Usuario> call, Response<Usuario> response) {

                        if (response.isSuccessful()) {
                            Sistema.setUsuario(response.body());
                            callback.onSuccess();
                        }
                        else {
                            callback.onError(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {
                        callback.onError(t.getMessage());
                    }
                });
            }
        });
    }
}