package br.com.wiser.features.discussao;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.wiser.AbstractActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.Utils;
import br.com.wiser.utils.UtilsDate;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class DiscussaoActivity extends AbstractActivity {

    private DiscussaoPresenter discussaoPresenter;
    private DiscussaoRespostaAdapter adapter;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.lblIDDiscussao) TextView lblIDDiscussao;
    @BindView(R.id.imgPerfil) ImageView imgPerfil;
    @BindView(R.id.prgBarra) ProgressBar prgBarra;
    @BindView(R.id.lblTituloDiscussao) TextView lblTituloDiscussao;
    @BindView(R.id.lblDescricaoDiscussao) TextView lblDescricaoDiscussao;
    @BindView(R.id.lblAutor) TextView lblAutor;
    @BindView(R.id.lblDataHora) TextView lblDataHora;
    @BindView(R.id.lblRespostas) TextView lblRespostas;
    @BindView(R.id.txtResposta) EditText txtResposta;
    @BindView(R.id.pgbLoading) ProgressBar pgbLoading;
    @BindView(R.id.lblContResposta) TextView lblContResposta;
    @BindView(R.id.btnEnviarResposta) Button btnEnviarResposta;
    @BindView(R.id.lblSugestao) View lblSugestao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_discussao);

        discussaoPresenter = new DiscussaoPresenter((Discussao) getIntent().getBundleExtra(Sistema.DISCUSSAO).get(Sistema.DISCUSSAO));

        onLoad();
        onLoadData();

        onPrgLoadingChanged(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_discussoes, menu);
        return (true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.itmCompartilhar:
                Utils.compartilharComoImagem(findViewById(R.id.frmDiscussao));
                break;
            default:
                onBackPressed();
        }

        return true;
    }

    private void onLoad() {
        ButterKnife.bind(this);

        onPrgLoadingChanged(View.VISIBLE);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        ((ViewGroup) lblSugestao.getParent()).removeView(lblSugestao);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setFocusable(false);

        adapter = new DiscussaoRespostaAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void onLoadData() {
        Discussao discussao = discussaoPresenter.getDiscussao();

        Utils.loadImageInBackground(this, discussao.getUsuario().getUrlFotoPerfil(), imgPerfil, prgBarra);
        lblIDDiscussao.setText("#" + discussao.getId());
        lblTituloDiscussao.setText(Utils.decode(discussao.getTitulo()));
        lblDescricaoDiscussao.setText(Utils.decode(discussao.getDescricao()));
        lblAutor.setText(discussao.getUsuario().getPrimeiroNome());
        lblDataHora.setText(UtilsDate.formatDate(discussao.getData(), UtilsDate.DDMMYYYY_HHMMSS));
        lblContResposta.setText(getString(discussao.getListaRespostas().size() == 1 ?
                R.string.resposta : R.string.respostas, discussao.getListaRespostas().size())
        );

        if (!discussao.isAtiva()) {
            findViewById(R.id.include).setVisibility(View.INVISIBLE);
        }

        adapter.addItems(discussao.getListaRespostas());
    }

    @OnClick(R.id.btnEnviarResposta)
    public void onEnviarClicked() {
        onPrgLoadingChanged(View.VISIBLE);
        discussaoPresenter.enviarResposta(txtResposta.getText().toString(), new ICallback() {
            @Override
            public void onSuccess() {
                txtResposta.setText("");
                adapter.addItem(discussaoPresenter.getDiscussao().getListaRespostas().getLast());
                onPrgLoadingChanged(View.INVISIBLE);
            }

            @Override
            public void onError(String mensagemErro) {
                onPrgLoadingChanged(View.INVISIBLE);
            }
        });
    }

    @OnTextChanged(value = R.id.txtResposta, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onRespostaTextChanged(Editable editable) {
        lblContResposta.setText(editable.length() + " / 250");
    }

    private void onPrgLoadingChanged(int visibility) {
        pgbLoading.bringToFront();
        pgbLoading.setVisibility(visibility);
    }
}
