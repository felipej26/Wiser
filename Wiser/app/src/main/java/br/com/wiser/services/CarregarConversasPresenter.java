package br.com.wiser.services;

import br.com.wiser.features.conversa.Conversa;
import br.com.wiser.features.mensagem.Mensagem;
import io.realm.Realm;

/**
 * Created by Jefferson on 10/09/2017.
 */

public class CarregarConversasPresenter {

    public interface ICallbackIdUltimaMensagem {
        void onSuccess(long idUltimaMensagem);
    }

    private Realm realm;

    public CarregarConversasPresenter() {
        realm = Realm.getDefaultInstance();
    }

    public void adicionarConversa(Conversa conversa) {
        Conversa checkConversa = realm.where(Conversa.class).equalTo("id", conversa.getId()).findFirst();

        if (checkConversa == null) {
            realm.beginTransaction();
            realm.insert(conversa);
            realm.commitTransaction();
        } else {
            if (conversa.getMensagens().size() > 0) {
                realm.beginTransaction();
                checkConversa.getMensagens().addAll(conversa.getMensagens());
                realm.commitTransaction();
            }
        }
    }

    public void getIdUltimaMensagem(final ICallbackIdUltimaMensagem callback) {
        Realm realm = Realm.getDefaultInstance();
        Number number = realm.where(Mensagem.class).max("id");
        callback.onSuccess(number != null ? number.longValue() : 0);
        realm.close();
    }
}
