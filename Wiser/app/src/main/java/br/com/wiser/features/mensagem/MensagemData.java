package br.com.wiser.features.mensagem;

/**
 * Created by Jefferson on 13/09/2017.
 */

public class MensagemData extends MensagemAbstract {

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public MensagemTipo getTipo() {
        return MensagemTipo.MENSAGEM_DATA;
    }
}
