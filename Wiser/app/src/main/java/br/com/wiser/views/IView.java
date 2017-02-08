package br.com.wiser.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by Jefferson on 22/01/2017.
 */
public interface IView {
    Context getContext();
    Activity getActivity();
    void showToast(String mensagem);
    void showSnackBar(View view, String mensagem);
    void showSnackBarImage(View view, String mensagem, Bitmap imagem);
}
