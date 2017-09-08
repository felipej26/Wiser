package br.com.wiser.features.conversa;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.features.mensagem.Mensagem;
import br.com.wiser.utils.Utils;
import br.com.wiser.utils.UtilsDate;

/**
 * Created by Jefferson on 23/05/2016.
 */
public class ConversaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface ICallback {
        void onClick(int posicao);
    }

    private Context context;
    private LinkedList<Conversa> listaConversas = new LinkedList<>();
    private ICallback callback;

    public ConversaAdapter(Context context, ICallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_mensagens_item, parent, false);

        RecyclerView.ViewHolder viewHolder = new ViewHolder(itemLayoutView, callback);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).bind(context, listaConversas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return listaConversas.size();
    }

    public void addConversa(Conversa conversa) {
        this.listaConversas.add(conversa);
        notifyItemInserted(listaConversas.size());
    }

    public void addConversas(List<Conversa> listaConversas) {
        this.listaConversas.addAll(listaConversas);
        notifyDataSetChanged();
    }

    public void addMensagem(Conversa conversa, Mensagem mensagem) {
        for (Conversa c : this.listaConversas) {
            if (c.getId() == conversa.getId()) {
                c.getMensagens().add(mensagem);
                notifyItemInserted(this.listaConversas.indexOf(c));
                break;
            }
        }
    }

    public void addMensagens(Conversa conversa, List<Mensagem> listaMensagens) {
        for (Conversa c : this.listaConversas) {
            if (c.getId() == conversa.getId()) {
                c.getMensagens().addAll(listaMensagens);
                notifyItemChanged(this.listaConversas.indexOf(c));
                break;
            }
        }
    }

    public void updateItem(Conversa conversa) {
        notifyItemChanged(listaConversas.indexOf(conversa));
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View viewSeparator;

        public ImageView imgPerfil;
        public ProgressBar prgBarra;

        public TextView lblNome;
        public TextView lblDataHora;
        public TextView lblMensagens;
        public TextView lblContMensagens;

        private ICallback callback;
        private int posicao;

        public ViewHolder(View itemLayoutView, ICallback callback) {
            super(itemLayoutView);
            this.callback = callback;
            itemLayoutView.setOnClickListener(this);

            viewSeparator = itemLayoutView.findViewById(R.id.viewSeparator);

            imgPerfil = (ImageView) itemLayoutView.findViewById(R.id.imgPerfil);
            prgBarra = (ProgressBar) itemLayoutView.findViewById(R.id.prgBarra);

            lblNome = (TextView) itemLayoutView.findViewById(R.id.lblNome);
            lblDataHora = (TextView) itemLayoutView.findViewById(R.id.lblDataHora);
            lblMensagens = (TextView) itemLayoutView.findViewById(R.id.lblMensagens);
            lblContMensagens = (TextView) itemLayoutView.findViewById(R.id.lblContMensagens);
        }

        public void bind(Context context, Conversa conversa, int posicao) {
            this.posicao = posicao;
            viewSeparator.setVisibility(posicao == 0 ? View.INVISIBLE : View.VISIBLE);

            Utils.loadImageInBackground(context, conversa.getDestinatario().getUrlFotoPerfil(), imgPerfil, prgBarra);
            lblNome.setText(conversa.getDestinatario().getNome());
            if (conversa.getMensagens().size() > 0) {
                lblDataHora.setText(UtilsDate.formatDate(conversa.getMensagens().getLast().getData(), UtilsDate.HHMM));
                lblMensagens.setText(Utils.decode(conversa.getMensagens().getLast().getMensagem()));
            }
            lblContMensagens.setText(conversa.getContMsgNaoLidas() + " " + context.getString(conversa.getContMsgNaoLidas() <= 1 ? R.string.nao_lida : R.string.nao_lidas));
        }

        @Override
        public void onClick(View view) {
            callback.onClick(posicao);
        }
    }
}