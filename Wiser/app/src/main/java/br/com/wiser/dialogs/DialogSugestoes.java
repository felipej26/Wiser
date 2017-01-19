package br.com.wiser.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.HashSet;

import br.com.wiser.R;

/**
 * Created by Jefferson on 30/10/2016.
 */
public class DialogSugestoes {

    public interface CallbackSugestao {
        void setSugestao(String sugestao);
    }

    private Activity activity;
    private CallbackSugestao callback;

    private LinearLayout lytBotoes;

    private AlertDialog alert;
    private AlertDialog.Builder builder;

    public DialogSugestoes(Activity activity, CallbackSugestao callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void show(HashSet<String> assuntos) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sugestao, null);

        lytBotoes = (LinearLayout) view.findViewById(R.id.lytBotoes);

        if (assuntos.size() > 0) {
            carregaBotoes(assuntos);
        }

        builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        alert = builder.create();
        alert.show();
    }

    private void carregaBotoes(HashSet<String> assuntos) {

        int cont = 0;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 250);
        layoutParams.gravity = Gravity.CENTER;

        for (final String assunto : assuntos) {
            final Button button = new Button(activity);
            ViewGroup.MarginLayoutParams margin;

            button.setText(assunto);
            button.setTextSize(10);
            button.setLayoutParams(layoutParams);
            button.setBackground(activity.getDrawable(R.drawable.btndefaultshape));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.setSugestao(assunto);
                    alert.dismiss();
                }
            });

            margin = (ViewGroup.MarginLayoutParams) button.getLayoutParams();
            margin.leftMargin = 40;
            margin.rightMargin = 40;
            margin.bottomMargin = cont * 50;

            lytBotoes.addView(button);

            cont++;
        }
    }
}
