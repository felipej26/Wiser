package br.com.wiser.views.mensagens;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.models.mensagens.Mensagem;
import br.com.wiser.presenters.mensagens.MensagensPresenter;
import br.com.wiser.views.AbstractActivity;

/**
 * Created by Jefferson on 30/05/2016.
 */
public class MensagensActivity extends AbstractActivity implements IMensagensView {

    private MensagensPresenter mensagensPresenter;

    private RecyclerView recyclerView;
    private MensagensAdapter adapter;

    private TextView lblContResposta;
    private TextView lblSugestao;
    private EditText txtResposta;
    private Button btnEnviar;
    private Button btnSugestao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_mensagens);

        mensagensPresenter = new MensagensPresenter();
        mensagensPresenter.onCreate(this, (Conversas) getIntent().getSerializableExtra(Sistema.CONVERSA));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mensagensPresenter.onStart();
    }

    @Override
    protected void onStop() {
        mensagensPresenter.onStop();
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LinkedList<Conversas> conversas) {
        mensagensPresenter.onEvent(conversas);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void setSugestao(String sugestao) {
        lblSugestao.setVisibility(View.VISIBLE);
        lblSugestao.setText(sugestao);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onInitView() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        lblSugestao = (TextView) findViewById(R.id.lblSugestao);
        lblSugestao.setText("");
        lblSugestao.setVisibility(View.INVISIBLE);

        lblContResposta = (TextView) super.findViewById(R.id.lblContResposta);
        txtResposta = (EditText) findViewById(R.id.txtResposta);
        btnEnviar = (Button) findViewById(R.id.btnEnviarResposta);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensagensPresenter.enviarMensagem(txtResposta.getText().toString());
            }
        });

        TextWatcher textWatcher = new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void afterTextChanged(Editable s) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mensagensPresenter.setTextChangedTxtResposta(s.length());
            }
        };
        txtResposta.addTextChangedListener(textWatcher);

        btnSugestao = (Button) findViewById(R.id.btnSugestao);
        btnSugestao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mensagensPresenter.mostrarSugestoes();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MensagensAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoadListaMensagens(LinkedList<Mensagem> listaMensagens) {
        adapter.setItems(listaMensagens);
    }

    @Override
    public void onSetTitleActionBar(String titulo) {
        getActionBar().setTitle(titulo);
    }

    @Override
    public void onSetPositionRecyclerView(int posicao) {
        recyclerView.scrollToPosition(posicao);
    }

    @Override
    public void onSetTextLblContLetras(String contLetras) {
        lblContResposta.setText(contLetras);
    }

    @Override
    public void onSetVisibilityBtnSugestoes(final int visibility) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnSugestao.setVisibility(visibility);
            }
        });
    }

    @Override
    public int onGetQntMensagens() {
        return adapter.getItemCount();
    }

    @Override
    public void onClearCampos() {
        txtResposta.setText("");
        lblSugestao.setText("");
        lblSugestao.setVisibility(View.INVISIBLE);
    }
}
