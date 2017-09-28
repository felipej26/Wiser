package br.com.wiser.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.wiser.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jefferson on 14/09/2016.
 */
public class DialogConfirmar {

    @BindView(R.id.txtConfirmacao) TextView txtConfirmacao;
    @BindView(R.id.btnSim) Button btnSim;
    @BindView(R.id.btnNao) Button btnNao;

    private IDialog onYesClick;
    private IDialog onNoClick;
    private String mensagem;

    private AlertDialog alert;

    public void setYesClick(final IDialog dialogInterface) {
        onYesClick = dialogInterface;
    }

    public void setNoClick(final IDialog dialogInterface) {
        onNoClick = dialogInterface;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void show(Context context) {
        AlertDialog.Builder builder;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirm, null);

        onLoad(view);

        builder = new AlertDialog.Builder(context);
        builder.setView(view);
        alert = builder.create();
        alert.show();
    }

    private void onLoad(View view) {
        ButterKnife.bind(this, view);

        txtConfirmacao.setText(mensagem);
    }

    @OnClick(R.id.btnSim)
    public void onSimClicked() {
        if (onYesClick != null) {
            onYesClick.onClick();
        }
        alert.dismiss();
    }

    @OnClick(R.id.btnNao)
    public void onNaoClicked() {
        if (onNoClick != null) {
            onNoClick.onClick();
        }
        alert.dismiss();
    }
}
