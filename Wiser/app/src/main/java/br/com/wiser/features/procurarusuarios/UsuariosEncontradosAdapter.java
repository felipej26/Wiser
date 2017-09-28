package br.com.wiser.features.procurarusuarios;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.wiser.R;
import br.com.wiser.Sistema;
import br.com.wiser.WiserApplication;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.IClickListener;
import br.com.wiser.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wesley on 08/04/2016.
 */
public class UsuariosEncontradosAdapter extends RecyclerView.Adapter<UsuariosEncontradosAdapter.ViewHolder> {

    private ArrayList<Usuario> listaUsuarios = null;

    private IClickListener onViewClick;
    private IClickListener onChatClick;

    public UsuariosEncontradosAdapter(ArrayList<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_search_users, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);

        holder.viewSeparator.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);

        holder.txtNome.setText(usuario.getPrimeiroNome());
        Utils.loadImageInBackground(usuario.getUrlFotoPerfil(), holder.imgPerfil, holder.prgBarra);
        holder.lblIdiomaNivel.setText(WiserApplication.getAppContext().getString(R.string.fluencia_idioma,
                Sistema.getDescricaoFluencia(usuario.getFluencia()), Sistema.getDescricaoIdioma(usuario.getIdioma())));

        if (!usuario.isContato()) {
            holder.btnChat.setText("ADD");
        }
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public void setOnViewClick(IClickListener onViewClick) {
        this.onViewClick = onViewClick;
    }

    public void setOnChatClick(IClickListener onChatClick) {
        this.onChatClick = onChatClick;
    }

    public void addItems(List<Usuario> usuarios) {
        listaUsuarios.addAll(usuarios);
        notifyDataSetChanged();
    }

    public void updateItem(int posicao) {
        notifyItemChanged(posicao);
    }

    public void limparDados() {
        listaUsuarios = new ArrayList<>();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.viewSeparator) View viewSeparator;
        @BindView(R.id.imgPerfil) ImageView imgPerfil;
        @BindView(R.id.prgBarra) ProgressBar prgBarra;
        @BindView(R.id.txtNome) TextView txtNome;
        @BindView(R.id.lblIdiomaNivel) TextView lblIdiomaNivel;
        @BindView(R.id.btnChat) Button btnChat;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            if (onChatClick != null) {
                btnChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onChatClick.itemClicked(v, getAdapterPosition());
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            if (onViewClick != null) {
                onViewClick.itemClicked(v, getAdapterPosition());
            }
        }
    }
}