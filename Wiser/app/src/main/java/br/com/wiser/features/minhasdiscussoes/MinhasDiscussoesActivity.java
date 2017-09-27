package br.com.wiser.features.minhasdiscussoes;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import br.com.wiser.AbstractAppCompatActivity;
import br.com.wiser.R;
import br.com.wiser.features.discussao.DiscussaoAdapter;
import br.com.wiser.features.discussao.DiscussaoPartial;
import br.com.wiser.features.discussao.IDiscussao;
import br.com.wiser.features.novadiscussao.NovaDiscussaoActivity;
import br.com.wiser.interfaces.ICallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jefferson on 19/05/2016.
 */
public class MinhasDiscussoesActivity extends AbstractAppCompatActivity implements IDiscussao {

    private MinhasDiscussoesPresenter minhasDiscussoesPresenter;
    private DiscussaoPartial discussaoPartial;
    private DiscussaoAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btnNovaDiscussao) Button btnNovaDiscussao;
    @BindView(R.id.pgbLoading) ProgressBar pgbLoading;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_minhas_discussoes);

        minhasDiscussoesPresenter = new MinhasDiscussoesPresenter();
        minhasDiscussoesPresenter.carregarDiscussoes(new ICallback() {
            @Override
            public void onSuccess() {
                if (minhasDiscussoesPresenter.getDiscussoes().size() <= 0) {
                    showToast(getString(R.string.erro_usuario_sem_discussao));
                }
                else {
                    adapter.addItems(minhasDiscussoesPresenter.getDiscussoes());
                }

                onPrgLoadingChanged(View.INVISIBLE);
            }

            @Override
            public void onError(String mensagemErro) {
                onPrgLoadingChanged(View.INVISIBLE);
            }
        });
        discussaoPartial = new DiscussaoPartial();

        onLoad();
    }

    public void onLoad() {
        ButterKnife.bind(this);

        onPrgLoadingChanged(View.VISIBLE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DiscussaoAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        discussaoPartial.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnClick(R.id.btnNovaDiscussao)
    public void onNovaDiscussaoClicked() {
        startActivity(new Intent(this, NovaDiscussaoActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onDiscussaoClicked(int posicao) {
        discussaoPartial.onDiscussaoClicked(this, minhasDiscussoesPresenter.getDiscussao(posicao));
    }

    @Override
    public void onPerfilClicked(int posicao) {
        discussaoPartial.onPerfilClicked(this, minhasDiscussoesPresenter.getDiscussao(posicao).getUsuario());
    }

    @Override
    public void onDesativarCliked(final int posicao) {
        discussaoPartial.onDesativarCliked(this, minhasDiscussoesPresenter.getDiscussao(posicao), new ICallback() {
            @Override
            public void onSuccess() {
                adapter.updateItem(posicao);
            }

            @Override
            public void onError(String mensagemErro) {

            }
        });
    }

    @Override
    public void onCompartilharClicked(View view) {
        discussaoPartial.onCompartilharClicked(this, view);
    }

    public void onPrgLoadingChanged(int visibility) {
        pgbLoading.bringToFront();
        pgbLoading.setVisibility(visibility);
    }
}