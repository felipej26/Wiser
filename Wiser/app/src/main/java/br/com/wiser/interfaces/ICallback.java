package br.com.wiser.interfaces;

/**
 * Created by Jefferson on 23/01/2017.
 */
public interface ICallback {
    void onSuccess();
    void onError(String mensagemErro);
}
