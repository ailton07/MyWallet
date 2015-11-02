package br.edu.ufam.ceteli.mywallet.activities.reports;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;


/**
 * Created by Asus on 04/10/2015.
 */
public class GraphicReport extends Fragment implements OnChartValueSelectedListener{
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

        categoria_array.add("Residencia");
        categoria_array.add("Alimenticios");
        categoria_array.add("Entretenimento");
        categoria_array.add("Transporte");
        categoria_array.add("Saude");
        categoria_array.add("Ocasional");

        mChart = (PieChart) view.findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");

        // tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        // mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(false);

        mChart.setOnChartValueSelectedListener(this);

        mChart.setCenterText("Porcentagem de Gastos");

        mChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);


        setData(100);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    private void setData(float range) {
        int count =6;

        float mult = range;
        DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
        Date date = new Date();
        Calendar cal = Calendar.getInstance();

        //System.out.println(dateFormat.format(date));
        //int mes=Integer.parseInt(dateFormat.format(date).substring(6));
        int mes = cal.get(Calendar.MONTH) + 1;
        //int ano=Integer.parseInt(dateFormat.format(date).substring(0,3));
        int ano = cal.get(Calendar.YEAR);
        Log.e("App123",mes+" "+ano);

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
//        for (int i = 0; i < count + 1; i++) {
//            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
//        }

        if(Utils.getGastosCasa(mes, ano)!=0){
            yVals1.add(new Entry(Utils.getGastosCasa(mes, ano),0));
        }

        if (Utils.getGastosAlimenticios(mes, ano) != 0){
            yVals1.add(new Entry(Utils.getGastosAlimenticios(mes, ano),1));
        }

        if (Utils.getGastosEntrentenimento(mes, ano) != 0){
            yVals1.add(new Entry(Utils.getGastosEntrentenimento(mes, ano),2));
        }

        if (Utils.getGastosTransporte(mes, ano) != 0){
            yVals1.add(new Entry(Utils.getGastosTransporte(mes, ano),3));
        }

        if (Utils.getGastosSaude(mes, ano) != 0){
            yVals1.add(new Entry(Utils.getGastosSaude(mes, ano),4));
        }

        if (Utils.getGastosOutros(mes, ano) != 0){
            yVals1.add(new Entry(Utils.getGastosOutros(mes, ano),5));
        }









        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count + 1; i++)
                xVals.add(categoria_array.get(i % categoria_array.size()));

        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tf);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }



}
