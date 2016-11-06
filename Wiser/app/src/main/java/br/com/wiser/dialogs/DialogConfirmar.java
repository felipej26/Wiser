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
public class DialogConfirmar {

    private TextView txtConfirmacao;
    private Button btnSim;
    private Button btnNao;

    private Activity activity;
    private AlertDialog alert;
    private AlertDialog.Builder builder;

    private IDialog mOnYesClick;
    private IDialog mOnNoClick;
    private String mensagem;

    public DialogConfirmar(Activity activity) {
        this.activity = activity;
    }

    public void setYesClick(final IDialog dialogInterface) {
        mOnYesClick = dialogInterface;
    }

    public void setNoClick(final IDialog dialogInterface) {
        mOnNoClick = dialogInterface;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void show() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirmar, null);

        builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        txtConfirmacao = (TextView) view.findViewById(R.id.txtConfirmacao);
        btnSim = (Button) view.findViewById(R.id.btnSim);
        btnNao = (Button) view.findViewById(R.id.btnNao);

        txtConfirmacao.setText(mensagem);

        try {
            btnSim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnYesClick != null) {
                        mOnYesClick.onClick();
                    }
                    alert.dismiss();
                }
            });

            btnNao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnNoClick != null) {
                        mOnNoClick.onClick();
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
