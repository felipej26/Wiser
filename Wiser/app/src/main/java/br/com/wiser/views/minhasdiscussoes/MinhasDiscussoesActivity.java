package br.com.wiser.views.minhasdiscussoes;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.models.forum.Discussao;
import br.com.wiser.presenters.discussao.DiscussaoPresenter;
import br.com.wiser.presenters.minhasdiscussoes.MinhasDiscussoesPresenter;
import br.com.wiser.views.AbstractActivity;
import br.com.wiser.views.discussao.DiscussaoCardViewAdapter;
import br.com.wiser.views.discussao.IDiscussaoView;

/**
 * Created by Jefferson on 19/05/2016.
 */
public class MinhasDiscussoesActivity extends AbstractActivity implements IMinhasDiscussoesView, IDiscussaoView {

    private MinhasDiscussoesPresenter minhasDiscussoesPresenter;
    private DiscussaoPresenter discussaoPresenter;

    private Button btnNovaDiscussao;

    private RecyclerView recyclerView;
    private DiscussaoCardViewAdapter adapter;
    private ProgressBar pgbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_minhas_discussoes);

        minhasDiscussoesPresenter = new MinhasDiscussoesPresenter();
        minhasDiscussoesPresenter.onCreate(this);

        discussaoPresenter = new DiscussaoPresenter();
        discussaoPresenter.onCreate(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(int posicao) {
        minhasDiscussoesPresenter.startDiscussao(posicao);
    }

    @Override
    public void onClickPerfil(int posicao) {
        discussaoPresenter.openPerfil(adapter.getItem(posicao).getUsuario());
    }

    @Override
    public void desativarDiscussao(int posicao) {
        discussaoPresenter.confirmarDesativarDiscussao(adapter.getItem(posicao));
    }

    @Override
    public void compartilharDiscussao(View view) {
        discussaoPresenter.compartilhar(view);
    }

    @Override
    public void onInitView() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        btnNovaDiscussao = (Button) findViewById(R.id.btnNovaDiscussao);
        btnNovaDiscussao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minhasDiscussoesPresenter.startNovaDiscussao();
            }
        });

        pgbLoading = (ProgressBar) findViewById(R.id.pgbLoading);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MinhasDiscussoesActivity.this));

        adapter = new DiscussaoCardViewAdapter(MinhasDiscussoesActivity.this, new LinkedList<Discussao>());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoadListaDiscussoes(LinkedList<Discussao> listaDiscussoes) {
        adapter.setItems(listaDiscussoes);
    }

    @Override
    public void onSetVisibilityProgressBar(int visibility) {
        if (visibility == View.VISIBLE) {
            pgbLoading.bringToFront();
        }

        pgbLoading.setVisibility(visibility);
    }
}