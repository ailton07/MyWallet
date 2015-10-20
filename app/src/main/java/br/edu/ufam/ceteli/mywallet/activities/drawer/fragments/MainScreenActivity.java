package br.edu.ufam.ceteli.mywallet.activities.drawer.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.activities.dialog.fragments.DialogNoPhoto;
import br.edu.ufam.ceteli.mywallet.activities.dialog.fragments.DialogPhoto;
import br.edu.ufam.ceteli.mywallet.classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.classes.Entrada;
import br.edu.ufam.ceteli.mywallet.classes.IUpdateListView;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;

import static br.edu.ufam.ceteli.mywallet.classes.ocr.Utils.getSaldoMes;

/**
 * Created by rodrigo on 18/10/15.
 */
public class MainScreenActivity extends Fragment implements OnChartValueSelectedListener, IUpdateListView {
    // Nova entrada (Botão Flutuante)
    private FloatingActionsMenu fabNewInput = null;

    // Singleton
    private static Fragment instance = null;


    // private ArrayAdapter<Entrada> adapter;
    private AdapterListView adapter;
    TextView orcamento, gastos, saldo, renda;
    Calendar c = Calendar.getInstance();

    private List<String> mesano=new ArrayList<>();
    private LineChart mChart;

    public static Fragment getInstance() {
        return (instance == null)? instance = new MainScreenActivity() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Quando for mexer na lista, fechar esse FAB
        fabNewInput = (FloatingActionsMenu) view.findViewById(R.id.fabNewInput);

        FloatingActionButton fabPhotoInput = (FloatingActionButton) view.findViewById(R.id.fabPhotoInput);
        fabPhotoInput.setIcon(R.drawable.ic_photo_fab);
        fabPhotoInput.setSize(FloatingActionButton.SIZE_MINI);
        fabPhotoInput.setOnClickListener(fabPhotoOnClick());

        FloatingActionButton fabManualInput = (FloatingActionButton) view.findViewById(R.id.fabManualInput);
        fabManualInput.setIcon(R.drawable.ic_manual_fab);
        fabManualInput.setSize(FloatingActionButton.SIZE_MINI);
        fabManualInput.setOnClickListener(fabManualOnClick());

        // Pega Toolbar
        getActivity().findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.toolbar_main));
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).setTitle("Resumo");

        List<Entrada> values = Entrada.getComments();

        adapter = new AdapterListView(getContext(), values);

        //ListView lv = (ListView) findViewById(android.R.id.list);
        //setListAdapter(adapter);
        //lv.setAdapter(adapter);
        //lv.setAdapter(adapter);

        //List<Entrada> valuesDoMes = Entrada.getEntradasMesAno(10,2015);
        Log.d("Saldo", String.valueOf(getSaldoMes(10, 2015)));

        mChart = (LineChart) view.findViewById(R.id.chart1);
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

        setData(12, 20.0f);


        orcamento = (TextView) view.findViewById(R.id.textView23);
        renda = (TextView) view.findViewById(R.id.textView24);
        gastos = (TextView) view.findViewById(R.id.textView25);
        saldo = (TextView) view.findViewById(R.id.textView27);

        orcamento.setText(String.valueOf(Utils.getOrcamentoTotalMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        renda.setText(String.valueOf(Utils.getSaldoOrcamentoTotal(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        gastos.setText(String.valueOf(Utils.getgastosMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        saldo.setText(String.valueOf(getSaldoMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
    }

    private View.OnClickListener fabManualOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNoPhoto dialogNoPhoto = new DialogNoPhoto();
                dialogNoPhoto.show(getFragmentManager(), null);
                fabNewInput.collapse();
            }
        };
    }

    private View.OnClickListener fabPhotoOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPhoto dialogPhoto = new DialogPhoto();
                dialogPhoto.show(getFragmentManager(), null);
                fabNewInput.collapse();
            }
        };
    }

    public void restartArryaListAdapter(){
        List<Entrada> values = Entrada.getComments();
        adapter = new AdapterListView(getContext(), values);
        ListView lv = (ListView) getView().findViewById(android.R.id.list);
        lv.setAdapter(adapter);
    }

    // Faz um update quando é adicionado um novo elemento a partir do dialogFragment
    @Override
    public void onListUpdated(Entrada newValueToAdd) {
        adapter.notifyDataSetInvalidated();
        adapter.add(newValueToAdd);
        adapter.notifyDataSetChanged();
    }

    private void setData(int count,float range) {
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            xVals.add(mesano.get(i));
        }

        ArrayList<Entry> yVals1 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 50;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(new Entry(val, i));
        }

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
        set1.setDrawCircles(false);
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
