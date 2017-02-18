package br.com.wiser.views.procurardiscussao;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.models.forum.Discussao;
import br.com.wiser.presenters.discussao.DiscussaoPresenter;
import br.com.wiser.presenters.procurardiscussao.ProcurarDiscussaoPresenter;
import br.com.wiser.views.AbstractActivity;
import br.com.wiser.views.discussao.DiscussaoCardViewAdapter;
import br.com.wiser.views.discussao.IDiscussaoView;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ProcurarDiscussaoActivity extends AbstractActivity implements IProcurarDiscussaoActivity, IDiscussaoView {

    private ProcurarDiscussaoPresenter procurarDiscussaoPresenter;
    private DiscussaoPresenter discussaoPresenter;

    private EditText txtDiscussao;
    private TextView lblResultados;
    private TextView lblContResultados;
    private Button btnProcurarDiscussao;

    private RecyclerView recyclerView;
    private DiscussaoCardViewAdapter adapter;

    private ProgressBar pgbLoading;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.forum_pesquisa);

        procurarDiscussaoPresenter = new ProcurarDiscussaoPresenter();
        procurarDiscussaoPresenter.onCreate(this);

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
        procurarDiscussaoPresenter.startDiscussao(posicao);
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
        getActionBar().setDisplayHomeAsUpEnabled(true);

        txtDiscussao = (EditText) findViewById(R.id.txtDiscussao);
        lblResultados = (TextView) findViewById(R.id.lblResultados);
        lblContResultados = (TextView) findViewById(R.id.lblContResultados);

        lblResultados.setVisibility(View.INVISIBLE);
        lblContResultados.setVisibility(View.INVISIBLE);
        btnProcurarDiscussao = (Button) findViewById(R.id.btnProcurarDiscussao);
        btnProcurarDiscussao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procurarDiscussaoPresenter.procurarDiscussao(txtDiscussao.getText().toString());
            }
        });

        pgbLoading = (ProgressBar)findViewById(R.id.pgbLoading);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DiscussaoCardViewAdapter(this);
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

    @Override
    public void onSetTextLblResultados(String texto) {
        lblResultados.setText(texto);
    }

    @Override
    public void onSetTextLblContResultados(String texto) {
        lblContResultados.setText(texto);
    }

    @Override
    public void onSetVisibilityLblResultados(int visibility) {
        lblResultados.setVisibility(visibility);
    }

    @Override
    public void onSetVisibilityLblContResultados(int visibility) {
        lblContResultados.setVisibility(visibility);
    }
}
