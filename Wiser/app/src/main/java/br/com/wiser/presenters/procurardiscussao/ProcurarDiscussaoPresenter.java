package br.com.wiser.presenters.procurardiscussao;

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
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.discussao.DiscussaoActivity;
import br.com.wiser.views.procurardiscussao.IProcurarDiscussaoActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class ProcurarDiscussaoPresenter extends Presenter<IProcurarDiscussaoActivity> {

    private IForumService service;
    private IUsuarioService usuarioService;

    private LinkedList<Discussao> listaDiscussoes;

    @Override
    protected void onCreate() {
        super.onCreate();
        view.onInitView();

        service = APIClient.getClient().create(IForumService.class);
        usuarioService = APIClient.getClient().create(IUsuarioService.class);
    }

    public void startDiscussao(int posicao) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Sistema.DISCUSSAO, listaDiscussoes.get(posicao));

        Intent i = new Intent(getContext(), DiscussaoActivity.class);
        i.putExtra(Sistema.DISCUSSAO, bundle);
        getContext().startActivity(i);
    }

    public void procurarDiscussao(final String chave) {
        view.onSetVisibilityProgressBar(View.VISIBLE);

        Call<LinkedList<Discussao>> call = service.procurarDiscussoes(Sistema.getUsuario().getUserID(), chave);
        call.enqueue(new Callback<LinkedList<Discussao>>() {
            @Override
            public void onResponse(Call<LinkedList<Discussao>> call, Response<LinkedList<Discussao>> response) {
                if (response.isSuccessful()) {
                    listaDiscussoes = response.body();

                    long quantidadeEncontrada = listaDiscussoes.size();

                    view.onSetTextLblResultados(getContext().getString(R.string.resultados_para, chave));
                    view.onSetTextLblContResultados(getContext().getString(R.string.cont_discussoes, quantidadeEncontrada) + " " +
                            (quantidadeEncontrada == 1 ? getContext().getString(R.string.discussao_encontrada) :
                                    getContext().getString(R.string.discussoes_encontradas)));

                    view.onSetVisibilityLblResultados(View.VISIBLE);
                    view.onSetVisibilityLblContResultados(View.VISIBLE);
                    view.onSetVisibilityProgressBar(View.INVISIBLE);

                    carregarDiscussoes();
                }
                else {
                    onFailure(call, null);
                }
            }

            @Override
            public void onFailure(Call<LinkedList<Discussao>> call, Throwable t) {
                view.showToast(getContext().getString(R.string.erro_discussao_nao_encontrada));
                view.onSetVisibilityProgressBar(View.INVISIBLE);
            }
        });


    }

    private void carregarDiscussoes() {
        List<Long> usuariosParaCarregarAPI = new ArrayList<>();

        for (Discussao discussao : listaDiscussoes) {
            if (!Sistema.getListaUsuarios().containsKey(discussao.getUsuario())) {
                Sistema.getListaUsuarios().put(discussao.getUsuario(), new Usuario(discussao.getUsuario()));
                usuariosParaCarregarAPI.add(discussao.getUsuario());
            }

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
}
