package br.edu.ufam.ceteli.mywallet.activities.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    int mes, ano;
    Calendar c = Calendar.getInstance();

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



        //float[] algo = {1.0F,5.0F,6.0F};
        float[] algo = {Utils.getSaidaMes(mes - 2, ano), Utils.getSaidaMes(mes - 1, ano), Utils.getSaidaMes(mes, ano)};
        float[] algo2 = {1000 ,2000, 4000};
        String[] labels = {"label1", "label2", "label3"};

        LineSet mesAtual = new LineSet(labels, algo);
        LineSet mesAnterior = new LineSet(labels,algo2);

        mesAtual.setFill(getResources().getColor(R.color.chartActualMonth1));
        mesAnterior.setFill(getResources().getColor(R.color.chartLastMonth1));

        mesAtual.setThickness(Tools.fromDpToPx(0));
        mesAnterior.setThickness(Tools.fromDpToPx(0));

        chartView.addData(mesAnterior);
        chartView.addData(mesAtual);

        Animation animation = new Animation(2000);
        animation.setEasing(new CubicEase());

        chartView.show(animation);


    }
}
