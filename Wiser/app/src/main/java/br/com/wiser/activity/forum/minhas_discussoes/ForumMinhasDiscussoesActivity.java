package br.com.wiser.activity.forum.minhas_discussoes;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.activity.forum.DiscussaoCardViewAdapter;
import br.com.wiser.activity.forum.IDiscussaoCardViewAdapterCallback;
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.enums.Activities;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.utils.Utils;

/**
 * Created by Jefferson on 19/05/2016.
 */
public class ForumMinhasDiscussoesActivity extends Activity implements IDiscussaoCardViewAdapterCallback {

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

        carregarDiscussoes();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void carregarDiscussoes(){

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
                        recyclerView.setVisibility(View.INVISIBLE);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(ForumMinhasDiscussoesActivity.this));

                        adapter = new DiscussaoCardViewAdapter(ForumMinhasDiscussoesActivity.this, listaDiscussoes);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);

                        pgbLoading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    public void chamarNovaDiscussao(View view) {
        Utils.chamarActivity((Activity) view.getContext(), Activities.FORUM_NOVA_DISCUSSAO);
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