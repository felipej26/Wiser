package br.com.wiser.activity.app.perfil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.activity.chat.mensagens.ChatMensagensActivity;
import br.com.wiser.activity.forum.DiscussaoCardViewAdapter;
import br.com.wiser.activity.forum.IDiscussaoCardViewAdapterCallback;
import br.com.wiser.activity.forum.minhas_discussoes.ForumMinhasDiscussoesActivity;
import br.com.wiser.business.app.usuario.Usuario;
import br.com.wiser.business.app.usuario.UsuarioDAO;
import br.com.wiser.business.chat.conversas.ConversasDAO;
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.utils.Utils;

/**
 * Created by Lucas on 25/09/2016.
 */

public class AppPerfilCompleto extends Activity implements IDiscussaoCardViewAdapterCallback{

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

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;


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

        Utils.loadImageInBackground(this, usuario.getUrlProfilePicture(), imgPerfil, prgBarra);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        adapter = new DiscussaoCardViewAdapter(this, new LinkedList<DiscussaoDAO>());
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
                        if (Sistema.getUsuario(AppPerfilCompleto.this).adicionarContato(AppPerfilCompleto.this, usuario)) {
                            btnAbrirChat.setText(AppPerfilCompleto.this.getString(R.string.enviar_mensagem));
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
        lblNomeDetalhe.setText(usuario.getFullName());
        lblIdade.setText(", " + usuario.getIdade());
        lblIdiomaNivel.setText(getString(R.string.fluencia_idioma,
                Utils.getDescricaoFluencia(usuario.getFluencia()), Utils.getDescricaoIdioma(usuario.getIdioma())));
        lblStatus.setText(usuario.getStatus());
    }

    @Override
    public void desativarDiscussao(Discussao discussao) {

    }
}
