package br.com.wiser.features.mensagem;

/**
 * Created by Jefferson on 13/09/2017.
 */

public class MensagemObject extends MensagemAbstract {
    private Mensagem mensagem;

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public MensagemTipo getTipo() {
        return mensagem.isDestinatario() ? MensagemTipo.MENSAGEM_CONTATO : MensagemTipo.MENSAGEM_USUARIO;
    }
}
