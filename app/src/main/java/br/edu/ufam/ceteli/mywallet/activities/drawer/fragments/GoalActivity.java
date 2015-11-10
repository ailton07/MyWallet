package br.edu.ufam.ceteli.mywallet.activities.drawer.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;

/**
 * Created by rodrigo on 09/11/15.
 */
public class GoalActivity extends Fragment {
    private static Fragment instance = null;

    public static Fragment getInstance() {
        return (instance == null)? instance = new GoalActivity() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle("Planejamento");

        TextView meta = (TextView) view.findViewById(R.id.tvMetaValue);
        TextView spend = (TextView) view.findViewById(R.id.tvSpendValue);
        TextView leftover = (TextView) view.findViewById(R.id.tvLeftoverValue);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getContext(), R.array.categoria_array1, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinnerGoalFilter);
        spinner.setAdapter(adapterSpinner);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SELECTED_FILTER", 0);

        spinner.setOnItemSelectedListener(onItemSelectedListener(meta, spend, leftover));
        spinner.setSelection(0, true);

        return view;
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener(final TextView meta, final TextView spend, final TextView leftover){
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                Calendar calendar = Calendar.getInstance();

                numberFormat.setGroupingUsed(true);

                switch (position){
                    case 0:
                        meta.setText("Nenhum Filtro Definido");
                        spend.setText("Nenhum Filtro Definido");
                        leftover.setText("Nenhum Filtro Definido");
                        break;

                    case 1:
                        spend.setText(numberFormat.format(Utils.getSaidaDia(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))));
                        leftover.setText(numberFormat.format(CalcularSaldoDia(meta, calendar)));
                        break;

                    case 2:
                        spend.setText(numberFormat.format(Utils.getSaidaMes(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))));
                        leftover.setText(numberFormat.format(CalcularSaldoMes(meta, calendar)));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    @Override
    public void onResume(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SELECTED_FILTER", 0);
        Log.e("META", sharedPreferences.getString("Meta", "Vazio"));
        super.onResume();

    }

    public float CalcularSaldoDia(TextView meta, Calendar calendar){
        float goal, leftover;
        try{
            goal = Float.valueOf(meta.getText().toString());
            leftover = goal - Utils.getSaidaDia(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        } catch (Exception e){
            leftover = 0;
        }
        return leftover;
    }

    public float CalcularSaldoMes(final TextView meta, Calendar calendar){
        float goal, leftover;
        try{
            goal = Float.valueOf(meta.getText().toString());
            leftover = goal - Utils.getSaidaMes(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        } catch (Exception e){
            leftover = 0;
        }
        return leftover;
    }

    public float CalcularSaldo(int valor, final TextView meta, Calendar calendar){
        float goal, leftover = 0;
        goal = Float.valueOf(meta.getText().toString());
        if(valor==1) {
            leftover = goal - Utils.getSaidaDia(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        }
        if(valor==2){
            leftover = goal - Utils.getSaidaMes(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        }
        return leftover;
    }
}
