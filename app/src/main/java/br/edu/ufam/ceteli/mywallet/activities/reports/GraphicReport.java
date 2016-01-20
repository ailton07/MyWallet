package br.edu.ufam.ceteli.mywallet.activities.reports;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;


/**
 * Created by Asus on 04/10/2015.
 */
public class GraphicReport extends Fragment {
    private TextView tvHomeP;
    private TextView tvFoodP;
    private TextView tvLoungeP;
    private TextView tvTransportP;
    private TextView tvHealthP;
    private TextView tvOthersP;
    private PieChart mChart;
    private Typeface tf;
    private List<String> categoria_array=new ArrayList<String>();
    private static Fragment instance = null;

    public static Fragment getInstance() {
        return (instance == null)? instance = new GraphicReport() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graphic_report, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvHomeP = (TextView) view.findViewById(R.id.textViewHomeP);
        tvFoodP = (TextView) view.findViewById(R.id.textViewFoodP);
        tvLoungeP = (TextView) view.findViewById(R.id.textViewLoungeP);
        tvTransportP = (TextView) view.findViewById(R.id.textViewTransportP);
        tvHealthP = (TextView) view.findViewById(R.id.textViewHealthP);
        tvOthersP = (TextView) view.findViewById(R.id.textViewOthersP);

        categoria_array.add("Residencia");
        categoria_array.add("Alimenticios");
        categoria_array.add("Entretenimento");
        categoria_array.add("Transporte");
        categoria_array.add("Saude");
        categoria_array.add("Ocasional");

        mChart = (PieChart) view.findViewById(R.id.pieChart);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setDrawHoleEnabled(false);
        mChart.setDrawCenterText(false);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(false);
        mChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        mChart.setDrawSliceText(false);
        mChart.getLegend().setEnabled(false);
        setData();
    }


    private void setData() {
        ArrayList<Entry> yVals = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int mes = cal.get(Calendar.MONTH) + 1;
        int ano = cal.get(Calendar.YEAR);
        double homeP, foodP, loungeP, transportP, healthP, othersP;

        yVals.add(new Entry(Utils.getGastosCasa(mes, ano), 1));
        yVals.add(new Entry(Utils.getGastosAlimenticios(mes, ano),1));
        yVals.add(new Entry(Utils.getGastosEntrentenimento(mes, ano),1));
        yVals.add(new Entry(Utils.getGastosTransporte(mes, ano),1));
        yVals.add(new Entry(Utils.getGastosSaude(mes, ano),1));
        yVals.add(new Entry(Utils.getGastosOutros(mes, ano),1));


        ArrayList<String> xVals = new ArrayList<>();

        for (int i = 0; i < 6 + 1; i++)
                xVals.add(categoria_array.get(i % categoria_array.size()));

        PieDataSet dataSet = new PieDataSet(yVals, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        homeP = (yVals.get(0).getVal() * 100)/dataSet.getYValueSum();
        foodP = (yVals.get(1).getVal() * 100)/dataSet.getYValueSum();
        loungeP = (yVals.get(2).getVal() * 100)/dataSet.getYValueSum();
        transportP = (yVals.get(3).getVal() * 100)/dataSet.getYValueSum();
        healthP = (yVals.get(4).getVal() * 100)/dataSet.getYValueSum();
        othersP = (yVals.get(5).getVal() * 100)/dataSet.getYValueSum();

        /*tvHomeP.setText(NumberFormat.getPercentInstance(Locale.getDefault()).format(homeP / 100));
        tvFoodP.setText(NumberFormat.getPercentInstance(Locale.getDefault()).format(foodP / 100));
        tvLoungeP.setText(NumberFormat.getPercentInstance(Locale.getDefault()).format(loungeP / 100));
        tvTransportP.setText(NumberFormat.getPercentInstance(Locale.getDefault()).format(transportP / 100));
        tvHealthP.setText(NumberFormat.getPercentInstance(Locale.getDefault()).format(healthP / 100));
        tvOthersP.setText(NumberFormat.getPercentInstance(Locale.getDefault()).format(othersP / 100));*/

        tvHomeP.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(yVals.get(0).getVal()));
        tvFoodP.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(yVals.get(1).getVal()));
        tvLoungeP.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(yVals.get(2).getVal()));
        tvTransportP.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(yVals.get(3).getVal()));
        tvHealthP.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(yVals.get(4).getVal()));
        tvOthersP.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(yVals.get(5).getVal()));


        dataSet.addColor(getResources().getColor(R.color.toolbar_food));
        dataSet.addColor(getResources().getColor(R.color.toolbar_lounge));
        dataSet.addColor(getResources().getColor(R.color.toolbar_transport));
        dataSet.addColor(getResources().getColor(R.color.toolbar_health));
        dataSet.addColor(getResources().getColor(R.color.toolbar_others));
        dataSet.addColor(getResources().getColor(R.color.toolbar_home));

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(11f);

        mChart.setData(data);
    }



}
