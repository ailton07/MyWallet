package br.edu.ufam.ceteli.mywallet.activities.budget.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.CubicEase;

import java.util.Calendar;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;

/**
 * Created by rodrigo on 03/11/15.
 */
public class BudgetGraphic extends Fragment {
    private static Fragment instance = null;
    int mes, ano, semana;
    Calendar c = Calendar.getInstance();
    float aux, aux1;

    public static Fragment getInstance() {
        return (instance == null)? instance = new BudgetGraphic() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget_graphic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LineChartView chartView = (LineChartView) view.findViewById(R.id.view2);

        mes = c.get(Calendar.MONTH) + 1;
        ano = c.get(Calendar.YEAR);
        semana = c.get(Calendar.WEEK_OF_MONTH);

        Log.i("App123", String.valueOf(Utils.getOrcamentoTotalSemana(semana, mes, ano)));

        float a = Utils.getOrcamentoTotalSemana(1, mes ,ano);
        float b = Utils.getOrcamentoTotalSemana(2, mes, ano);
        float e = Utils.getOrcamentoTotalSemana(3, mes, ano);
        float d = Utils.getOrcamentoTotalSemana(4, mes, ano);

        //float[] algo = {1.0F,5.0F,6.0F};
        float[] algo = {Utils.getOrcamentoTotalSemana(1, mes ,ano), Utils.getOrcamentoTotalSemana(2, mes, ano), Utils.getOrcamentoTotalSemana(3, mes, ano),  Utils.getOrcamentoTotalSemana(4, mes, ano)};
        float[] algo2 = {Utils.getOrcamentoTotalSemana(1, mes - 1, ano), Utils.getOrcamentoTotalSemana(2, mes - 1, ano), Utils.getOrcamentoTotalSemana(3, mes - 1, ano),  Utils.getOrcamentoTotalSemana(4, mes - 1, ano)};
        String[] labels = {"Semana1", "Semana2", "Semana3", "Semana4"};

        LineSet mesAtual = new LineSet(labels, algo);
        LineSet mesAnterior = new LineSet(labels,algo2);

        mesAtual.setFill(getResources().getColor(R.color.chartActualMonth1));
        mesAnterior.setFill(getResources().getColor(R.color.chartLastMonth1));

        mesAtual.setThickness(Tools.fromDpToPx(0));
        mesAnterior.setThickness(Tools.fromDpToPx(0));

        if(a>b && a>e)
            aux = a;
        else if(b>e)
            aux = b;
        else
            aux = e;

        if(aux>d)
            aux1 = aux;
        else
            aux1 = d;

        if(aux1 == 0){
            chartView.setAxisBorderValues(0, 1, 1);
        }else{
            if(aux1%2==0) {
                chartView.setAxisBorderValues(0, (int) aux1, (int) aux1 / 2);
            }else{
                chartView.setAxisBorderValues(0, (int) aux1, (int) aux1 / 3);
            }
        }


        chartView.addData(mesAnterior);
        chartView.addData(mesAtual);

        Animation animation = new Animation(2000);
        animation.setEasing(new CubicEase());

        chartView.show(animation);


    }
}
