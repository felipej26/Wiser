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

import br.com.wiser.AbstractActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogSugestoes;
import br.com.wiser.features.usuario.Usuario;
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

    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.lblContResposta) TextView lblContResposta;
    @BindView(R.id.lblSugestao) TextView lblSugestao;
    @BindView(R.id.txtResposta) EditText txtResposta;
    @BindView(R.id.btnEnviarResposta) Button btnEnviarResposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_mensagens);

        mensagensPresenter = new MensagemPresenter(
                getIntent().getLongExtra(Sistema.CONVERSA, 0),
                (Usuario) getIntent().getSerializableExtra(Sistema.CONTATO));

        onLoad();

        if (mensagensPresenter.getConversa().getId() > 0 && mensagensPresenter.getConversa().getMensagens().size() > 0) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void onLoad() {
        MensagemAdapter adapter;

        ButterKnife.bind(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(mensagensPresenter.getUsuario().getNome());

        lblSugestao.setText("");
        lblSugestao.setVisibility(View.INVISIBLE);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MensagemAdapter(mensagensPresenter.getMensagens());
        adapter.onSetSugestao(new MensagemAdapter.Callback() {
            @Override
            public void onSugestaoClick() {
                DialogSugestoes sugestoes = new DialogSugestoes();
                sugestoes.show(getContext(), MensagemActivity.this, mensagensPresenter.getConversa().getSugestoes());
            }
        });
        recyclerView.setAdapter(adapter);
        ajustarLista();
    }

    @OnClick(R.id.btnEnviarResposta)
    public void onEnviarRespostaClicked() {
        mensagensPresenter.enviarMensagem(txtResposta.getText().toString(), new ICallback() {
            @Override
            public void onSuccess() {
                limparCampos();
                ajustarLista();
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
        //recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void ajustarLista() {
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }
}
