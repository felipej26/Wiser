package br.com.wiser.activity.app.perfil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.activity.chat.mensagens.ChatMensagensActivity;
import br.com.wiser.activity.forum.DiscussaoCardViewAdapter;
import br.com.wiser.activity.forum.IDiscussao;
import br.com.wiser.activity.forum.discussao.ForumDiscussaoActivity;
import br.com.wiser.business.app.usuario.Usuario;
import br.com.wiser.business.chat.conversas.ConversasDAO;
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.dialogs.DialogConfirmar;
import br.com.wiser.dialogs.DialogInformar;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.dialogs.IDialog;
import br.com.wiser.utils.Utils;

/**
 * Created by Lucas on 25/09/2016.
 */

public class AppPerfilCompletoActivity extends Activity implements IDiscussao {

    private ImageView imgPerfil;
    private ProgressBar prgBarra;
    private TextView lblNomeDetalhe;
    private TextView lblIdade;
    private TextView lblIdiomaNivel;
    private TextView lblStatus;
    private Button btnAbrirChat;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private Usuario usuario;
    private List<DiscussaoDAO> listaDiscussoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        usuario = (Usuario) getIntent().getExtras().get("usuario");
        setContentView(R.layout.app_perfil_completo);
        carregarComponentes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private void carregarComponentes(){
        imgPerfil = (ImageView) findViewById(R.id.imgPerfil);
        prgBarra = (ProgressBar) findViewById(R.id.prgBarra);
        lblNomeDetalhe = (TextView) findViewById(R.id.lblNomeDetalhe);
        lblIdade = (TextView) findViewById(R.id.lblIdade);
        lblIdiomaNivel = (TextView) findViewById(R.id.lblIdiomaNivel);
        lblStatus = (TextView) findViewById(R.id.lblStatus);
        btnAbrirChat = (Button) findViewById(R.id.btnAbrirChat);

        Utils.loadImageInBackground(this, usuario.getPerfil().getUrlProfilePicture(), imgPerfil, prgBarra);

        listaDiscussoes = new LinkedList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        adapter = new DiscussaoCardViewAdapter(this, listaDiscussoes);
        recyclerView.setAdapter(adapter);

        if (Sistema.getUsuario(this).getUserID() == usuario.getUserID()) {
            btnAbrirChat.setVisibility(View.INVISIBLE);
        }
        else {
            if (!usuario.isContato()) {
                btnAbrirChat.setText(getString(R.string.adicionar_amigo));
                btnAbrirChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Sistema.getUsuario(AppPerfilCompletoActivity.this).adicionarContato(AppPerfilCompletoActivity.this, usuario)) {
                            btnAbrirChat.setText(AppPerfilCompletoActivity.this.getString(R.string.enviar_mensagem));
                            btnAbrirChat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(v.getContext(), ChatMensagensActivity.class);
                                    ConversasDAO conversa = new ConversasDAO();
                                    conversa.setDestinatario(usuario);
                                    i.putExtra("conversa", conversa);
                                    v.getContext().startActivity(i);
                                }
                            });
                        }
                    }
                });
            }
            else {
                btnAbrirChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), ChatMensagensActivity.class);
                        ConversasDAO conversa = new ConversasDAO();
                        conversa.setDestinatario(usuario);
                        i.putExtra("conversa", conversa);
                        v.getContext().startActivity(i);
                    }
                });
            }
        }

        carregarDados();
    }

    private void carregarDados(){
        lblNomeDetalhe.setText(usuario.getPerfil().getFullName());
        lblIdade.setText(", " + usuario.getPerfil().getIdade());
        lblIdiomaNivel.setText(getString(R.string.fluencia_idioma,
                Utils.getDescricaoFluencia(usuario.getFluencia()), Utils.getDescricaoIdioma(usuario.getIdioma())));
        lblStatus.setText(usuario.getStatus());
    }

    public void desativar(Discussao discussao) {
        DialogInformar informar = new DialogInformar(this);

        DiscussaoDAO objDiscussao = new DiscussaoDAO();

        objDiscussao.setId(discussao.getId());
        objDiscussao.setAtiva(!discussao.isAtiva());

        if (objDiscussao.desativarDiscussao(this)) {
            discussao.setAtiva(objDiscussao.isAtiva());

            informar.setMensagem(getString(R.string.sucesso_discussao_excluida));
        }
        else {
            informar.setMensagem(getString(R.string.erro_excluir_discussao));
        }

        informar.show();
    }

    @Override
    public void onClick(int posicao) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("discussao", listaDiscussoes.get(posicao));

        Intent i = new Intent(this, ForumDiscussaoActivity.class);
        i.putExtra("discussoes", bundle);
        startActivity(i);
    }

    @Override
    public void onClickPerfil(int posicao) {
        DialogPerfilUsuario dialog = new DialogPerfilUsuario();
        dialog.show(this, listaDiscussoes.get(posicao).getUsuario());
    }

    @Override
    public void desativarDiscussao(final int posicao) {
        DialogConfirmar confirmar = new DialogConfirmar(this);

        confirmar.setYesClick(new IDialog() {
            @Override
            public void onClick() {
                desativar(listaDiscussoes.get(posicao));
            }
        });

        if (listaDiscussoes.get(posicao).isAtiva()) {
            confirmar.setMensagem(getString(R.string.confirmar_desativar_discussao));
        }
        else {
            confirmar.setMensagem(getString(R.string.confirmar_reativar_discussao));
        }

        confirmar.show();
    }

    @Override
    public void compartilharDiscussao(View view) {
        Utils.compartilharComoImagem(view);
    }
}
