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
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.views.perfilcompleto.PerfilCompletoActivity;
import br.com.wiser.views.mensagens.MensagensActivity;
import br.com.wiser.models.usuario.Usuario;
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
    private Button btnPerfCompleto;

    public void show(final Context context, final Usuario contato) {

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
        else {
            if (!contato.isContato()) {
                btnAbrirChat.setText(R.string.adicionar_amigo);
                btnAbrirChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*
                        if (Sistema.getUsuario().adicionarContato(context, contato)) {
                            btnAbrirChat.setText(context.getString(R.string.enviar_mensagem));
                            btnAbrirChat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(v.getContext(), MensagensActivity.class);
                                    Conversas conversa = new Conversas();
                                    conversa.setDestinatario(contato);
                                    i.putExtra(Sistema.CONVERSA, conversa);
                                    v.getContext().startActivity(i);
                                }
                            });
                        }
                        */
                    }
                });
            }
            else {
                btnAbrirChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(v.getContext(), MensagensActivity.class);
                        Conversas conversa = new Conversas();
                        conversa.setDestinatario(contato);
                        i.putExtra(Sistema.CONVERSA, conversa);
                        v.getContext().startActivity(i);
                    }
                });
            }
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
}
