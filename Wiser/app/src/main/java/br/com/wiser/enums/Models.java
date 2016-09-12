package br.com.wiser.enums;

public enum Models {

    CONVERSA(0),
    CONVERSAMENSAGEM(1),
    CONVERSAUSUARIO(2),
    DISCUSSAO(3),
    DISCUSSAORESPOSTA(4),
    FACEBOOK(5),
    FLUENCIA(6),
    IDIOMA(7),
    LINGUAGEM(8),
    USUARIO(9);

    public int getEnmModels;
    Models(int valor) {
        getEnmModels = valor;
    }
}
