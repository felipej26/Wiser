package br.com.wiser.features.mensagem;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import br.com.wiser.R;
import br.com.wiser.utils.Utils;
import br.com.wiser.utils.UtilsDate;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.RealmList;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Jefferson on 30/05/2016.
 */
public class MensagemAdapter extends RealmRecyclerViewAdapter<Mensagem, RecyclerView.ViewHolder> {

    public interface Callback {
        void onSugestaoClick();
    }

    public interface IMensagensListener {
        void onMensagensChanged();
    }

    private List<MensagemAbstract> listaMensagensAbstratas;
    private RealmList<Mensagem> listaMensagens;

    private final int VIEW_MENSAGEM_USUARIO = 0;
    private final int VIEW_MENSAGEM_CONTATO = 1;
    private final int VIEW_BOTAO_SUGESTAO = 2;
    private final int VIEW_DATA = 3;

    private boolean hasSugestao;
    private Callback callback;

    public MensagemAdapter(RealmList<Mensagem> listaMensagens, final IMensagensListener listener) {
        super(listaMensagens, false);
        listaMensagens.addChangeListener(new OrderedRealmCollectionChangeListener<RealmList<Mensagem>>() {
            @Override
            public void onChange(RealmList<Mensagem> mensagens, OrderedCollectionChangeSet changeSet) {
                if (changeSet == null) {
                    notifyDataSetChanged();
                    return;
                }

                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                for (int i = deletions.length - 1; i >= 0; i--) {
                    OrderedCollectionChangeSet.Range range = deletions[i];
                    notifyItemRangeRemoved(listaMensagensAbstratas.size() - 1, range.length);
                }

                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    notifyItemRangeChanged(listaMensagensAbstratas.size() - 1, range.length);
                }

                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    notifyItemRangeChanged(listaMensagensAbstratas.size() - 1, range.length);
                }

                listener.onMensagensChanged();
            }
        });
        this.listaMensagens = listaMensagens;
    }

    @Override
    public int getItemViewType(int position) {

        if (position != listaMensagensAbstratas.size()) {
            switch (listaMensagensAbstratas.get(position).getTipo()) {
                case MENSAGEM_DATA:
                    return VIEW_DATA;
                case MENSAGEM_CONTATO:
                    return VIEW_MENSAGEM_CONTATO;
                case MENSAGEM_USUARIO:
                    return VIEW_MENSAGEM_USUARIO;
            }
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
            case VIEW_DATA:
                return new DataViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.chat_mensagens_data, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_MENSAGEM_USUARIO:
            case VIEW_MENSAGEM_CONTATO:
                ((MensagensViewHolder)holder).bind(((MensagemObject)listaMensagensAbstratas.get(position)).getMensagem());
                break;
            case VIEW_BOTAO_SUGESTAO:
                ((SugestaoViewHolder)holder).bind(callback);
                break;
            case VIEW_DATA:
                ((DataViewHolder)holder).bind(((MensagemData)listaMensagensAbstratas.get(position)).getData());
                break;
        }
    }

    @Override
    public int getItemCount() {
        super.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
        });

        LinkedHashMap<String, Set<Mensagem>> mapMensagens = new LinkedHashMap<>();
        listaMensagensAbstratas = new LinkedList<>();

        for (Mensagem mensagem : listaMensagens) {
            String data = UtilsDate.formatDate(mensagem.getData(), UtilsDate.DDMMYYYY);

            if (mapMensagens.containsKey(data)) {
                mapMensagens.get(data).add(mensagem);
            }
            else {
                Set<Mensagem> mensagens = new LinkedHashSet<>();
                mensagens.add(mensagem);
                mapMensagens.put(data, mensagens);
            }
        }

        for (String data : mapMensagens.keySet()) {
            MensagemData dataItem = new MensagemData();
            dataItem.setData(data);
            listaMensagensAbstratas.add(dataItem);

            for (Mensagem mensagem : mapMensagens.get(data)) {
                MensagemObject mensagemItem = new MensagemObject();
                mensagemItem.setMensagem(mensagem);
                listaMensagensAbstratas.add(mensagemItem);
            }
        }

        /* Soma um item para poder incluir o botão, se tiver uma sugestão */
        return listaMensagensAbstratas.size() + (hasSugestao ? 1 : 0);
    }

    public void onSetSugestao(Callback callback) {
        this.callback = callback;
        hasSugestao = true;
        notifyDataSetChanged();
    }

    static class MensagensViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.lblDataHora) TextView lblDataHora;
        @BindView(R.id.lblMensagem) TextView lblMensagem;

        public MensagensViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(Mensagem mensagem) {
            lblDataHora.setText(UtilsDate.formatDate(mensagem.getData(), UtilsDate.HHMM));
            lblMensagem.setText(Utils.decode(mensagem.getMensagem().trim()));

            /*
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
            */
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

    static class DataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.lblData) TextView lblData;

        public DataViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(String data) {
            lblData.setText(data);
        }
    }
}
