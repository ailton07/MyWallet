package br.edu.ufam.ceteli.mywallet.classes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.edu.ufam.ceteli.mywallet.R;

/**
 * Created by rodrigo on 01/11/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{
    private static RecyclerViewAdapter instance = null;
    private List<Entrada> items = null;
    private String[] categories = null;
    private View view = null;
    private Entrada object = null;

    public static RecyclerViewAdapter getInstance(List<Entrada> list){
        return instance == null? instance = new RecyclerViewAdapter(list) : instance;
    }

    public RecyclerViewAdapter(List<Entrada> list){
        items = list;
    }

    public void add(Entrada item){
        items.add(0, item);
        notifyItemInserted(0);
    }

    public void remove(Entrada item){
        int index = items.indexOf(item);
        items.remove(index);
        notifyItemRemoved(index);
    }

    public Entrada getClickedItem(){
        return object;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_reports, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
        holder.tvDate.setText(items.get(position).getDataCompra());
        holder.tvDescription.setText(items.get(position).getDescricao());
        holder.tvPlace.setText(items.get(position).getEstabelecimento());
        holder.tvType.setText(view.getResources().getStringArray(R.array.categoria_array)[items.get(position).getCategoria()]);
        holder.tvValue.setText(numberFormat.format(items.get(position).getValor()));
        holder.ivType.setImageDrawable(items.get(position).getTipo() == 1 ? view.getResources().getDrawable(R.drawable.ic_spending) : view.getResources().getDrawable(R.drawable.ic_earning));
        holder.itemView.setLongClickable(true);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                object = items.get(holder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription = null;
        private TextView tvValue = null;
        private TextView tvType = null;
        private TextView tvDate = null;
        private TextView tvPlace = null;
        private ImageView ivType = null;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvDescription = (TextView) itemView.findViewById(R.id.tvM);
            tvValue = (TextView) itemView.findViewById(R.id.tvB);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvDate = (TextView) itemView.findViewById(R.id.tvMonth);
            tvPlace = (TextView) itemView.findViewById(R.id.tvT);
            ivType = (ImageView) itemView.findViewById(R.id.ivType);
        }
    }
}
