package br.com.wiser.business.app.servidor;

import android.content.Context;

import br.com.wiser.business.app.usuario.Usuario;

/**
 * Created by Jefferson on 07/09/2016.
 */
public abstract class AbstractServidor {
    protected Context context;

    public AbstractServidor(Context context) {
        this.context = context;
    }
}
