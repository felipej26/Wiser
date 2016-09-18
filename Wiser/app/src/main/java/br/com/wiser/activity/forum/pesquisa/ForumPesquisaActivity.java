package br.com.wiser.activity.forum.pesquisa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.LinkedList;

import br.com.wiser.Sistema;
import br.com.wiser.R;
import br.com.wiser.activity.forum.DiscussaoCardViewAdapter;
import br.com.wiser.activity.forum.IDiscussaoCardViewAdapterCallback;
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogInformar;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ForumPesquisaActivity extends Activity implements IDiscussaoCardViewAdapterCallback {

    private EditText txtDiscussao;
    private TextView lblResultados;
    private TextView lblContResultados;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ProgressBar pgbLoading;

    private DiscussaoDAO objDiscussao;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.forum_pesquisa);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        txtDiscussao = (EditText) findViewById(R.id.txtDiscussao);
        lblResultados = (TextView) findViewById(R.id.lblResultados);
        lblContResultados = (TextView) findViewById(R.id.lblContResultados);

        lblResultados.setVisibility(View.INVISIBLE);
        lblContResultados.setVisibility(View.INVISIBLE);

        pgbLoading = (ProgressBar)findViewById(R.id.pgbLoading);

        objDiscussao = new DiscussaoDAO();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void procurarDiscussao(View view) {
        final String procurar = txtDiscussao.getText().toString().trim();

        pgbLoading = (ProgressBar)findViewById(R.id.pgbLoading);
        pgbLoading.setVisibility(View.VISIBLE);
        pgbLoading.bringToFront();

        final Context context = this;
        final Handler hRecycleView = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final LinkedList<DiscussaoDAO> listaDiscussoes = objDiscussao.procurarDiscussoes(context, procurar);

                hRecycleView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(listaDiscussoes == null || listaDiscussoes.isEmpty()){
                            Toast.makeText(context, getString(R.string.erro_discussao_nao_encontrada), Toast.LENGTH_LONG).show();
                            pgbLoading.setVisibility(View.INVISIBLE);
                            return;
                        }

                        long cont = listaDiscussoes.size();

                        lblResultados.setText(getString(R.string.resultados_para, txtDiscussao.getText()));
                        lblContResultados.setText(getString(R.string.cont_discussoes, cont) + " " +
                                (cont == 1 ? getString(R.string.discussao_encontrada) :
                                        getString(R.string.discussoes_encontradas)));

                        lblResultados.setVisibility(View.VISIBLE);
                        lblContResultados.setVisibility(View.VISIBLE);

                        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                        recyclerView.setVisibility(View.INVISIBLE);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                        adapter = new DiscussaoCardViewAdapter(ForumPesquisaActivity.this, listaDiscussoes);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);

                        pgbLoading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    @Override
    public void desativarDiscussao(final Discussao discussao) {
        DialogConfirmar confirmar = new DialogConfirmar(this);

        confirmar.setYesClick(new DialogConfirmar.DialogInterface() {
            @Override
            public void onClick() {
                desativar(discussao);
            }
        });

        if (discussao.isAtiva()) {
            confirmar.setMensagem(this.getString(R.string.confirmar_desativar_discussao));
        }
        else {
            confirmar.setMensagem(getString(R.string.confirmar_reativar_discussao));
        }

        confirmar.show();
    }

    public void desativar(Discussao discussao) {
        DialogInformar informar = new DialogInformar(this);

        DiscussaoDAO objDiscussao = new DiscussaoDAO();

        objDiscussao.setId(discussao.getId());
        objDiscussao.setAtiva(!discussao.isAtiva());

        if (objDiscussao.desativarDiscussao(this)) {
            discussao.setAtiva(objDiscussao.isAtiva());

            informar.setMensagem(getString(R.string.sucesso_discussao_excluida));
        }
        else {
            informar.setMensagem(getString(R.string.erro_excluir_discussao));
        }

        informar.show();
    }
}
