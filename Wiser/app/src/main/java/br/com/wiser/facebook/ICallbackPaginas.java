package br.com.wiser.facebook;

import java.util.HashSet;

import br.com.wiser.features.assunto.Pagina;

/**
 * Created by Jefferson on 02/11/2016.
 */
public interface ICallbackPaginas {
    void setResponse(HashSet<Pagina> paginas);
}
