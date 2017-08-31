package br.com.wiser.features.forum;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import br.com.wiser.AbstractFragment;
import br.com.wiser.R;
import br.com.wiser.features.discussao.DiscussaoCardViewAdapter;
import br.com.wiser.features.discussao.DiscussaoPartial;
import br.com.wiser.features.discussao.IDiscussao;
import br.com.wiser.features.novadiscussao.NovaDiscussaoActivity;
import br.com.wiser.features.procurardiscussao.ProcurarDiscussaoActivity;
import br.com.wiser.interfaces.ICallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ForumFragment extends AbstractFragment implements IDiscussao {

    private ForumPresenter forumPresenter;

    @BindView(R.id.btnNovaDiscussao) Button btnNovaDiscussao;
    @BindView(R.id.btnProcurarDiscussao) Button btnProcurarDiscussao;
    @BindView(R.id.btnAtualizarDiscussoes) Button btnAtualizarDiscussoes;
    @BindView(R.id.pgbLoading) ProgressBar pgbLoading;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private DiscussaoCardViewAdapter adapter;
    private DiscussaoPartial discussaoPartial;

    private ICallback callbackCarregarDiscussoes;

    public static ForumFragment newInstance() {
        return new ForumFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forum_principal, container, false);
        discussaoPartial = new DiscussaoPartial(getActivity());

        callbackCarregarDiscussoes = new ICallback() {
            @Override
            public void onSuccess() {
                adapter.setItems(forumPresenter.getDiscussoes());
                onPrgLoadingChanged(View.INVISIBLE);
            }

            @Override
            public void onError(String mensagemErro) {
                showToast(mensagemErro);
                onPrgLoadingChanged(View.INVISIBLE);
            }
        };

        forumPresenter = new ForumPresenter();
        forumPresenter.carregarDiscussoes(callbackCarregarDiscussoes);

        ButterKnife.bind(this, view);
        onLoad();

        onPrgLoadingChanged(View.VISIBLE);

        return view;
    }

    public void onLoad() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DiscussaoCardViewAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDiscussaoClicked(int posicao) {
        discussaoPartial.onDiscussaoClicked(forumPresenter.getDiscussao(posicao));
    }

    @Override
    public void onPerfilClicked(int posicao) {
        discussaoPartial.onPerfilClicked(posicao);
    }

    @Override
    public void onDesativarCliked(int posicao) {
        discussaoPartial.onDesativarCliked(forumPresenter.getDiscussao(posicao), new ICallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String mensagemErro) {

            }
        });
    }

    @Override
    public void onCompartilharClicked(View view) {
        discussaoPartial.onCompartilharClicked(view);
    }

    @OnClick(R.id.btnNovaDiscussao)
    public void onNovaDiscussaoClicked() {
        startActivity(new Intent(getContext(), NovaDiscussaoActivity.class));
    }

    @OnClick(R.id.btnProcurarDiscussao)
    public void onProcurarDiscussaoClicked() {
        startActivity(new Intent(getContext(), ProcurarDiscussaoActivity.class));
    }

    @OnClick(R.id.btnAtualizarDiscussoes)
    public void onAtualizarDiscussoesClicked() {
        onPrgLoadingChanged(View.VISIBLE);
        forumPresenter.carregarDiscussoes(callbackCarregarDiscussoes);
    }

    public void onPrgLoadingChanged(int visibility) {
        if (visibility == View.VISIBLE) {
            pgbLoading.bringToFront();
        }

        pgbLoading.setVisibility(visibility);
    }
}