package br.com.wiser.features.procurardiscussao;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import br.com.wiser.AbstractActivity;
import br.com.wiser.R;
import br.com.wiser.features.discussao.DiscussaoAdapter;
import br.com.wiser.features.discussao.DiscussaoPartial;
import br.com.wiser.features.discussao.IDiscussao;
import br.com.wiser.interfaces.ICallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ProcurarDiscussaoActivity extends AbstractActivity implements IDiscussao {

    private ProcurarDiscussaoPresenter procurarDiscussaoPresenter;
    private DiscussaoPartial discussaoPartial;
    private DiscussaoAdapter adapter;

    @BindView(R.id.txtDiscussao) EditText txtDiscussao;
    @BindView(R.id.lblResultados) TextView lblResultados;
    @BindView(R.id.lblContResultados) TextView lblContResultados;
    @BindView(R.id.btnProcurarDiscussao) Button btnProcurarDiscussao;
    @BindView(R.id.pgbLoading) ProgressBar pgbLoading;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.forum_pesquisa);

        procurarDiscussaoPresenter = new ProcurarDiscussaoPresenter();
        discussaoPartial = new DiscussaoPartial();

        onLoad();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void onLoad() {
        ButterKnife.bind(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        lblResultados.setVisibility(View.INVISIBLE);
        lblContResultados.setVisibility(View.INVISIBLE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DiscussaoAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.btnProcurarDiscussao)
    public void onProcurarClicked() {
        onPrgLoadingChanged(View.VISIBLE);
        procurarDiscussaoPresenter.procurarDiscussao(txtDiscussao.getText().toString(), new ICallback() {
            @Override
            public void onSuccess() {
                adapter.limparLista();
                adapter.addItems(procurarDiscussaoPresenter.getDiscussoes());

                String quantidadeEncontrada = String.valueOf(procurarDiscussaoPresenter.getDiscussoes().size());

                lblResultados.setText(getString(R.string.resultados_para, txtDiscussao.getText().toString()));
                lblResultados.setVisibility(View.VISIBLE);

                lblContResultados.setText(getString(R.string.cont_discussoes, quantidadeEncontrada) + " " +
                        (Integer.valueOf(quantidadeEncontrada) == 1 ? getContext().getString(R.string.discussao_encontrada) :
                                getContext().getString(R.string.discussoes_encontradas)));
                lblContResultados.setVisibility(View.VISIBLE);

                onPrgLoadingChanged(View.INVISIBLE);
            }

            @Override
            public void onError(String mensagemErro) {
                Toast.makeText(ProcurarDiscussaoActivity.this, R.string.erro_discussao_nao_encontrada, Toast.LENGTH_SHORT);
                onPrgLoadingChanged(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onDiscussaoClicked(int posicao) {
        discussaoPartial.onDiscussaoClicked(this, procurarDiscussaoPresenter.getDiscussao(posicao));
    }

    @Override
    public void onPerfilClicked(int posicao) {
        discussaoPartial.onPerfilClicked(this, procurarDiscussaoPresenter.getDiscussao(posicao).getUsuario());
    }

    @Override
    public void onDesativarCliked(final int posicao) {
        discussaoPartial.onDesativarCliked(this, procurarDiscussaoPresenter.getDiscussao(posicao), new ICallback() {
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
        discussaoPartial.onCompartilharClicked(view);
    }

    public void onPrgLoadingChanged(int visibility) {
        pgbLoading.bringToFront();
        pgbLoading.setVisibility(visibility);
    }
}
