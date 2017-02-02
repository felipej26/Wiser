package br.com.wiser.presenters.perfilcompleto;

import android.content.Intent;
import android.util.Log;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.models.contatos.IContatosService;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.utils.Utils;
import br.com.wiser.views.mensagens.MensagensActivity;
import br.com.wiser.views.perfilcompleto.IPerfilCompletoView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 23/01/2017.
 */
public class PerfilCompletoPresenter extends Presenter<IPerfilCompletoView> {

    private IContatosService service;
    private Usuario usuario;

    public void onCreate(IPerfilCompletoView view, Usuario usuario) {
        super.onCreate(view);
        view.onInitView();

        service = APIClient.getClient().create(IContatosService.class);
        this.usuario = usuario;

        if (Sistema.getUsuario().getUserID() == usuario.getUserID()) {
            view.onLoadAsUser();
        }
        else if (usuario.isContato()) {
            view.onLoadAsFriend(usuario);
        }
        else {
            view.onLoadAsNotFriend(usuario);
        }

        view.onLoadProfilePicture(usuario.getPerfil().getUrlProfilePicture());
        view.onSetTextLblNome(usuario.getPerfil().getFullName());
        view.onSetTextLblIdade(", " + usuario.getPerfil().getIdade());
        view.onSetTextLblIdiomaNivel(view.getContext().getString(R.string.fluencia_idioma,
                Sistema.getDescricaoFluencia(usuario.getFluencia()), Sistema.getDescricaoIdioma(usuario.getIdioma())));
        view.onSetTextLblStatus(Utils.decode(usuario.getStatus()));
    }

    public void adicionarContato() {

        Call<Object> call = service.adicionarContato(Sistema.getUsuario().getUserID(), usuario.getUserID());
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    view.onLoadAsFriend(usuario);
                }
                else {
                    Log.e("Adicionar Contato", response.message());
                    view.showToast("Falha ao adicionar contato");
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("Adicionar Contato", t.getMessage(), t);
                view.showToast("Falha ao adicionar contato");
            }
        });
    }

    public void openChat() {
        Conversas conversa = new Conversas();
        conversa.setDestinatario(usuario);

        Intent i = new Intent(getContext(), MensagensActivity.class);
        i.putExtra(Sistema.CONVERSA, conversa);
        getContext().startActivity(i);
    }
}
