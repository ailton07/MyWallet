package br.edu.ufam.ceteli.mywallet.classes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.edu.ufam.ceteli.mywallet.R;

/**
 * Created by rodrigo on 03/11/15.
 */
public class RecyclerViewBudgetAdapter extends RecyclerView.Adapter<RecyclerViewBudgetAdapter.RecyclerViewHolder>{
    private static RecyclerViewBudgetAdapter instance = null;
    private List<Entry> items = null;
    private View view = null;

    public static RecyclerViewBudgetAdapter getInstance(List<Entry> list){
        return instance == null? instance = new RecyclerViewBudgetAdapter(list) : instance;
    }


    private RecyclerViewBudgetAdapter(List<Entry> list){
        items = list;
    }

    public void add(Entry entry){
        items.add(0, entry);
        notifyItemInserted(0);
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_budgets, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("pt","BR"));
        holder.tvBudget.setText(numberFormat.format(items.get(position).getOrcamento()));
        holder.tvBonus.setText(numberFormat.format(items.get(position).getBonus()));
        holder.tvTotal.setText(numberFormat.format(items.get(position).getTotal()));
        holder.tvMonth.setText(items.get(position).getDataOrcamento());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBudget = null;
        private TextView tvBonus = null;
        private TextView tvTotal = null;
        private TextView tvMonth = null;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvBudget = (TextView) itemView.findViewById(R.id.tvBudget);
            tvBonus = (TextView) itemView.findViewById(R.id.tvBonus);
            tvTotal = (TextView) itemView.findViewById(R.id.tvTotal);
            tvMonth = (TextView) itemView.findViewById(R.id.tvMonth);
        }
    }
}
