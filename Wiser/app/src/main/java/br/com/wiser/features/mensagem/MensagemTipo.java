package br.com.wiser.features.mensagem;

/**
 * Created by Jefferson on 16/09/2017.
 */

public enum MensagemTipo {
    MENSAGEM_USUARIO(0), MENSAGEM_CONTATO(1), MENSAGEM_DATA(3);

    private int tipo;

    MensagemTipo(int tipo) {
        this.tipo = tipo;
    }

    public int getValor() {
        return tipo;
    }
}
