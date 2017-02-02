package br.com.wiser.presenters.novadiscussao;

import android.content.Intent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.models.forum.Discussao;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.models.forum.IForumService;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.minhasdiscussoes.MinhasDiscussoesActivity;
import br.com.wiser.views.novadiscussao.INovaDiscussaoView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class NovaDiscussaoPresenter extends Presenter<INovaDiscussaoView> {

    private IForumService service;

    @Override
    protected void onCreate() {
        view.onInitView();

        service = APIClient.getClient().create(IForumService.class);
    }

    public void setTextChangedLblTitulo(int tamanho) {
        view.onSetTextLblContTitulo(tamanho + " / 30");
    }

    public void setTextChangedLblDescricao(int tamanho) {
        view.onSetTextLblContDescricao(tamanho + " / 250");
    }

    public void criarNovaDiscussao(final String titulo, final String descricao) {
        DialogConfirmar confirmar = new DialogConfirmar(getActivity());
        DialogInformar informar = new DialogInformar(getActivity());

        if (titulo.trim().isEmpty() || descricao.trim().isEmpty()) {
            informar.setMensagem(getContext().getString(R.string.erro_criar_discussao_campos));
            informar.show();
            return;
        }

        confirmar.setMensagem(getContext().getString(R.string.confirmar_salvar));
        confirmar.setYesClick(new IDialog() {
            @Override
            public void onClick() {
                salvarDiscussao(titulo, descricao);
            }
        });
        confirmar.show();
    }

    private void salvarDiscussao(String titulo, String descricao) {
        final DialogInformar informar = new DialogInformar(getActivity());
        Map<String, String> parametros = new HashMap<>();

        parametros.put("id", "0");
        parametros.put("usuario", String.valueOf(Sistema.getUsuario().getUserID()));
        parametros.put("titulo", titulo);
        parametros.put("descricao", descricao);
        parametros.put("data", new Date().toString());

        Call<Discussao> call = service.salvarDiscussao(parametros);
        call.enqueue(new Callback<Discussao>() {
            @Override
            public void onResponse(Call<Discussao> call, Response<Discussao> response) {
                if (response.isSuccessful()) {
                    informar.setMensagem(getContext().getString(R.string.sucesso_criar_discussao));
                    informar.setOkClick(new IDialog() {
                        @Override
                        public void onClick() {
                            getContext().startActivity(new Intent(getContext(), MinhasDiscussoesActivity.class));
                            getActivity().finish();
                        }
                    });
                    return;
                }

                onFailure(call, null);
            }

            @Override
            public void onFailure(Call<Discussao> call, Throwable t) {
                informar.setMensagem(getContext().getString(R.string.erro_criar_discussao));
                informar.show();
            }
        });
    }
}
