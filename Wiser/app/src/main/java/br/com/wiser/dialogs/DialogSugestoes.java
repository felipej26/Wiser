package br.com.wiser.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashSet;

import br.com.wiser.R;

/**
 * Created by Jefferson on 30/10/2016.
 */
public class DialogSugestoes {

    private Activity activity;
    private AlertDialog alert;
    private AlertDialog.Builder builder;

    public DialogSugestoes(Activity activity) {
        this.activity = activity;
    }

    public void show(HashSet<String> assuntos) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sugestao, null);

        builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        alert = builder.create();
        alert.show();
    }
}
