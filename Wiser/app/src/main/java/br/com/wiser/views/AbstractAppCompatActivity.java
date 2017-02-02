package br.com.wiser.views;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Jefferson on 22/01/2017.
 */
public abstract class AbstractAppCompatActivity extends AppCompatActivity implements IView {
    public Context getContext() {
        return this;
    }

    public Activity getActivity() {
        return this;
    }

    public void showToast(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}
