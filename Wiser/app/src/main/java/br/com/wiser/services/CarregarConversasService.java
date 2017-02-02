package br.com.wiser.services;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.params.Face;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.APIClient;
import br.com.wiser.facebook.Facebook;
import br.com.wiser.interfaces.ICallback;
import br.com.wiser.models.conversas.Conversas;
import br.com.wiser.models.conversas.ConversasDeserializer;
import br.com.wiser.models.conversas.IConversasService;
import br.com.wiser.models.mensagens.Mensagem;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.utils.Utils;
import br.com.wiser.views.splashscreen.SplashScreenActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jefferson on 18/09/2016.
 */
public class CarregarConversasService extends Service {

    private IConversasService service;

    private LinkedList<Conversas> listaConversas;
    private boolean lock = false;
    private boolean lockCarregarUsuarios = false;

    @Override
    public void onCreate() {
        super.onCreate();

        service = APIClient.getClient().create(IConversasService.class);
        listaConversas = new LinkedList<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                long idUltimaMensagem;

                while (true) {
                    try {
                        idUltimaMensagem = 0;
                        lock = true;

                        for (Conversas conversa : listaConversas) {
                            if (conversa.getMensagens().size() > 0) {
                                if (conversa.getMensagens().getLast().getId() > idUltimaMensagem) {
                                    idUltimaMensagem = conversa.getMensagens().getLast().getId();
                                }
                            }
                        }

                        Call<LinkedList<Conversas>> call = service.carregarConversas(Sistema.getUsuario().getUserID(), idUltimaMensagem);
                        call.enqueue(new Callback<LinkedList<Conversas>>() {
                            @Override
                            public void onResponse(Call<LinkedList<Conversas>> call, Response<LinkedList<Conversas>> response) {
                                List<String> listaNovasMensagens;

                                if (response.isSuccessful()) {
                                    listaNovasMensagens = adicionarNovasMensagens(response.body());

                                    if (listaNovasMensagens.size() > 0) {
                                        Utils.vibrar(CarregarConversasService.this, 150);
                                        notificar(listaNovasMensagens);
                                    }

                                    EventBus.getDefault().post(listaConversas);
                                }

                                lock = false;
                            }

                            @Override
                            public void onFailure(Call<LinkedList<Conversas>> call, Throwable t) {
                                Log.e("Serviço", "Erro ao Carregar Conversas", t);
                                lock = false;
                            }
                        });

                        while (lock) ;
                    }
                    catch (Exception e) {
                        Log.e("Serviço", "Erro", e);
                    }

                    try {
                        Thread.sleep(3000);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private List<String> adicionarNovasMensagens(LinkedList<Conversas> listaConversas) {
        List<String> listaNovasMensagens = new LinkedList<>();
        List<Usuario> listaUsuarios = new LinkedList<>();
        Facebook facebook = new Facebook(this);

        Conversas conversaAux;

        /* Adiciona na Lista de Conversas as novas conversas
         * e na Lista de Usuarios, os novos usuarios dessas conversas */
        for (Conversas conversa : listaConversas) {
            conversaAux = null;

            if (conversa.getMensagens().size() > 0) {

                for (Conversas c : this.listaConversas) {
                    if (c.getId() == conversa.getId()) {
                        conversaAux = c;
                        break;
                    }
                }

                if (conversaAux == null) {
                    conversaAux = new Conversas();
                    conversaAux.setId(conversa.getId());
                    conversaAux.setDestinatario(conversa.getDestinatario());

                    listaUsuarios.add(conversa.getDestinatario());
                    this.listaConversas.add(conversaAux);
                }
            }
        }

        lockCarregarUsuarios = false;

        /* Carrega o perfil dos novos usuarios */
        if (listaUsuarios.size() > 0) {
            lockCarregarUsuarios = true;

            facebook.carregarUsuarios(listaUsuarios, new ICallback() {
                @Override
                public void onSuccess() {
                    lockCarregarUsuarios = false;
                }

                @Override
                public void onError(String mensagemErro) {
                    Log.e("Serviço", mensagemErro);
                    lockCarregarUsuarios = false;
                }
            });
        }

        // TODO Arrumar isso
        //while (lockCarregarUsuarios);

        /* Adiciona na Lista de Conversas e na Lista de Novas Mensagens as novas mensagens recebidas */
        for (Conversas conversa : listaConversas) {

            if (conversa.getMensagens().size() > 0) {

                for (Conversas c : this.listaConversas) {
                    if (c.getId() == conversa.getId()) {
                        c.getMensagens().addAll(conversa.getMensagens());

                        for (Mensagem mensagem : c.getMensagens()) {
                            if (mensagem.isDestinatario() && !mensagem.isLida()) {
                                listaNovasMensagens.add(c.getDestinatario().getPerfil().getFirstName() + ": " + Utils.decode(mensagem.getMensagem()));
                            }

                            mensagem.setMensagem(Utils.decode(mensagem.getMensagem()));
                        }
                    }
                }
            }
        }

        return listaNovasMensagens;
    }

    private void notificar(List<String> listaNovasMensagens) {

        int notificacaoID = 1;

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_wiser_notificacao)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Chegaram " + listaNovasMensagens.size() + " novas mensagens!");

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.app_name));

        for (String mensagem : listaNovasMensagens) {
            System.out.println(mensagem);
            inboxStyle.addLine(mensagem);
        }

        builder.setStyle(inboxStyle);

        Intent resultIntent = new Intent(this, SplashScreenActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SplashScreenActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificacaoID, builder.build());
    }
}
