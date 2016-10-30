package br.com.wiser.activity.forum.minhas_discussoes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.activity.forum.DiscussaoCardViewAdapter;
import br.com.wiser.activity.forum.IDiscussao;
import br.com.wiser.activity.forum.discussao.ForumDiscussaoActivity;
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.enums.Activities;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.utils.Utils;

/**
 * Created by Jefferson on 19/05/2016.
 */
public class ForumMinhasDiscussoesActivity extends Activity implements IDiscussao {

    private DiscussaoDAO objDiscussao;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressBar pgbLoading;

    private LinkedList<DiscussaoDAO> listaDiscussoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_minhas_discussoes);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        objDiscussao = new DiscussaoDAO();

        pgbLoading = (ProgressBar) findViewById(R.id.pgbLoading);

        carregarDados();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void carregarDados(){

        final Handler hCarregar = new Handler();

        pgbLoading.setVisibility(View.VISIBLE);
        pgbLoading.bringToFront();

        new Thread(new Runnable() {
            @Override
            public void run() {
                listaDiscussoes = objDiscussao.carregarMinhasDiscussoes(ForumMinhasDiscussoesActivity.this);

                hCarregar.post(new Runnable() {
                    @Override
                    public void run() {
                        if(listaDiscussoes == null || listaDiscussoes.isEmpty()){
                            Toast.makeText(ForumMinhasDiscussoesActivity.this, getString(R.string.erro_usuario_sem_discussao), Toast.LENGTH_LONG);
                            return;
                        }

                        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ForumMinhasDiscussoesActivity.this));

                        adapter = new DiscussaoCardViewAdapter(ForumMinhasDiscussoesActivity.this, listaDiscussoes);
                        recyclerView.setAdapter(adapter);

                        pgbLoading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    public void chamarNovaDiscussao(View view) {
        Utils.chamarActivity((Activity) view.getContext(), Activities.FORUM_NOVA_DISCUSSAO);
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
            confirmar.setMensagem(getString(R.string.confirmar_desativar_discussao));
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