package br.edu.ufam.ceteli.mywallet.activities.drawer.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.CubicEase;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
import br.edu.ufam.ceteli.mywallet.activities.dialog.fragments.DialogNoPhoto;
import br.edu.ufam.ceteli.mywallet.activities.dialog.fragments.DialogPhoto;
import br.edu.ufam.ceteli.mywallet.classes.DividerItemDecoration;
import br.edu.ufam.ceteli.mywallet.classes.Entrada;
import br.edu.ufam.ceteli.mywallet.classes.RecyclerViewAdapter;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;

import static br.edu.ufam.ceteli.mywallet.classes.ocr.Utils.getSaldoMes;

/**
 * Created by rodrigo on 18/10/15.
 */
public class MainScreenActivity extends Fragment implements OnChartValueSelectedListener {
    private FloatingActionMenu fabNewInput = null;
    private static Fragment instance = null;

    TextView orcamento, gastos, saldo, renda;
    Calendar c = Calendar.getInstance();
    int mes, ano, semana;
    float aux, aux1;

    private List<String> mesano=new ArrayList<>();
    private LineChart mChart;

    public static Fragment getInstance() {
        return (instance == null)? instance = new MainScreenActivity() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle("Resumo");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().findViewById(R.id.appBarLayout).setElevation(((AppBarLayout) getActivity().findViewById(R.id.appBarLayout)).getTargetElevation());
        }
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Quando for mexer na lista, fechar esse FAB
        fabNewInput = (FloatingActionMenu) view.findViewById(R.id.fabNewInput);
        fabNewInput.setClosedOnTouchOutside(true);

        com.github.clans.fab.FloatingActionButton fabPhotoInput = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fabPhotoInput);
        fabPhotoInput.setOnClickListener(fabPhotoOnClick());

        FloatingActionButton fabManualInput = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fabManualInput);
        fabManualInput.setOnClickListener(fabManualOnClick());

        // RecyclerView
        // TODO: Atualizar na inserção
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewMainScreen);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(RecyclerViewAdapter.getInstance(Entrada.getComments()));

        // Gráfico
        LineChartView chartView = (LineChartView) view.findViewById(R.id.mainChart);

        mes = c.get(Calendar.MONTH) + 1;
        ano = c.get(Calendar.YEAR);
        semana = c.get(Calendar.WEEK_OF_MONTH);

        Log.i("App123", String.valueOf(semana));
        Log.i("App123", String.valueOf(Utils.getGastosSemana(semana, mes, ano)));

        float a = Utils.getGastosSemana(1, mes ,ano);
        float b = Utils.getGastosSemana(2, mes, ano);
        float e = Utils.getGastosSemana(3, mes, ano);
        float d = Utils.getGastosSemana(4, mes, ano);

        float[] algo = {Utils.getGastosSemana(1, mes ,ano), Utils.getGastosSemana(2, mes, ano), Utils.getGastosSemana(3, mes, ano),  Utils.getGastosSemana(4, mes, ano)};
        float[] algo2 = {Utils.getGastosSemana(1, mes-1 ,ano), Utils.getGastosSemana(2, mes-1, ano), Utils.getGastosSemana(3, mes-1, ano),  Utils.getGastosSemana(4, mes-1, ano)};
        String[] labels = {"Semana1", "Semana2", "Semana3", "Semana4"};

        LineSet mesAtual = new LineSet(labels, algo);
        LineSet mesAnterior = new LineSet(labels, algo2);

        mesAtual.setFill(getResources().getColor(R.color.chartActualMonth));
        mesAnterior.setFill(getResources().getColor(R.color.chartLastMonth));

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

        chartView.setAxisThickness(Tools.fromDpToPx(1));
        chartView.setAxisColor(Color.parseColor("#4E000000"));

        Animation animation = new Animation(2000);
        animation.setEasing(new CubicEase());

        chartView.show(animation);

        /*mChart = (LineChart) view.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        mChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);

        mesano.add("Jan/15");
        mesano.add("Fev/15");
        mesano.add("Mar/15");
        mesano.add("Abr/15");
        mesano.add("Mai/15");
        mesano.add("Jun/15");
        mesano.add("Jul/15");
        mesano.add("Ago/15");
        mesano.add("Set/15");
        mesano.add("Out/15");
        mesano.add("Nov/15");
        mesano.add("Dez/15");

        setData(12, 20.0f);*/


        orcamento = (TextView) view.findViewById(R.id.textView23);
        renda = (TextView) view.findViewById(R.id.textView24);
        gastos = (TextView) view.findViewById(R.id.textView25);
        saldo = (TextView) view.findViewById(R.id.textView27);

        orcamento.setText(String.valueOf(Utils.getOrcamentoTotalMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        renda.setText(String.valueOf(Utils.getSaldoOrcamentoTotal(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        gastos.setText(String.valueOf(Utils.getSaidaMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        saldo.setText(String.valueOf(getSaldoMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
    }

    private View.OnClickListener fabManualOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNoPhoto dialogNoPhoto = new DialogNoPhoto();
                dialogNoPhoto.show(getFragmentManager(), null);
                fabNewInput.close(true);
            }
        };
    }

    private View.OnClickListener fabPhotoOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPhoto dialogPhoto = new DialogPhoto();
                dialogPhoto.show(getFragmentManager(), null);
                fabNewInput.close(true);
            }
        };
    }

    private void setData(int count,float range) {
        ArrayList<String> xVals = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
        Date date = new Date();
        //System.out.println(dateFormat.format(date));
        int mes=Integer.parseInt(dateFormat.format(date).substring(6));
        int ano=Integer.parseInt(dateFormat.format(date).substring(0,3));
        Log.e("graphic", mes + " " + ano);
        ArrayList<Entry> yVals1 = new ArrayList<>();
        int cn=0;
        int i=12-(12-mes);
        Log.e("App123", "i" + Integer.toString(i));
        /*for ( i = i; i < count; i++) {
            //Log.e("App123", "i" + Integer.toString(i));
            xVals.add(mesano.get(i));
            yVals1.add(new Entry((float) Utils.getSaidaMes(i,ano),cn));
            if(i==11) {
                i = 0;
                ano = ano + 1;
            }
            cn++;
            }*/






//        for (int i = 0; i < count; i++) {
//            float mult = range / 2f;
//            float val = (float) (Math.random() * mult) + 50;// + (float)
//            // ((mult *
//            // 0.1) / 10);
//            yVals1.add(new Entry(val, i));
//        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals1, "Gastos mensais");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleSize(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircles(true);
        set1.setDrawFilled(true);
        //set1.setFillFormatter(new MyFillFormatter(0f));
//        set1.setDrawHorizontalHighlightIndicator(false);
//        set1.setVisible(false);
//        set1.setCircleHoleColor(Color.WHITE);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        android.util.Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        android.util.Log.i("Nothing selected", "Nothing selected.");
    }
}
