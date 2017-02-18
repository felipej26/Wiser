package br.com.wiser.presenters.discussao;

import android.view.View;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.models.forum.Resposta;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.models.forum.Discussao;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.models.forum.IForumService;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.utils.Utils;
import br.com.wiser.utils.UtilsDate;
import br.com.wiser.views.discussao.IDiscussaoCompletaView;
import br.com.wiser.views.discussao.IDiscussaoView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 23/01/2017.
 */
public class DiscussaoPresenter extends Presenter<IDiscussaoView> {

    private IForumService service;
    private Discussao discussao;

    public void onCreate(IDiscussaoView view, Discussao discussao) {
        super.onCreate(view);

        service = APIClient.getClient().create(IForumService.class);
        this.discussao = discussao;

        if (view instanceof IDiscussaoCompletaView) {
            carregar(discussao);
        }
    }

    private void carregar(Discussao discussao) {
        Usuario usuario = null;

        if (Sistema.getListaUsuarios().containsKey(discussao.getUsuario())) {
            usuario = Sistema.getListaUsuarios().get(discussao.getUsuario());
        }

        ((IDiscussaoCompletaView)view).onInitView();
        ((IDiscussaoCompletaView)view).onSetTextLblID("#" + discussao.getId());
        ((IDiscussaoCompletaView)view).onSetTextLblTitulo(Utils.decode(discussao.getTitulo()));
        ((IDiscussaoCompletaView)view).onSetTextLblDescricao(Utils.decode(discussao.getDescricao()));
        ((IDiscussaoCompletaView)view).onSetTextLblAutor(usuario.getPerfil().getFirstName());
        ((IDiscussaoCompletaView)view).onSetTextLblDataHora(UtilsDate.formatDate(discussao.getData(), UtilsDate.DDMMYYYY_HHMMSS));
        ((IDiscussaoCompletaView)view).onSetTextLblQntRespostas(
                getContext().getString(discussao.getListaRespostas().size() == 1 ?
                        R.string.resposta : R.string.respostas, discussao.getListaRespostas().size()));
        ((IDiscussaoCompletaView)view).onLoadProfilePicture(usuario.getPerfil().getUrlProfilePicture());

        if (!discussao.isAtiva()) {
            ((IDiscussaoCompletaView) view).onSetVisibilityFrmResponder(View.INVISIBLE);
        }

        ((IDiscussaoCompletaView)view).onLoadRespostas(discussao.getListaRespostas());
    }

    public void compartilhar(View view) {
        Utils.compartilharComoImagem(view);
    }

    public void setTextChangedTxtResposta(int tamanho) {
        ((IDiscussaoCompletaView) view).onSetTextLblQntRespostas(tamanho + " / 250");
    }

    public void enviarResposta(String resposta) {
        Map<String, String> map = new HashMap<>();

        if (resposta.trim().isEmpty()) {
            return;
        }

        ((IDiscussaoCompletaView) view).onSetVisibilityProgressBar(View.VISIBLE);

        map.put("id", String.valueOf(discussao.getId()));
        map.put("usuario", String.valueOf(Sistema.getUsuario().getUserID()));
        map.put("data", new Date().toString());
        map.put("resposta", Utils.encode(resposta.trim()));

        Call<Resposta> call = service.responderDiscussao(map);
        call.enqueue(new Callback<Resposta>() {
            @Override
            public void onResponse(Call<Resposta> call, Response<Resposta> response) {
                ((IDiscussaoCompletaView) view).onSetVisibilityProgressBar(View.INVISIBLE);

                if (response.isSuccessful()) {
                    discussao.getListaRespostas().add(response.body());
                    ((IDiscussaoCompletaView) view).onNotifyDataSetChanged();
                    ((IDiscussaoCompletaView) view).onSetTextTxtResposta("");
                }
            }

            @Override
            public void onFailure(Call<Resposta> call, Throwable t) {
                ((IDiscussaoCompletaView) view).onSetVisibilityProgressBar(View.INVISIBLE);
            }
        });

    }

    public void openPerfil(Usuario usuario) {
        DialogPerfilUsuario dialog = new DialogPerfilUsuario();
        dialog.show(view.getContext(), usuario);
    }

    public void confirmarDesativarDiscussao(final Discussao discussao) {
        DialogConfirmar confirmar = new DialogConfirmar(getActivity());

        confirmar.setYesClick(new IDialog() {
            @Override
            public void onClick() {
                desativarDiscussao(discussao);
            }
        });

        if (discussao.isAtiva()) {
            confirmar.setMensagem(getContext().getString(R.string.confirmar_desativar_discussao));
        }
        else {
            confirmar.setMensagem(getContext().getString(R.string.confirmar_reativar_discussao));
        }

        confirmar.show();
    }

    private void desativarDiscussao(Discussao discussao) {

    }
}
