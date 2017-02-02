package br.com.wiser.views;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by Jefferson on 22/01/2017.
 */
public abstract class AbstractActivity extends Activity implements IView {

    public Context getContext() {
        return this;
    }

    public Activity getActivity() {
        return this;
    }

    public void showToast(final String mensagem) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(AbstractActivity.this, mensagem, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
