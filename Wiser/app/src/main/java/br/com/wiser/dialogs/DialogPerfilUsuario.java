package br.com.wiser.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Button;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.activity.chat.mensagens.ChatMensagensActivity;
import br.com.wiser.business.app.usuario.Usuario;
import br.com.wiser.business.chat.conversas.Conversas;
import br.com.wiser.business.chat.conversas.ConversasDAO;
import br.com.wiser.utils.Utils;

/**
 * Created by Jefferson on 29/05/2016.
 */
public class DialogPerfilUsuario {

    private AlertDialog alert;
    private AlertDialog.Builder builder;
    private View viewDetalhes;
    private ImageView imgPerfil;
    private ProgressBar prgBarra;
    private TextView lblNome;
    private TextView lblIdiomaNivel;
    private TextView lblStatus;
    private Button btnAbrirChat;

    public void mostrarDetalhes(final Context context, final Usuario contato) {

        builder = new AlertDialog.Builder(context);
        viewDetalhes = ((Activity) context).getLayoutInflater().inflate(R.layout.chat_perfil_detalhes, null);
        imgPerfil = (ImageView) viewDetalhes.findViewById(R.id.imgPerfil);
        prgBarra = (ProgressBar) viewDetalhes.findViewById(R.id.prgBarra);
        lblNome = (TextView) viewDetalhes.findViewById(R.id.lblNomeDetalhe);
        lblIdiomaNivel = (TextView) viewDetalhes.findViewById(R.id.lblIdiomaNivel);
        lblStatus = (TextView) viewDetalhes.findViewById(R.id.lblStatus);
        btnAbrirChat = (Button) viewDetalhes.findViewById(R.id.btnAbrirChat);

        Utils.loadImageInBackground(context, contato.getUrlProfilePicture(), imgPerfil, prgBarra);
        lblNome.setText(contato.getFirstName());
        lblIdiomaNivel.setText(context.getString(R.string.fluencia_idioma,
                Utils.getDescricaoFluencia(contato.getFluencia()), Utils.getDescricaoIdioma(contato.getIdioma())));
        lblStatus.setText(contato.getStatus());

        if (Sistema.getUsuario(context).getUserID() == contato.getUserID()) {
            btnAbrirChat.setVisibility(View.INVISIBLE);
        }
        else {
            btnAbrirChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), ChatMensagensActivity.class);
                    ConversasDAO conversa = new ConversasDAO();
                    conversa.setDestinatario(contato);
                    i.putExtra("conversa", conversa);
                    v.getContext().startActivity(i);
                }
            });
        }

        builder.setView(viewDetalhes);
        alert = builder.create();
        alert.show();
    }
}
