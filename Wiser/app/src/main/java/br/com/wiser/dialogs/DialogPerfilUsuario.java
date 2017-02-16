package br.com.wiser.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Button;

import java.util.LinkedList;

import br.com.wiser.APIClient;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.models.contatos.IContatosService;
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.models.forum.Discussao;
import br.com.wiser.models.forum.IForumService;
import br.com.wiser.models.usuario.IUsuarioService;
import br.com.wiser.views.perfilcompleto.PerfilCompletoActivity;
import br.com.wiser.views.mensagens.MensagensActivity;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 29/05/2016.
 */
public class DialogPerfilUsuario {

    private IContatosService service;
    private IUsuarioService usuarioService;
    private IForumService forumService;

    private AlertDialog alert;
    private AlertDialog.Builder builder;
    private View viewDetalhes;
    private ImageView imgPerfil;
    private ProgressBar prgBarra;
    private TextView lblNome;
    private TextView lblIdiomaNivel;
    private TextView lblStatus;
    private Button btnAbrirChat;
    private Button btnPerfCompleto;

    public void show(final Context context, final Usuario contato) {

        service = APIClient.getClient().create(IContatosService.class);
        usuarioService = APIClient.getClient().create(IUsuarioService.class);
        forumService = APIClient.getClient().create(IForumService.class);

        builder = new AlertDialog.Builder(context);
        viewDetalhes = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_perfil, null);
        imgPerfil = (ImageView) viewDetalhes.findViewById(R.id.imgPerfil);
        prgBarra = (ProgressBar) viewDetalhes.findViewById(R.id.prgBarra);
        lblNome = (TextView) viewDetalhes.findViewById(R.id.lblNomeDetalhe);
        lblIdiomaNivel = (TextView) viewDetalhes.findViewById(R.id.lblIdiomaNivel);
        lblStatus = (TextView) viewDetalhes.findViewById(R.id.lblStatus);
        btnAbrirChat = (Button) viewDetalhes.findViewById(R.id.btnAbrirChat);
        btnPerfCompleto = (Button) viewDetalhes.findViewById(R.id.btnPerfCompleto);

        Utils.loadImageInBackground(context, contato.getPerfil().getUrlProfilePicture(), imgPerfil, prgBarra);
        lblNome.setText(contato.getPerfil().getFirstName());
        lblIdiomaNivel.setText(context.getString(R.string.fluencia_idioma,
                Sistema.getDescricaoFluencia(contato.getFluencia()), Sistema.getDescricaoIdioma(contato.getIdioma())));
        lblStatus.setText(Utils.decode(contato.getStatus()));

        if (Sistema.getUsuario().getUserID() == contato.getUserID()) {
            btnAbrirChat.setVisibility(View.INVISIBLE);
        }
        else if (!contato.isContato()) {
            btnAbrirChat.setText(R.string.adicionar_amigo);
            btnAbrirChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnAbrirChat.setVisibility(View.INVISIBLE);
                    Call<Object> call = service.adicionarContato(Sistema.getUsuario().getUserID(), contato.getUserID());
                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            btnAbrirChat.setVisibility(View.VISIBLE);
                            if (response.isSuccessful()) {
                                btnAbrirChat.setText(context.getString(R.string.enviar_mensagem));
                                loadAsFriend(contato);
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            btnAbrirChat.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        } else {
           loadAsFriend(contato);
        }

        btnPerfCompleto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PerfilCompletoActivity.class);
                intent.putExtra(Sistema.CONTATO, contato);
                v.getContext().startActivity(intent);
                alert.dismiss();
            }
        });

        builder.setView(viewDetalhes);
        alert = builder.create();
        alert.show();
    }

    public void loadAsFriend(final Usuario contato){
        btnAbrirChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), MensagensActivity.class);
                Conversas conversa = new Conversas();
                conversa.setDestinatario(contato.getUserID());
                i.putExtra(Sistema.CONVERSA, conversa);
                v.getContext().startActivity(i);
            }
        });
    }
}