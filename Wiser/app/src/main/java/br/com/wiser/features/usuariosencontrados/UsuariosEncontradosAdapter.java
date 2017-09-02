package br.com.wiser.features.usuariosencontrados;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.wiser.R;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.IClickListener;
import br.com.wiser.utils.Utils;

/**
 * Created by Wesley on 08/04/2016.
 */
public class UsuariosEncontradosAdapter extends RecyclerView.Adapter<UsuariosEncontradosAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Usuario> listaUsuarios = null;

    private IClickListener clickListener;

    public UsuariosEncontradosAdapter(Context context, ArrayList<Usuario> listaUsuarios) {
        this.context = context;
        this.listaUsuarios = listaUsuarios;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.contatos_encontrar_pessoas_resultados_grid, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);
        holder.txtNome.setText(usuario.getPrimeiroNome());
        Utils.loadImageInBackground(context, usuario.getUrlFotoPerfil(), holder.imgPerfil, holder.prgBarra);
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public void setClickListener(IClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgPerfil;
        ProgressBar prgBarra;
        TextView txtNome;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            imgPerfil = (ImageView) itemView.findViewById(R.id.imgPerfil);
            prgBarra = (ProgressBar) itemView.findViewById(R.id.prgBarra);
            txtNome = (TextView) itemView.findViewById(R.id.txtNomeLista);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.itemClicked(v, getAdapterPosition());
            }
        }
    }
}