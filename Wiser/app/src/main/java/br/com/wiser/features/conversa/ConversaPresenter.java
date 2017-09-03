package br.com.wiser.features.conversa;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.features.usuario.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class ConversaPresenter {

    public interface ICallbackConversa {
        void onSuccess(Conversa conversa);
        void onError();
    }

    private IConversaService conversaService;

    public ConversaPresenter() {
        conversaService = APIClient.getClient().create(IConversaService.class);
    }

    public void getConversa(final Usuario destinatario, final ICallbackConversa callback) {
        Call<Conversa> call = conversaService.carregarConversa(Sistema.getUsuario().getId(), destinatario.getId());
        call.enqueue(new Callback<Conversa>() {
            @Override
            public void onResponse(Call<Conversa> call, Response<Conversa> response) {
                if (response.isSuccessful()) {
                    response.body().setDestinatario(destinatario);
                    callback.onSuccess(response.body());
                }
                else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(Call<Conversa> call, Throwable t) {
                callback.onError();
            }
        });
    }
}
