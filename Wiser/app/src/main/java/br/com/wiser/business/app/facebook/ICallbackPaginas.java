package br.com.wiser.business.app.facebook;

import java.util.HashSet;

import br.com.wiser.business.chat.paginas.Pagina;

/**
 * Created by Jefferson on 02/11/2016.
 */
public interface ICallbackPaginas {
    void setResponse(HashSet<Pagina> paginas);
}
