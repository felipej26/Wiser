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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.business.app.servidor.Servidor;
import br.com.wiser.business.app.usuario.Usuario;
import br.com.wiser.business.chat.conversas.Conversas;
import br.com.wiser.business.chat.conversas.ConversasDAO;
import br.com.wiser.business.chat.mensagem.Mensagem;
import br.com.wiser.dialogs.DialogSugestoes;
import br.com.wiser.utils.Utils;

/**
 * Created by Jefferson on 30/05/2016.
 */
public class ChatMensagensActivity extends Activity {

    private ConversasDAO objConversa;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private EditText txtResposta;
    private Button btnEnviar;

    private boolean checouExiste = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_mensagens);

        objConversa = (ConversasDAO) getIntent().getSerializableExtra("conversa");

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(objConversa.getDestinatario().getPerfil().getFullName());
        getActionBar().setLogo(R.drawable.logo_wiser);

        carregarComponentes();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final LinkedList<ConversasDAO> conversas) {
        boolean novaMensagem;

        for (ConversasDAO conversa : conversas) {

            if (objConversa.getId() == 0) {
                if (conversa.getDestinatario().getUserID() == objConversa.getDestinatario().getUserID()) {
                    objConversa = conversa;
                }
                else {
                    continue;
                }
            }

            if (conversa.getId() == objConversa.getId()) {
                novaMensagem = conversa.getMensagens().size() != adapter.getItemCount();

                objConversa.setMensagens(conversa.getMensagens());
                ((ChatMensagensAdapter) adapter).setItems(objConversa.getMensagens());

                if (novaMensagem) {
                    if (objConversa.getContMsgNaoLidas() > 0) {
                        Utils.vibrar(this, 150);
                        objConversa.atualizarLidas(this);
                    }

                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }

                break;
            }
        }

        checouExiste = true;
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

        adapter = new ChatMensagensAdapter(this);
        recyclerView.setAdapter(adapter);

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

        if (objConversa.getContMsgNaoLidas() > 0) {
            Utils.vibrar(this, 150);
        }
    }

    public void enviar(View view) {

        String texto = txtResposta.getText().toString().trim();
        Mensagem mensagem;

        if (checouExiste && !texto.isEmpty()) {
            mensagem = new Mensagem();

            mensagem.setLida(true);
            mensagem.setDestinatario(false);
            mensagem.setData(new Date());
            mensagem.setMensagem(texto);

            if (objConversa.enviarMensagem(this, mensagem)) {
                txtResposta.setText("");
            }
        }
    }

    public void abrirSugestao(View view) {
        DialogSugestoes sugestoes = new DialogSugestoes(this);

        if (!objConversa.isCarregouSugestoes()) {

        }

        sugestoes.show(objConversa.getSugestoes());
    }
}
