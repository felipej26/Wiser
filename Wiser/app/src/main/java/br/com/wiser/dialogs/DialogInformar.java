package br.com.wiser.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;
import android.widget.Button;

import br.com.wiser.R;

/**
 * Created by Jefferson on 14/09/2016.
 */
public class DialogInformar {

    public interface DialogInterface {
        void onClick();
    }

    private TextView txtInformacao;
    private Button btnOk;

    private Activity activity;
    private AlertDialog alert;
    private AlertDialog.Builder builder;

    private DialogInterface mOnOkClick;
    private String mensagem;

    public DialogInformar(Activity activity) {
        this.activity = activity;
    }

    public void setOkClick(final DialogInterface dialogInterface) {
        mOnOkClick = dialogInterface;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void show() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_informar, null);

        builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        txtInformacao = (TextView) view.findViewById(R.id.txtInformacao);
        btnOk = (Button) view.findViewById(R.id.btnOk);

        txtInformacao.setText(mensagem);

        try {
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnOkClick != null) {
                        mOnOkClick.onClick();
                    }
                    alert.dismiss();
                }
            });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        alert = builder.create();
        alert.show();
    }
}
