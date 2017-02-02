package br.com.wiser.presenters.minhasdiscussoes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.models.forum.Discussao;
import br.com.wiser.models.forum.IForumService;
import br.com.wiser.presenters.Presenter;
import br.com.wiser.views.discussao.DiscussaoActivity;
import br.com.wiser.views.minhasdiscussoes.IMinhasDiscussoesView;
import br.com.wiser.views.novadiscussao.NovaDiscussaoActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class MinhasDiscussoesPresenter extends Presenter<IMinhasDiscussoesView> {

    private IForumService service;

    private LinkedList<Discussao> listaDiscussoes;

    @Override
    protected void onCreate() {
        view.onInitView();

        service = APIClient.getClient().create(IForumService.class);

        carregarDiscussoes();
    }

    private void carregarDiscussoes() {
        view.onSetVisibilityProgressBar(View.VISIBLE);

        Call<LinkedList<Discussao>> call = service.carregarDiscussoes(Sistema.getUsuario().getUserID(), true);
        call.enqueue(new Callback<LinkedList<Discussao>>() {
            @Override
            public void onResponse(Call<LinkedList<Discussao>> call, Response<LinkedList<Discussao>> response) {
                view.onSetVisibilityProgressBar(View.INVISIBLE);

                if (response.isSuccessful()) {
                    if (response.body().size() == 0) {
                        view.showToast(getContext().getString(R.string.erro_usuario_sem_discussao));
                        return;
                    }

                    listaDiscussoes = response.body();
                    view.onLoadListaDiscussoes(listaDiscussoes);
                }
            }

            @Override
            public void onFailure(Call<LinkedList<Discussao>> call, Throwable t) {
                view.onSetVisibilityProgressBar(View.INVISIBLE);
            }
        });
    }

    public void startDiscussao(int posicao) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Sistema.DISCUSSAO, listaDiscussoes.get(posicao));

        Intent i = new Intent(getContext(), DiscussaoActivity.class);
        i.putExtra(Sistema.DISCUSSAO, bundle);
        getContext().startActivity(i);
    }

    public void startNovaDiscussao() {
        getContext().startActivity(new Intent(getContext(), NovaDiscussaoActivity.class));
    }
}
