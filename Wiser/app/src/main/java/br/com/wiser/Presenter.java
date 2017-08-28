package br.com.wiser;

import android.app.Activity;
import android.content.Context;

import br.com.wiser.IView;

/**
 * Created by Jefferson on 22/01/2017.
 */
public abstract class Presenter<V extends IView> {
    protected V view;

    public void onCreate(V view) {
        this.view = view;
        onCreate();
    }

    protected void onCreate() { }

    protected Context getContext() {
        return view.getContext();
    }

    protected Activity getActivity() {
        return view.getActivity();
    }
}
