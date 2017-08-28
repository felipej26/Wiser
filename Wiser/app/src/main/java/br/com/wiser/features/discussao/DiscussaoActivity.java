package br.com.wiser.features.discussao;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.utils.Utils;
import br.com.wiser.AbstractActivity;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class DiscussaoActivity extends AbstractActivity implements IDiscussaoCompletaView {

    private DiscussaoPresenter discussaoPresenter;

    private RecyclerView recyclerView;
    private DiscussaoRespostaAdapter adapter;

    private TextView lblIDDiscussao;
    private ImageView imgPerfil;
    private ProgressBar prgBarra;
    private TextView lblTituloDiscussao;
    private TextView lblDescricaoDiscussao;
    private TextView lblAutor;
    private TextView lblDataHora;
    private TextView lblRespostas;
    private EditText txtResposta;
    private ProgressBar pgbLoading;
    private TextView lblContResposta;

    private Button btnEnviarResposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_discussao);

        discussaoPresenter = new DiscussaoPresenter();
        discussaoPresenter.onCreate(this, (Discussao) getIntent().getBundleExtra(Sistema.DISCUSSAO).get(Sistema.DISCUSSAO));
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
                compartilharDiscussao(findViewById(R.id.frmDiscussao));
                break;
            default:
                onBackPressed();
        }

        return true;
    }

    @Override
    public void onInitView() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        lblIDDiscussao = (TextView) findViewById(R.id.lblIDDiscussao);
        imgPerfil = (ImageView) findViewById(R.id.imgPerfil);
        prgBarra = (ProgressBar) findViewById(R.id.prgBarra);
        lblTituloDiscussao = (TextView) findViewById(R.id.lblTituloDiscussao);
        lblDescricaoDiscussao = (TextView) findViewById(R.id.lblDescricaoDiscussao);
        lblAutor = (TextView) findViewById(R.id.lblAutor);
        lblDataHora = (TextView) findViewById(R.id.lblDataHora);
        lblRespostas = (TextView) findViewById(R.id.lblRespostas);
        txtResposta = (EditText) findViewById(R.id.txtResposta);
        lblContResposta = (TextView) findViewById(R.id.lblContResposta);

        btnEnviarResposta = (Button) findViewById(R.id.btnEnviarResposta);
        btnEnviarResposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discussaoPresenter.enviarResposta(txtResposta.getText().toString());
            }
        });

        View lblSugestao = findViewById(R.id.lblSugestao);
        ((ViewGroup) lblSugestao.getParent()).removeView(lblSugestao);

        txtResposta.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void afterTextChanged(Editable s) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                discussaoPresenter.setTextChangedTxtResposta(s.length());
            }
        });

        pgbLoading = (ProgressBar) findViewById(R.id.pgbLoading);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setFocusable(false);

        adapter = new DiscussaoRespostaAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoadRespostas(LinkedList<Resposta> listaRespostas) {
        adapter.setItems(listaRespostas);
    }

    @Override
    public void onSetTextLblID(String texto) {
        lblIDDiscussao.setText(texto);
    }

    @Override
    public void onSetTextLblTitulo(String texto) {
        lblTituloDiscussao.setText(texto);
    }

    @Override
    public void onSetTextLblDescricao(String texto) {
        lblDescricaoDiscussao.setText(texto);
    }

    @Override
    public void onSetTextLblAutor(String texto) {
        lblAutor.setText(texto);
    }

    @Override
    public void onSetTextLblDataHora(String texto) {
        lblDataHora.setText(texto);
    }

    @Override
    public void onSetTextLblQntRespostas(String texto) {
        lblContResposta.setText(texto);
    }

    @Override
    public void onSetTextTxtResposta(String texto) {
        txtResposta.setText(texto);
    }

    @Override
    public void onSetVisibilityFrmResponder(int visibility) {
        findViewById(R.id.include).setVisibility(visibility);
    }

    @Override
    public void onSetVisibilityProgressBar(int visibility) {
        if (visibility == View.VISIBLE) {
            pgbLoading.bringToFront();
        }

        pgbLoading.setVisibility(visibility);
    }

    @Override
    public void onLoadProfilePicture(String url) {
        Utils.loadImageInBackground(this, url, imgPerfil, prgBarra);
    }

    @Override
    public void onNotifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(int posicao) {

    }

    @Override
    public void onClickPerfil(int posicao) {

    }

    @Override
    public void desativarDiscussao(int posicao) {

    }

    @Override
    public void compartilharDiscussao(View view) {
        discussaoPresenter.compartilhar(view);
    }
}
