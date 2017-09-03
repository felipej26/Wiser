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

import java.util.List;

import br.com.wiser.AbstractActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogSugestoes;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.interfaces.ICallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Jefferson on 30/05/2016.
 */
public class MensagemActivity extends AbstractActivity implements DialogSugestoes.CallbackSugestao {

    private MensagemPresenter mensagensPresenter;
    private MensagemAdapter adapter;

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.lblContResposta) TextView lblContResposta;
    @BindView(R.id.lblSugestao) TextView lblSugestao;
    @BindView(R.id.txtResposta) EditText txtResposta;
    @BindView(R.id.btnEnviarResposta) Button btnEnviarResposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_mensagens);

        mensagensPresenter = new MensagemPresenter((Conversa) getIntent().getSerializableExtra(Sistema.CONVERSA));

        onLoad();
        onLoadMessages();

        if (mensagensPresenter.getConversa().getId() > 0) {
            mensagensPresenter.atualizarMensagensLidas(new ICallback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(String mensagemErro) {

                }
            });
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(List<Conversa> listaConversas) {
        for (Conversa conversa : listaConversas) {
            if (conversa.getId() == mensagensPresenter.getConversa().getId()) {
                mensagensPresenter.getConversa().getMensagens().addAll(conversa.getMensagens());
                adapter.addAll(conversa.getMensagens());
                break;
            }
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
        getActionBar().setTitle(mensagensPresenter.getConversa().getDestinatario().getNome());

        lblSugestao.setText("");
        lblSugestao.setVisibility(View.INVISIBLE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MensagemAdapter(this);
        adapter.onSetSugestao(new MensagemAdapter.Callback() {
            @Override
            public void onSugestaoClick() {
                DialogSugestoes sugestoes = new DialogSugestoes();
                sugestoes.show(getContext(), MensagemActivity.this, mensagensPresenter.getConversa().getSugestoes());
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void onLoadMessages() {
        adapter.addAll(mensagensPresenter.getConversa().getMensagens());
        recyclerView.getLayoutManager().scrollToPosition(adapter.getItemCount());
    }

    @OnClick(R.id.btnEnviarResposta)
    public void onEnviarRespostaClicked() {
        mensagensPresenter.enviarMensagem(txtResposta.getText().toString(), new ICallback() {
            @Override
            public void onSuccess() {
                limparCampos();
            }

            @Override
            public void onError(String mensagemErro) {
                showToast("Erro ao enviar a mensagem!");
            }
        });
    }

    @OnTextChanged(value = R.id.txtResposta, callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void onRespostaTextChanged(Editable editable) {
        lblContResposta.setText(editable.length() + " / 250");
    }

    private void limparCampos() {
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
