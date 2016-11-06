package br.com.wiser.activity.forum.pesquisa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.activity.forum.DiscussaoCardViewAdapter;
import br.com.wiser.activity.forum.IDiscussao;
import br.com.wiser.activity.forum.discussao.ForumDiscussaoActivity;
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.utils.Utils;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class ForumPesquisaActivity extends Activity implements IDiscussao {

    private EditText txtDiscussao;
    private TextView lblResultados;
    private TextView lblContResultados;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ProgressBar pgbLoading;

    private DiscussaoDAO objDiscussao;
    private LinkedList<DiscussaoDAO> listaDiscussoes;

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
                listaDiscussoes = objDiscussao.procurarDiscussoes(context, procurar);

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
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                        adapter = new DiscussaoCardViewAdapter(ForumPesquisaActivity.this, listaDiscussoes);
                        recyclerView.setAdapter(adapter);

                        pgbLoading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
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

    @Override
    public void onClick(int posicao) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("discussao", listaDiscussoes.get(posicao));

        Intent i = new Intent(this, ForumDiscussaoActivity.class);
        i.putExtra("discussoes", bundle);
        startActivity(i);
    }

    @Override
    public void onClickPerfil(int posicao) {
        DialogPerfilUsuario dialog = new DialogPerfilUsuario();
        dialog.show(this, listaDiscussoes.get(posicao).getUsuario());
    }

    @Override
    public void desativarDiscussao(final int posicao) {
        DialogConfirmar confirmar = new DialogConfirmar(this);

        confirmar.setYesClick(new IDialog() {
            @Override
            public void onClick() {
                desativar(listaDiscussoes.get(posicao));
            }
        });

        if (listaDiscussoes.get(posicao).isAtiva()) {
            confirmar.setMensagem(this.getString(R.string.confirmar_desativar_discussao));
        }
        else {
            confirmar.setMensagem(getString(R.string.confirmar_reativar_discussao));
        }

        confirmar.show();
    }

    @Override
    public void compartilharDiscussao(View view) {
        Utils.compartilharComoImagem(view);
    }
}
