package br.com.wiser.features.mensagem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.utils.Utils;
import br.com.wiser.utils.UtilsDate;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jefferson on 30/05/2016.
 */
public class MensagemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Callback {
        void onSugestaoClick();
    }

    private Context context;
    private List<Mensagem> listaMensagens = new LinkedList<>();

    private final int VIEW_MENSAGEM_USUARIO = 0;
    private final int VIEW_MENSAGEM_CONTATO = 1;
    private final int VIEW_BOTAO_SUGESTAO = 2;

    private boolean hasSugestao;
    private Callback callback;

    public MensagemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position != listaMensagens.size()) {
            return (listaMensagens.get(position).isDestinatario() ? VIEW_MENSAGEM_CONTATO : VIEW_MENSAGEM_USUARIO);
        }
        return VIEW_BOTAO_SUGESTAO;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_MENSAGEM_USUARIO:
                return new MensagensViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.chat_mensagens_baloes_usuario, parent, false));
            case VIEW_MENSAGEM_CONTATO:
                return new MensagensViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.chat_mensagens_baloes_contato, parent, false));
            case VIEW_BOTAO_SUGESTAO:
                return new SugestaoViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.chat_mensagens_sugestao, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_MENSAGEM_USUARIO:
            case VIEW_MENSAGEM_CONTATO:
                ((MensagensViewHolder)holder).bind(context, listaMensagens.get(position));
                break;
            case VIEW_BOTAO_SUGESTAO:
                ((SugestaoViewHolder)holder).bind(callback);
                break;
        }
    }

    @Override
    public int getItemCount() {
        /* Soma um item para poder incluir o botão, se tiver uma sugestão */
        return listaMensagens.size() + (hasSugestao ? 1 : 0);
    }

    public void addItem(Mensagem mensagem) {
        listaMensagens.add(mensagem);
        notifyItemInserted(listaMensagens.size());
    }

    public void addAll(List<Mensagem> listaMensagens) {
        this.listaMensagens.addAll(listaMensagens);
        notifyDataSetChanged();
    }

    public void onSetSugestao(Callback callback) {
        this.callback = callback;
        hasSugestao = true;
        notifyDataSetChanged();
    }

    static class MensagensViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lblDataHora) TextView lblDataHora;
        @BindView(R.id.lblMensagem) TextView lblMensagem;
        @BindView(R.id.viewState) View viewState;

        public MensagensViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(Context context, Mensagem mensagem) {
            lblDataHora.setText(UtilsDate.formatDate(mensagem.getData(), UtilsDate.HHMM));
            lblMensagem.setText(Utils.decode(mensagem.getMensagem().trim()));

            switch (mensagem.getEstado()) {
                case ENVIADO:
                    viewState.setBackground(context.getDrawable(R.drawable.ic_message_state_send));
                    break;
                case ENVIANDO:
                    viewState.setBackground(context.getDrawable(R.drawable.ic_message_state_sending));
                    break;
                case ERRO:
                    viewState.setBackground(context.getDrawable(R.drawable.ic_message_state_error));
                    break;
            }
        }
    }

    static class SugestaoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.btnSugestao) Button btnSugestao;

        public SugestaoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Callback callback) {
            btnSugestao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onSugestaoClick();
                }
            });
        }
    }
}
