package br.com.wiser.presenters.configuracoes;

import android.util.Log;

import java.net.HttpURLConnection;

import br.com.wiser.APIClient;
import br.com.wiser.features.parametros.CodigoParametro;
import br.com.wiser.features.parametros.ParametroDAO;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.features.usuario.IUsuarioService;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.models.configuracoes.Configuracoes;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.utils.Utils;
import br.com.wiser.views.configuracoes.IConfiguracoesView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 22/01/2017.
 */
public class ConfiguracoesPresenter extends Presenter<IConfiguracoesView> {

    private IUsuarioService service;

    @Override
    public void onCreate() {
        super.onCreate();
        view.onInitView();

        service = APIClient.getClient().create(IUsuarioService.class);

        view.onSetSelectionCmbIdioma(Sistema.getUsuario().getIdioma());
        view.onSetSelectionCmbFluencia(Sistema.getUsuario().getFluencia());
        view.onSetTextTxtStatus(Utils.decode(Sistema.getUsuario().getStatus()));
        view.onSetSelectionTxtStatus(view.onGetTextTxtStatus().length());
    }

    public void setTextChangedTxtStatus(int tamanho) {
        view.onSetTextLblContLetras(tamanho + " / 30");
    }

    public void salvar(){
        final Configuracoes configuracoes = new Configuracoes();
        final ParametroDAO parametroDAO = new ParametroDAO();

        configuracoes.setId(Sistema.getUsuario().getId());
        configuracoes.setIdioma(view.onGetItemIdCmbIdioma());
        configuracoes.setFluencia(String.valueOf(view.onGetItemIdCmbFluencia()));
        configuracoes.setStatus(Utils.encode(view.onGetTextTxtStatus()));

        salvar(configuracoes, new ICallback() {
            @Override
            public void onSuccess() {
                Sistema.getUsuario().setIdioma(view.onGetItemIdCmbIdioma());
                Sistema.getUsuario().setFluencia(view.onGetItemIdCmbFluencia());
                Sistema.getUsuario().setStatus(view.onGetTextTxtStatus());

                parametroDAO.update(CodigoParametro.SALVOU_CONFIGURACOES, "SIM");
            }

            @Override
            public void onError(String mensagemErro) {
                Log.e("Salvar Configurações", mensagemErro);
            }
        });
    }

    private void salvar(Configuracoes configuracoes, final ICallback callback) {

        Call<Object> call = service.salvarConfiguracoes(configuracoes);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                }
                else {
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void confirmarDesativarConta() {
        DialogConfirmar confirmar = new DialogConfirmar(view.getActivity());

        confirmar.setMensagem(view.getContext().getString(R.string.confirmar_desativar_conta));
        confirmar.setYesClick(new IDialog() {
            @Override
            public void onClick() {
                desativarConta();
            }
        });

        confirmar.show();
    }

    private void desativarConta() {
        final DialogInformar informar = new DialogInformar(view.getActivity());

        desativarConta(Sistema.getUsuario().getId(), new ICallback() {
            @Override
            public void onSuccess() {
                informar.setMensagem(view.getContext().getString(R.string.sucesso_conta_desativada));
                informar.setOkClick(new IDialog() {
                    @Override
                    public void onClick() {
                        Sistema.logout(view.getActivity());
                    }
                });
                informar.show();
            }

            @Override
            public void onError(String mensagemErro) {
                Log.e("Desativar Conta", mensagemErro);
                informar.setMensagem(view.getContext().getString(R.string.erro_desativar_conta));
                informar.show();
            }
        });
    }

    private void desativarConta(long userID, final ICallback callback) {

        Call<Object> call = service.desativarConta(userID);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess();
                }
                else {
                    callback.onError(response.message());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });

    }
}
