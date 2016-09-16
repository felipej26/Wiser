package br.com.wiser.business.app.usuario;

import android.content.Context;

import br.com.wiser.Sistema;
import br.com.wiser.business.app.servidor.Servidor;

/**
 * Created by Jefferson on 07/08/2016.
 */
public class UsuarioDAO extends Usuario{

    public UsuarioDAO (long userID){
        super(userID);
    }

    public boolean salvarLogin(Context context) {
        try {
            new Servidor().new Usuarios(context).salvarLogin(this);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean salvarConfiguracoes(Context context) {
        return new Servidor().new Usuarios(context).salvarConfiguracoes(this);
    }

    public boolean desativarConta(Context context){
        return new Servidor().new Usuarios(context).desativarConta();
    }
}
