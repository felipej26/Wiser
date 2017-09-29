package br.com.wiser.features.mensagem;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import br.com.wiser.AbstractAppCompatActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.dialogs.DialogSugestoes;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.facebook.ICallbackPaginas;
import br.com.wiser.features.assunto.Assunto;
import br.com.wiser.features.assunto.Pagina;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.ICallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by Jefferson on 30/05/2016.
 */
public class MensagemActivity extends AbstractAppCompatActivity implements DialogSugestoes.CallbackSugestao {

    private MensagemPresenter mensagensPresenter;
    private MensagemAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.lblContResposta) TextView lblContResposta;
    @BindView(R.id.lblSugestao) TextView lblSugestao;
    @BindView(R.id.txtResposta) EditText txtResposta;
    @BindView(R.id.btnEnviarResposta) Button btnEnviarResposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mensagensPresenter = new MensagemPresenter(
                getIntent().getLongExtra(Sistema.CONVERSA, 0),
                (Usuario) getIntent().getSerializableExtra(Sistema.CONTATO));

        onLoad();

        if (mensagensPresenter.getConversa().getId() > 0 && mensagensPresenter.getConversa().getMensagens().size() > 0) {
            atualizarMensagensLidas();
        }

        carregarSugestoes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void onLoad() {
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mensagensPresenter.getContato().getNome());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MensagemAdapter(mensagensPresenter.getMensagens(), new MensagemAdapter.IMensagensListener() {
            @Override
            public void onMensagensChanged() {
                ajustarLista();
                atualizarMensagensLidas();
            }
        });

        lblSugestao.setText("");
        lblSugestao.setVisibility(View.INVISIBLE);

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
    }

    private void ajustarLista() {
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }

    private void atualizarMensagensLidas() {
        mensagensPresenter.atualizarMensagensLidas(new ICallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(String mensagemErro) {

            }
        });
    }

    private void carregarSugestoes() {
        Facebook facebook = new Facebook();

        if (mensagensPresenter.getConversa().getSugestoes().size() > 0) {
            adicionarSugestao();
        }
        else {
            facebook.carregarPaginasEmComum(mensagensPresenter.getContato().getFacebookID(), new ICallbackPaginas() {
                @Override
                public void setResponse(HashSet<Pagina> paginas) {

                    Map<String, Assunto> mapAssuntos = new HashMap<>();

                    for (Assunto assunto : Sistema.getAssuntos()) {
                        for (String categoria : assunto.getCategorias()) {
                            mapAssuntos.put(categoria, assunto);
                        }
                    }

                    for (Pagina pagina : paginas) {
                        if (mapAssuntos.containsKey(pagina.getCategoria())) {
                            Assunto assunto = mapAssuntos.get(pagina.getCategoria());

                            int item = new Random().nextInt(assunto.getItens().size());
                            mensagensPresenter.getConversa().getSugestoes().add(assunto.getItens().get(item)
                                    .replace("%a", pagina.getNome())
                                    .replace("%i", Sistema.getDescricaoIdioma(Sistema.getUsuario().getIdioma()))
                                    .replace("%u", mensagensPresenter.getContato().getPrimeiroNome()));
                        }
                    }

                    if (mensagensPresenter.getConversa().getSugestoes().size() > 0) {
                        adicionarSugestao();
                    }
                }
            });
        }
    }

    private void adicionarSugestao() {
        adapter.onSetSugestao(new MensagemAdapter.Callback() {
            @Override
            public void onSugestaoClick() {
                DialogSugestoes sugestoes = new DialogSugestoes();
                sugestoes.show(getContext(), MensagemActivity.this, mensagensPresenter.getConversa().getSugestoes());
            }
        });
    }
}
