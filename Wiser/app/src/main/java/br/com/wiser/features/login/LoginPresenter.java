package br.com.wiser.features.login;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

import br.com.wiser.APIClient;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.services.CarregarConversasService;
import br.com.wiser.utils.UtilsLocation;
import br.com.wiser.views.principal.PrincipalActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 22/01/2017.
 */
public class LoginPresenter extends Presenter<ILoginView> {

    private ILoginService service;
    private Facebook facebook;

    private Intent carregarConversasServices;

    @Override
    public void onCreate() {
        view.onInitView();

        service = APIClient.getClient().create(ILoginService.class);
        facebook = new Facebook(view.getContext());
        checkLogin();
    }

    private void checkLogin() {
        if (facebook.isLogado() && Sistema.checkPermissoes(view.getContext())) {
            login(new ICallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(String mensagemErro) {
                    Log.e("Carregar Perfil", mensagemErro);
                    view.showToast(view.getContext().getString(R.string.falha_login));
                }
            });
        }
    }

    public void setCallbackManager(int requestCode, int resultCode, Intent data) {
        facebook.callbackManager(requestCode, resultCode, data);
        checkLogin();
    }

    public void setOnClickBtnLogin() {
        facebook.login(getActivity());
    }

    private void login(final ICallback callback) {
        Login login = new Login();

        try {
            UtilsLocation.atualizarLocalizacao(view.getContext());
            UtilsLocation.atualizarCoordenadas(view.getContext());

            login.setFacebookID(facebook.getAccessToken().getUserId());
            login.setAccessToken(facebook.getAccessToken().getToken());
            login.setDataUltimoAcesso(new Date());
            login.setLatitude(UtilsLocation.getLatitude());
            login.setLongitude(UtilsLocation.getLongitude());

            Call<Usuario> call = service.salvar(login);
            call.enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(final Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful()) {
                        Sistema.setUsuario(response.body());

                        LoginDAO loginDAO = new LoginDAO(getContext());
                        loginDAO.logarUsuario();
                        Toast.makeText(getActivity(), "Funcionou: " + loginDAO.getLogado(), Toast.LENGTH_SHORT).show();

                        facebook.carregarPerfil(Sistema.getUsuario(), new ICallback() {
                            @Override
                            public void onSuccess() {
                                onLoginSuccess();
                                callback.onSuccess();
                            }

                            @Override
                            public void onError(String mensagemErro) {
                                callback.onError(mensagemErro);
                            }
                        });
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
        catch (Exception e) {
            view.showToast(view.getContext().getString(R.string.falha_login));
        }
    }

    public void logout() {
        stopService();
        Sistema.logout(view.getContext());
    }

    private void onLoginSuccess() {
        Sistema.getListaUsuarios().put(Sistema.getUsuario().getUserID(), Sistema.getUsuario());

        startPrincipalActivity();
        startService();
    }

    private void startPrincipalActivity() {
        Intent i = new Intent(getContext(), PrincipalActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(i);
        ((Activity) getContext()).finish();
    }

    private void startService() {
        carregarConversasServices = new Intent(getContext(), CarregarConversasService.class);
        getContext().startService(carregarConversasServices);
    }

    private void stopService() {
        if (carregarConversasServices != null) {
            getContext().stopService(carregarConversasServices);
        }
    }

    public void tratarPermissioes(int[] grantResults) {
        boolean permissoesAceitas = false;

        if (grantResults.length > 0) {
            permissoesAceitas = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissoesAceitas = false;
                }
            }
        }

        if (!permissoesAceitas) {
            view.showToast(view.getContext().getString(R.string.necessario_permitir));
            logout();
        }
    }
}