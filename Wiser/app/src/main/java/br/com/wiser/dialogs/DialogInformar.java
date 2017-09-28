package br.com.wiser.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
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
public class DialogInformar {

    @BindView(R.id.txtInformacao) TextView txtInformacao;
    @BindView(R.id.btnOk) Button btnOk;

    private Activity activity;
    private AlertDialog alert;

    private IDialog onOkClick;
    private String mensagem;

    public DialogInformar(Activity activity) {
        this.activity = activity;
    }

    public void setOkClick(final IDialog dialogInterface) {
        onOkClick = dialogInterface;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void show() {
        AlertDialog.Builder builder;

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_inform, null);

        onLoad(view);

        builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        alert = builder.create();
        alert.show();
    }

    private void onLoad(View view) {
        ButterKnife.bind(this, view);
        txtInformacao.setText(mensagem);
    }

    @OnClick(R.id.btnOk)
    public void onOkClicked() {
        if (onOkClick != null) {
            onOkClick.onClick();
        }
        alert.dismiss();
    }
}
