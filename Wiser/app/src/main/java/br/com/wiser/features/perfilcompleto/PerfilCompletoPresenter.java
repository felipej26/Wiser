package br.com.wiser.features.perfilcompleto;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.com.wiser.APIClient;
import br.com.wiser.Presenter;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.contato.ContatoDAO;
import br.com.wiser.features.contato.IContatosService;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.features.discussao.Discussao;
import br.com.wiser.features.discussao.DiscussaoActivity;
import br.com.wiser.features.discussao.IForumService;
import br.com.wiser.features.mensagem.MensagemActivity;
import br.com.wiser.features.usuario.IUsuarioService;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.utils.Utils;
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
ContatoDAO contatoDAO = new ContatoDAO();
        service = APIClient.getClient().create(IContatosService.class);
        usuarioService = APIClient.getClient().create(IUsuarioService.class);
        forumService = APIClient.getClient().create(IForumService.class);

        this.usuario = usuario;

        view.onSetVisibilityProgressBar(View.VISIBLE);

        if (Sistema.getUsuario().getId() == usuario.getId()) {
            view.onLoadAsUser();
        }
        else if (contatoDAO.isContato(usuario.getId())) {
            view.onLoadAsFriend(usuario);
        }
        else {
            view.onLoadAsNotFriend(usuario);
        }

        view.onLoadProfilePicture(usuario.getUrlFotoPerfil());
        view.onSetTextLblNome(usuario.getNome());
        view.onSetTextLblIdade(", " + 18); //usuario.getPerfil().getIdade());
        view.onSetTextLblIdiomaNivel(view.getContext().getString(R.string.fluencia_idioma,
                Sistema.getDescricaoFluencia(usuario.getFluencia()), Sistema.getDescricaoIdioma(usuario.getIdioma())));
        view.onSetTextLblStatus(Utils.decode(usuario.getStatus()));

        carregarDiscussoes();
    }

    private void carregarDiscussoes() {

        Call<LinkedList<Discussao>> call = forumService.carregarDiscussoes(usuario.getId(), true);
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

        /*
        for (Discussao discussao : listaDiscussoes) {
            for (Resposta resposta : discussao.getListaRespostas()) {
                if (!Sistema.getListaUsuarios().containsKey(resposta.getDestinatario())) {
                    Sistema.getListaUsuarios().put(resposta.getDestinatario(), new Usuario(resposta.getDestinatario()));
                    usuariosParaCarregarAPI.add(resposta.getDestinatario());
                }
            }
        }

        Sistema.carregarUsuarios(getAppContext(), usuariosParaCarregarAPI, new ICallback() {
            @Override
            public void onFinish() {
                view.onLoadListaDiscussoes(listaDiscussoes);
            }

            @Override
            public void onError(String mensagemErro) {
                Log.e("Carregar Perfis", mensagemErro);
            }
        });
        */
    }

    public void adicionarContato() {

        Call<Object> call = service.adicionarContato(Sistema.getUsuario().getId(), usuario.getId());
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
        conversa.setUsuario(usuario);

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
