package br.com.wiser.features.perfilcompleto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import br.com.wiser.AbstractAppCompatActivity;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.conversa.ConversaPresenter;
import br.com.wiser.features.discussao.DiscussaoAdapter;
import br.com.wiser.features.discussao.DiscussaoPartial;
import br.com.wiser.features.discussao.IDiscussao;
import br.com.wiser.features.mensagem.MensagemActivity;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lucas on 25/09/2016.
 */
public class PerfilCompletoActivity extends AbstractAppCompatActivity implements IDiscussao {

    private PerfilCompletoPresenter perfilCompletoPresenter;
    private DiscussaoPartial discussaoPartial;
    private DiscussaoAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.imgPerfil) ImageView imgPerfil;
    @BindView(R.id.lblNomeDetalhe) TextView lblNomeDetalhe;
    @BindView(R.id.lblIdade) TextView lblIdade;
    @BindView(R.id.lblIdiomaNivel) TextView lblIdiomaNivel;
    @BindView(R.id.lblStatus) TextView lblStatus;
    @BindView(R.id.btnAbrirChat) Button btnAbrirChat;
    @BindView(R.id.prgBarra) ProgressBar prgBarra;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_perfil_completo);

        perfilCompletoPresenter = new PerfilCompletoPresenter((Usuario) getIntent().getExtras().get(Sistema.CONTATO));
        perfilCompletoPresenter.carregarDiscussoes(new ICallback() {
            @Override
            public void onSuccess() {
                adapter.addItems(perfilCompletoPresenter.getDiscussoes());
                onSetVisibilityProgressBar(View.INVISIBLE);
            }

            @Override
            public void onError(String mensagemErro) {
                onSetVisibilityProgressBar(View.INVISIBLE);
            }
        });
        discussaoPartial = new DiscussaoPartial();

        onLoad();
        onLoadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void onLoad() {
        ButterKnife.bind(this);

        onSetVisibilityProgressBar(View.VISIBLE);

        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        adapter = new DiscussaoAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        discussaoPartial.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void onLoadData() {
        Usuario usuario = perfilCompletoPresenter.getUsuario();

        if (Sistema.getUsuario().getId() == usuario.getId()) {
            btnAbrirChat.setVisibility(View.GONE);
        }
        else if (usuario.isContato()) {
            btnAbrirChat.setText(R.string.enviar_mensagem);
        }
        else {
            btnAbrirChat.setText(R.string.adicionar_amigo);
        }

        Utils.loadImageInBackground(usuario.getUrlFotoPerfil(), imgPerfil, prgBarra);
        lblNomeDetalhe.setText(usuario.getNome());
        lblIdade.setText(", " + usuario.getIdade());
        lblIdiomaNivel.setText(getString(R.string.fluencia_idioma,
                Sistema.getDescricaoFluencia(usuario.getFluencia()), Sistema.getDescricaoIdioma(usuario.getIdioma())));
        lblStatus.setText(Utils.decode(usuario.getStatus()));
    }

    @OnClick(R.id.btnAbrirChat)
    public void onOpenChatClicked() {
        if (perfilCompletoPresenter.getUsuario().isContato()) {
            startChat();
        }
        else {
            perfilCompletoPresenter.adicionarContato(new ICallback() {
                @Override
                public void onSuccess() {
                    btnAbrirChat.setText(R.string.enviar_mensagem);
                }

                @Override
                public void onError(String mensagemErro) {
                    showToast(mensagemErro);
                }
            });
        }
    }

    @Override
    public void onDiscussaoClicked(int posicao) {
        discussaoPartial.onDiscussaoClicked(this, perfilCompletoPresenter.getDiscussoes().get(posicao));
    }

    @Override
    public void onPerfilClicked(int posicao) {
        discussaoPartial.onPerfilClicked(this, perfilCompletoPresenter.getUsuario());
    }

    @Override
    public void onDesativarCliked(final int posicao) {
        discussaoPartial.onDesativarCliked(this, perfilCompletoPresenter.getDiscussoes().get(posicao), new ICallback() {
            @Override
            public void onSuccess() {
                adapter.updateItem(posicao);
            }

            @Override
            public void onError(String mensagemErro) {

            }
        });
    }

    @Override
    public void onCompartilharClicked(View view) {
        discussaoPartial.onCompartilharClicked(this, view);
    }

    private void startChat() {
        ConversaPresenter conversaPresenter = new ConversaPresenter();

        conversaPresenter.getIdConversa(perfilCompletoPresenter.getUsuario().getId(), new ConversaPresenter.ICallbackIdConversa() {
            @Override
            public void onSuccess(long idConversa) {
                Intent i = new Intent(getContext(), MensagemActivity.class);
                i.putExtra(Sistema.CONVERSA, idConversa);
                i.putExtra(Sistema.CONTATO, perfilCompletoPresenter.getUsuario());
                startActivity(i);
            }
        });
    }

    public void onSetVisibilityProgressBar(int visibility) {
        prgBarra.bringToFront();
        prgBarra.setVisibility(visibility);
    }
}
