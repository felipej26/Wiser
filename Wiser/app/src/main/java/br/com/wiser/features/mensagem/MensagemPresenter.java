package br.com.wiser.features.mensagem;

import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.wiser.APIClient;
import br.com.wiser.Sistema;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.interfaces.ICallbackSuccess;
import br.com.wiser.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 25/01/2017.
 */
public class MensagemPresenter {

    private IMensagemService service;
    private MensagemDAO mensagensDAO;

    public MensagemPresenter(MensagemDAO mensagensDAO) {
        service = APIClient.getClient().create(IMensagemService.class);
        this.mensagensDAO = mensagensDAO;
    }

    public List<Mensagem> carregarMensagens(long conversa) {
        return mensagensDAO.get(conversa);
    }

    public void atualizarMensagensLidas(long conversa, final ICallbackSuccess callback) {
        Map<String, Long> map = new HashMap<>();

        map.put("conversa", conversa);
        map.put("usuario", Sistema.getUsuario().getUserID());
        map.put("mensagem", mensagensDAO.getMaxIdServer(conversa));

        Call<Object> call = service.atualizarMensagensLidas(map);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("Atualizar Lidas", "Erro ao fazer a requisição", t);
            }
        });
    }

    public void enviarMensagem(long conversa, long destinatario, String textoMensagem, final ICallback callback) {
        Map<String, String> map = new HashMap<>();
        Mensagem mensagem = new Mensagem();

        final long idMensagem[] = {0};

        mensagem.setConversa(conversa);
        mensagem.setUsuario(Sistema.getUsuario().getUserID());
        mensagem.setEstado(Mensagem.Estado.ENVIANDO);
        mensagem.setData(new Date());
        mensagem.setMensagem(textoMensagem.trim());
        mensagem.setLida(true);

        idMensagem[0] = mensagensDAO.insert(mensagem);
        mensagem.setId(idMensagem[0]);

        if (!textoMensagem.trim().isEmpty()) {
            map.put("conversa", String.valueOf(conversa));
            map.put("usuario", String.valueOf(mensagem.getUsuario()));
            map.put("destinatario", String.valueOf(destinatario));
            map.put("data", mensagem.getData().toString());
            map.put("mensagem", Utils.encode(mensagem.getMensagem()));

            Call<Mensagem> call = service.enviarMensagem(map);
            call.enqueue(new Callback<Mensagem>() {
                @Override
                public void onResponse(Call<Mensagem> call, Response<Mensagem> response) {
                    if (response.isSuccessful()) {
                        Mensagem mensagem = response.body();
                        mensagem.setIdServer(mensagem.getId());
                        mensagem.setId(idMensagem[0]);

                        mensagensDAO.update(mensagem);
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(Call<Mensagem> call, Throwable t) {
                    //view.showToast("Falha ao enviar");
                    // TODO reenviar a mensagem
                    callback.onError(t.getMessage());
                }
            });
        }
    }

    /*
    public void carregarSugestoesAssuntos() {
        Facebook facebook = new Facebook(getContext());

        facebook.carregarPaginasEmComum(conversa.getDestinatario(), new ICallbackPaginas() {
            @Override
            public void setResponse(HashSet<Pagina> paginas) {
                Map<String, Assunto> mapAssuntos = new HashMap<>();

                for (Assunto assunto : Sistema.getAssuntos()) {
                    for (String categoria : assunto.getCategorias()) {
                        mapAssuntos.put(categoria, assunto);
                    }
                }

                for (Pagina pagina : paginas) {
                    if (mapAssuntos.containsKey(pagina.getCategoria())) {
                        Assunto assunto = mapAssuntos.get(pagina.getCategoria());

                        int item = new Random().nextInt(assunto.getItens().size());
                        conversa.getSugestoes().add(assunto.getItens().get(item)
                            .replace("%a", pagina.getNome())
                            .replace("%i", Sistema.getDescricaoIdioma(Sistema.getUsuario().getIdioma()))
                            .replace("%u", Sistema.getListaUsuarios().get(conversa.getDestinatario()).getPerfil().getFirstName()));
                    }
                }

                if (conversa.getSugestoes().size() > 0) {
                    view.onSetSugestao();
                }

                conversa.setCarregouSugestoes(true);
            }
        });
    }
    */
}
