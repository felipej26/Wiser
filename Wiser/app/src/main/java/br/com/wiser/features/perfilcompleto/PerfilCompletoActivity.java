package br.com.wiser.features.perfilcompleto;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.features.discussao.Discussao;
import br.com.wiser.features.discussao.DiscussaoPresenter;
import br.com.wiser.features.discussao.DiscussaoCardViewAdapter;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.utils.Utils;
import br.com.wiser.AbstractActivity;
import br.com.wiser.features.discussao.IDiscussaoView;

/**
 * Created by Lucas on 25/09/2016.
 */
public class PerfilCompletoActivity extends AbstractActivity implements IPerfilCompletoView, IDiscussaoView {

    private PerfilCompletoPresenter perfilCompletoPresenter;
    private DiscussaoPresenter discussaoPresenter;

    private ImageView imgPerfil;

    private TextView lblNomeDetalhe;
    private TextView lblIdade;
    private TextView lblIdiomaNivel;
    private TextView lblStatus;
    private Button btnAbrirChat;

    private RecyclerView recyclerView;
    private DiscussaoCardViewAdapter adapter;
    private ProgressBar prgBarra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_perfil_completo);

        perfilCompletoPresenter = new PerfilCompletoPresenter();
        perfilCompletoPresenter.onCreate(this, (Usuario) getIntent().getExtras().get(Sistema.CONTATO));

        discussaoPresenter = new DiscussaoPresenter();
        discussaoPresenter.onCreate(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onInitView() {
        getActionBar().setDisplayHomeAsUpEnabled(true);

        imgPerfil = (ImageView) findViewById(R.id.imgPerfil);
        prgBarra = (ProgressBar) findViewById(R.id.prgBarra);
        lblNomeDetalhe = (TextView) findViewById(R.id.lblNomeDetalhe);
        lblIdade = (TextView) findViewById(R.id.lblIdade);
        lblIdiomaNivel = (TextView) findViewById(R.id.lblIdiomaNivel);
        lblStatus = (TextView) findViewById(R.id.lblStatus);
        btnAbrirChat = (Button) findViewById(R.id.btnAbrirChat);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        adapter = new DiscussaoCardViewAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoadAsUser() {
        btnAbrirChat.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoadAsFriend(final Usuario usuario) {

        btnAbrirChat.setText(PerfilCompletoActivity.this.getString(R.string.enviar_mensagem));
        btnAbrirChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perfilCompletoPresenter.openChat();
            }
        });
    }

    @Override
    public void onLoadAsNotFriend(final Usuario usuario) {

        btnAbrirChat.setText(getString(R.string.adicionar_amigo));
        btnAbrirChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perfilCompletoPresenter.adicionarContato();
            }
        });
    }

    @Override
    public void onLoadListaDiscussoes(LinkedList<Discussao> listaDiscussoes) {
        adapter.setItems(listaDiscussoes);
    }

    @Override
    public void onLoadProfilePicture(String urlProfilePicture) {
        Utils.loadImageInBackground(this, urlProfilePicture, imgPerfil, prgBarra);
    }

    @Override
    public void onSetTextLblNome(String nome) {
        lblNomeDetalhe.setText(nome);
    }

    @Override
    public void onSetTextLblIdade(String idade) {
        lblIdade.setText(idade);
    }

    @Override
    public void onSetTextLblIdiomaNivel(String idioma) {
        lblIdiomaNivel.setText(idioma);
    }

    @Override
    public void onSetTextLblStatus(String status) {
        lblStatus.setText(status);
    }

    @Override
    public void onSetVisibilityProgressBar(int visibility) {
        if (visibility == View.VISIBLE) {
            prgBarra.bringToFront();
        }

        prgBarra.setVisibility(visibility);
    }

    @Override
    public void onClick(int posicao) {
        perfilCompletoPresenter.openDiscussao(posicao);
    }

    @Override
    public void onClickPerfil(int posicao) {
        //discussaoPresenter.openPerfil(Sistema.getListaUsuarios().get(adapter.getItem(posicao).getUsuario()));
    }

    @Override
    public void desativarDiscussao(int posicao) {
        discussaoPresenter.confirmarDesativarDiscussao(adapter.getItem(posicao), new ICallback() {
            @Override
            public void onSuccess() {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String mensagemErro) {
            }
        });
    }

    @Override
    public void compartilharDiscussao(View view) {
        discussaoPresenter.compartilhar(view);
    }


}
