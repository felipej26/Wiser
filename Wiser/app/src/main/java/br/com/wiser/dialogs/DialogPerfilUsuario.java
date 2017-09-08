package br.com.wiser.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.wiser.APIClient;
import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.features.contato.IContatosService;
import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.features.conversa.ConversaPresenter;
import br.com.wiser.features.mensagem.MensagemActivity;
import br.com.wiser.features.perfilcompleto.PerfilCompletoActivity;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 29/05/2016.
 */
public class DialogPerfilUsuario {

    private IContatosService service;

    @BindView(R.id.imgPerfil) ImageView imgPerfil;
    @BindView(R.id.prgBarra) ProgressBar prgBarra;
    @BindView(R.id.lblNomeDetalhe) TextView lblNome;
    @BindView(R.id.lblIdiomaNivel) TextView lblIdiomaNivel;
    @BindView(R.id.lblStatus) TextView lblStatus;
    @BindView(R.id.btnAbrirChat) Button btnAbrirChat;
    @BindView(R.id.btnPerfCompleto) Button btnPerfCompleto;

    private Usuario contato;

    private AlertDialog alert;

    public DialogPerfilUsuario() {
        service = APIClient.getClient().create(IContatosService.class);
    }

    public void show(Context context, Usuario contato) {
        AlertDialog.Builder builder;

        this.contato = contato;

        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_perfil, null);

        onLoad(view, contato);

        builder = new AlertDialog.Builder(context);
        builder.setView(view);
        alert = builder.create();
        alert.show();
    }

    private void onLoad(View view, Usuario contato) {
        ButterKnife.bind(this, view);

        Utils.loadImageInBackground(view.getContext(), contato.getUrlFotoPerfil(), imgPerfil, prgBarra);
        lblNome.setText(contato.getNome());
        lblIdiomaNivel.setText(view.getContext().getString(R.string.fluencia_idioma,
                Sistema.getDescricaoFluencia(contato.getFluencia()), Sistema.getDescricaoIdioma(contato.getIdioma())));
        lblStatus.setText(Utils.decode(contato.getStatus()));

        if (Sistema.getUsuario().getId() == contato.getId()) {
            btnAbrirChat.setVisibility(View.GONE);
        }
        else if (!contato.isContato()) {
            btnAbrirChat.setText(R.string.adicionar_amigo);
        }
        else {
            loadAsFriend();
        }
    }

    private void loadAsFriend(){
        btnAbrirChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ConversaPresenter conversaPresenter = new ConversaPresenter();
                conversaPresenter.getConversa(contato, new ConversaPresenter.ICallbackConversa() {
                    @Override
                    public void onSuccess(Conversa conversa) {
                        Intent i = new Intent(v.getContext(), MensagemActivity.class);
                        i.putExtra(Sistema.CONVERSA, conversa);
                        v.getContext().startActivity(i);
                        alert.dismiss();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });
    }

    @OnClick(R.id.btnAbrirChat)
    public void onChatClicked() {
        btnAbrirChat.setVisibility(View.INVISIBLE);
        Call<Object> call = service.adicionarContato(Sistema.getUsuario().getId(), contato.getId());
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                btnAbrirChat.setVisibility(View.VISIBLE);
                if (response.isSuccessful()) {
                    btnAbrirChat.setText(R.string.enviar_mensagem);
                    loadAsFriend();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                btnAbrirChat.setVisibility(View.VISIBLE);
            }
        });
    }

    @OnClick(R.id.btnPerfCompleto)
    public void onPerfilClicked(View v) {
        Intent intent = new Intent(v.getContext(), PerfilCompletoActivity.class);
        intent.putExtra(Sistema.CONTATO, contato);
        v.getContext().startActivity(intent);
        alert.dismiss();
    }
}