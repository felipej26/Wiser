package br.com.wiser.presenters.forum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.models.forum.Discussao;
import br.com.wiser.models.forum.IForumService;
import br.com.wiser.models.forum.Resposta;
import br.com.wiser.models.usuario.IUsuarioService;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.discussao.DiscussaoActivity;
import br.com.wiser.views.forum.IForumView;
import br.com.wiser.views.novadiscussao.NovaDiscussaoActivity;
import br.com.wiser.views.procurardiscussao.ProcurarDiscussaoActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class ForumPresenter extends Presenter<IForumView> {

    private IForumService service;
    private IUsuarioService usuarioService;

    private LinkedList<Discussao> listaDiscussoes;

    @Override
    protected void onCreate() {
        super.onCreate();
        view.onInitView();

        service = APIClient.getClient().create(IForumService.class);
        usuarioService = APIClient.getClient().create(IUsuarioService.class);

        carregarListaDiscussoes();
    }

    private void carregarListaDiscussoes() {
        Call<LinkedList<Discussao>> call = service.carregarDiscussoes(Sistema.getUsuario().getUserID(), false);
        call.enqueue(new Callback<LinkedList<Discussao>>() {
            @Override
            public void onResponse(Call<LinkedList<Discussao>> call, Response<LinkedList<Discussao>> response) {
                if (response.isSuccessful()) {
                    listaDiscussoes = response.body();

                    carregarUsuarios();
                }

                view.onSetVisibilityProgressBar(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<LinkedList<Discussao>> call, Throwable t) {
                view.onSetVisibilityProgressBar(View.INVISIBLE);
            }
        });

        view.onSetVisibilityProgressBar(View.VISIBLE);
    }

    private void carregarUsuarios() {
        List<Long> usuariosParaCarregarAPI = new ArrayList<>();

        for (Discussao discussao : listaDiscussoes) {
            if (!Sistema.getListaUsuarios().containsKey(discussao.getUsuario().getUserID())) {
                Sistema.getListaUsuarios().put(discussao.getUsuario().getUserID(), discussao.getUsuario());
                usuariosParaCarregarAPI.add(discussao.getUsuario().getUserID());
            }

            for (Resposta resposta : discussao.getListaRespostas()) {
                if (!Sistema.getListaUsuarios().containsKey(resposta.getUsuario())) {
                    Sistema.getListaUsuarios().put(resposta.getUsuario(), new Usuario(resposta.getUsuario()));
                    usuariosParaCarregarAPI.add(resposta.getUsuario());
                }
            }
        }

        if (usuariosParaCarregarAPI.size() > 0) {
            Call<List<Usuario>> call = usuarioService.carregarUsuarios(Sistema.getUsuario().getUserID(), usuariosParaCarregarAPI.toArray());
            call.enqueue(new Callback<List<Usuario>>() {
                @Override
                public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                    if (response.isSuccessful()) {
                        for (Usuario usuario : response.body()) {
                            if (Sistema.getListaUsuarios().containsKey(usuario.getUserID())) {
                                Sistema.getListaUsuarios().put(usuario.getUserID(), usuario);
                            }
                        }

                        carregarPerfis();
                    } else {
                        Log.e("Carregar Perfis", response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Usuario>> call, Throwable t) {
                    Log.e("Carregar Perfis", "Erro ao carregar os usuarios", t);
                }
            });
        }
        else {
            carregarPerfis();
        }
    }

    public void carregarPerfis() {
        Facebook facebook = new Facebook(getContext());

        facebook.carregarUsuarios(Sistema.getListaUsuarios().values(), new ICallback() {
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

    public void atualizarDiscussoes() {
        carregarListaDiscussoes();
    }

    public void startProcurarDiscussao() {
        getContext().startActivity(new Intent(getContext(), ProcurarDiscussaoActivity.class));
    }

    public void startNovaDiscussao() {
        getContext().startActivity(new Intent(getContext(), NovaDiscussaoActivity.class));
    }

    public void startDiscussao(int posicao) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Sistema.DISCUSSAO, listaDiscussoes.get(posicao));

        Intent i = new Intent(getContext(), DiscussaoActivity.class);
        i.putExtra(Sistema.DISCUSSAO, bundle);
        getContext().startActivity(i);
    }
}
