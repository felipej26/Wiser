package br.com.wiser.activity.chat.mensagens;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import java.util.Date;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.business.chat.conversas.ConversasDAO;
import br.com.wiser.business.chat.mensagem.Mensagem;

/**
 * Created by Jefferson on 30/05/2016.
 */
public class ChatMensagensActivity extends Activity {

    private ConversasDAO objConversa;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private EditText txtResposta;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_mensagens);

        objConversa = (ConversasDAO) getIntent().getSerializableExtra("conversa");

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(objConversa.getDestinatario().getFullName());

        carregarComponentes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void carregarComponentes() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtResposta = (EditText) findViewById(R.id.txtResposta);
        btnEnviar = (Button) findViewById(R.id.btnEnviarResposta);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar(v);
            }
        });

        final TextView lblContResposta = (TextView) super.findViewById(R.id.lblContResposta);

        TextWatcher textWatcher = new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void afterTextChanged(Editable s) { }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lblContResposta.setText(String.valueOf(s.length()) + " / 250");
            }
        };
        txtResposta.addTextChangedListener(textWatcher);

        carregarDados();
    }

    private void carregarDados() {

        final Context context = this;
        final Handler hCarregar = new Handler();

        if (objConversa.getMensagens() == null || objConversa.getMensagens().isEmpty()) {
            return;
        }

        hCarregar.post(new Runnable() {
            @Override
            public void run() {
                adapter = new ChatMensagensAdapter(context, (List) objConversa.getMensagens());
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        });

        // TODO: objConversa.atualizarLidas();
    }

    public void enviar(View view) {

        String texto = txtResposta.getText().toString();
        Mensagem mensagem;

        if (!texto.isEmpty()) {
            mensagem = new Mensagem();

            mensagem.setLida(true);
            mensagem.setDestinatario(false);
            mensagem.setData(new Date());
            mensagem.setMensagem(texto);

            if (objConversa.enviarMensagem(this, mensagem)) {
                objConversa.getMensagens().add(mensagem);

                txtResposta.setText("");
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }
        }
    }
}
