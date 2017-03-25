package br.com.wiser.features.mensagem;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogSugestoes;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.interfaces.ICallbackSuccess;
import br.com.wiser.views.AbstractActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Jefferson on 30/05/2016.
 */
public class MensagemActivity extends AbstractActivity implements DialogSugestoes.CallbackSugestao {

    private Conversa conversa;

    private MensagemPresenter mensagensPresenter;
    private IMensagemAdapter adapter;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.lblContResposta) TextView lblContResposta;
    @BindView(R.id.lblSugestao) TextView lblSugestao;
    @BindView(R.id.txtResposta) EditText txtResposta;
    @BindView(R.id.btnEnviarResposta) Button btnEnviarResposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_mensagens);

        conversa = (Conversa) getIntent().getSerializableExtra(Sistema.CONVERSA);
        mensagensPresenter = new MensagemPresenter(new MensagemDAO(this));

        onLoad();
        /*
        if (!conversa.isCarregouSugestoes()) {
            mensagensPresenter.carregarSugestoesAssuntos();
        }
        */

        onLoadMessages();
        mensagensPresenter.atualizarMensagensLidas(conversa.getId(), new ICallbackSuccess() {
            @Override
            public void onSuccess() {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(Mensagem mensagem) {
        if (this.conversa.getId() == mensagem.getConversa()) {
            conversa.getMensagens().add(mensagem);
            adapter.addItem(mensagem);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void onLoad() {
        ButterKnife.bind(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(Sistema.getListaUsuarios().get(conversa.getDestinatario()).getPerfil().getFullName());

        lblSugestao.setText("");
        lblSugestao.setVisibility(View.INVISIBLE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MensagemAdapter(this);
        recyclerView.setAdapter((RecyclerView.Adapter) adapter);

        adapter.onSetSugestao(new MensagemAdapter.Callback() {
            @Override
            public void onSugestaoClick() {
                DialogSugestoes sugestoes = new DialogSugestoes(MensagemActivity.this);
                sugestoes.show(conversa.getSugestoes());
            }
        });
    }

    private void onLoadMessages() {
        conversa.setMensagens(
                new LinkedList<>(mensagensPresenter.carregarMensagens(conversa.getId())));

        adapter.addAll(conversa.getMensagens());
        recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount());
    }

    @OnClick(R.id.btnEnviarResposta)
    public void onSendMessage() {
        mensagensPresenter.enviarMensagem(conversa.getId(), conversa.getDestinatario(), txtResposta.getText().toString(), new ICallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String mensagemErro) {

            }
        });
        clearFiels();
    }

    @OnTextChanged(value = R.id.txtResposta, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onRespostaTextChanged(Editable editable) {
        lblContResposta.setText(editable.length() + " / 250");
    }

    private void clearFiels() {
        txtResposta.setText("");
        lblSugestao.setText("");
        lblSugestao.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setSugestao(String sugestao) {
        lblSugestao.setVisibility(View.VISIBLE);
        lblSugestao.setText(sugestao);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }
}
