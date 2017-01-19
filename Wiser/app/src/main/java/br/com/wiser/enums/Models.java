package br.com.wiser.enums;

public enum Models {

    CONTATO(0),
    CONVERSA(1),
    CONVERSAMENSAGEM(2),
    CONVERSAUSUARIO(3),
    DISCUSSAO(4),
    DISCUSSAORESPOSTA(5),
    FACEBOOK(6),
    FLUENCIA(7),
    IDIOMA(8),
    LINGUAGEM(9),
    USUARIO(10),
    ASSUNTOS(11);

    public int getEnmModels;
    Models(int valor) {
        getEnmModels = valor;
    }
}
