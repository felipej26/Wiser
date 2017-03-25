package br.com.wiser.presenters.perfilcompleto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.models.forum.Discussao;
import br.com.wiser.models.forum.IForumService;
import br.com.wiser.models.forum.Resposta;
import br.com.wiser.models.usuario.IUsuarioService;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.models.contatos.IContatosService;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.utils.Utils;
import br.com.wiser.views.discussao.DiscussaoActivity;
import br.com.wiser.features.mensagem.MensagemActivity;
import br.com.wiser.views.perfilcompleto.IPerfilCompletoView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 23/01/2017.
 */
public class PerfilCompletoPresenter extends Presenter<IPerfilCompletoView> {

    private IContatosService service;
    private IUsuarioService usuarioService;
    private IForumService forumService;

    private Usuario usuario;

    private LinkedList<Discussao> listaDiscussoes;

    public void onCreate(IPerfilCompletoView view, Usuario usuario) {
        super.onCreate(view);
        view.onInitView();

        service = APIClient.getClient().create(IContatosService.class);
        usuarioService = APIClient.getClient().create(IUsuarioService.class);
        forumService = APIClient.getClient().create(IForumService.class);

        this.usuario = usuario;

        view.onSetVisibilityProgressBar(View.VISIBLE);

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

        carregarDiscussoes();
    }

    private void carregarDiscussoes() {

        Call<LinkedList<Discussao>> call = forumService.carregarDiscussoes(usuario.getUserID(), true);
        call.enqueue(new Callback<LinkedList<Discussao>>() {
            @Override
            public void onResponse(Call<LinkedList<Discussao>> call, Response<LinkedList<Discussao>> response) {
                view.onSetVisibilityProgressBar(View.INVISIBLE);

                if (response.isSuccessful()) {
                    if (response.body().size() == 0) {
                        return;
                    }

                    listaDiscussoes = response.body();
                    carregarUsuarios();
                }
            }

            @Override
            public void onFailure(Call<LinkedList<Discussao>> call, Throwable t) {
                view.onSetVisibilityProgressBar(View.INVISIBLE);
            }
        });
    }

    private void carregarUsuarios() {
        List<Long> usuariosParaCarregarAPI = new ArrayList<>();

        for (Discussao discussao : listaDiscussoes) {
            for (Resposta resposta : discussao.getListaRespostas()) {
                if (!Sistema.getListaUsuarios().containsKey(resposta.getUsuario())) {
                    Sistema.getListaUsuarios().put(resposta.getUsuario(), new Usuario(resposta.getUsuario()));
                    usuariosParaCarregarAPI.add(resposta.getUsuario());
                }
            }
        }

        Sistema.carregarUsuarios(getContext(), usuariosParaCarregarAPI, new ICallback() {
            @Override
            public void onSuccess() {
                view.onLoadListaDiscussoes(listaDiscussoes);
            }

            @Override
            public void onError(String mensagemErro) {
                Log.e("Carregar Perfis", mensagemErro);
            }
        });
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
        Conversa conversa = new Conversa();
        conversa.setDestinatario(usuario.getUserID());

        Intent i = new Intent(getContext(), MensagemActivity.class);
        i.putExtra(Sistema.CONVERSA, conversa);
        getContext().startActivity(i);
    }

    public void openDiscussao(int posicao) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Sistema.DISCUSSAO, listaDiscussoes.get(posicao));

        Intent i = new Intent(getContext(), DiscussaoActivity.class);
        i.putExtra(Sistema.DISCUSSAO, bundle);
        getContext().startActivity(i);
    }
}
