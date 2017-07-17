package br.com.wiser.features.parametros;

/**
 * Created by Jefferson on 16/07/2017.
 */

public enum CodigoParametro {
    SALVOU_CONFIGURACOES(1);

    private int parametro;
    CodigoParametro(int parametro) {
        this.parametro = parametro;
    }

    public int getValor() {
        return parametro;
    }
}