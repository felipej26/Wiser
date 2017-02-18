package br.com.wiser.views.forum;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Button;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.presenters.discussao.DiscussaoPresenter;
import br.com.wiser.presenters.forum.ForumPresenter;
import br.com.wiser.views.AbstractFragment;
import br.com.wiser.views.discussao.DiscussaoCardViewAdapter;
import br.com.wiser.models.forum.Discussao;
import br.com.wiser.views.discussao.IDiscussaoView;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ForumFragment extends AbstractFragment implements IForumView, IDiscussaoView {

    private ForumPresenter forumPresenter;
    private DiscussaoPresenter discussaoPresenter;

    private View view;
    private Button btnNovaDiscussao;
    private Button btnProcurarDiscussao;
    private Button btnAtualizarDiscussoes;

    private RecyclerView recyclerView;
    private DiscussaoCardViewAdapter adapter;

    private ProgressBar pgbLoading;

    public static ForumFragment newInstance() {
        return new ForumFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.forum_principal, container, false);

        forumPresenter = new ForumPresenter();
        forumPresenter.onCreate(this);

        discussaoPresenter = new DiscussaoPresenter();
        discussaoPresenter.onCreate(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        forumPresenter.onResume();
    }

    @Override
    public void onClick(int posicao) {
        forumPresenter.startDiscussao(posicao);
    }

    @Override
    public void onClickPerfil(int posicao) {
        discussaoPresenter.openPerfil(Sistema.getListaUsuarios().get(adapter.getItem(posicao).getUsuario()));
    }

    @Override
    public void desativarDiscussao(int posicao) {
        discussaoPresenter.confirmarDesativarDiscussao(adapter.getItem(posicao), new ICallback() {
            @Override
            public void onSuccess() {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String mensagemErro) {

            }
        });
    }

    @Override
    public void compartilharDiscussao(View view) {
        discussaoPresenter.compartilhar(view);
    }

    @Override
    public void onInitView() {
        btnNovaDiscussao = (Button) view.findViewById(R.id.btnNovaDiscussao);
        btnProcurarDiscussao = (Button) view.findViewById(R.id.btnProcurarDiscussao);
        btnAtualizarDiscussoes = (Button) view.findViewById(R.id.btnAtualizarDiscussoes);

        btnNovaDiscussao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumPresenter.startNovaDiscussao();
            }
        });
        btnProcurarDiscussao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumPresenter.startProcurarDiscussao();
            }
        });
        btnAtualizarDiscussoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forumPresenter.atualizarDiscussoes();
            }
        });

        pgbLoading = (ProgressBar) view.findViewById(R.id.pgbLoading);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new DiscussaoCardViewAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoadListaDiscussoes(LinkedList<Discussao> listaDiscussao) {
        adapter.setItems(listaDiscussao);
    }

    @Override
    public void onSetVisibilityProgressBar(int visibility) {
        if (visibility == View.VISIBLE) {
            pgbLoading.bringToFront();
        }

        pgbLoading.setVisibility(visibility);
    }
}