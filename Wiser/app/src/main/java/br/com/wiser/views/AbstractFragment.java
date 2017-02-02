package br.com.wiser.views;

import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by Jefferson on 23/01/2017.
 */
public abstract class AbstractFragment extends Fragment implements IView {

    @Override
    public void showToast(String mensagem) {
        Toast.makeText(getContext(), mensagem, Toast.LENGTH_SHORT).show();
    }
}