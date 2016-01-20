package br.edu.ufam.ceteli.mywallet.activities.drawer.fragments;

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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

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
public class MainScreenActivity extends Fragment implements Observer{
    private FloatingActionMenu fabNewInput = null;
    private static Fragment instance = null;
    private LineChartView chartView = null;
    private LineSet mesAtual = null;
    LineSet mesAnterior = null;

    TextView orcamento, gastos, saldo, renda;
    Calendar c = Calendar.getInstance();
    int mes, ano, semana;
    float aux, aux1, aux2, aux3, aux4;

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
        chartView = (LineChartView) view.findViewById(R.id.mainChart);

        mes = c.get(Calendar.MONTH) + 1;
        ano = c.get(Calendar.YEAR);
        semana = c.get(Calendar.WEEK_OF_MONTH);

        Log.i("App123", String.valueOf(semana));
        Log.i("App123", String.valueOf(Utils.getGastosSemana(semana, mes, ano)));

        float a = Utils.getGastosSemana(1, mes ,ano);
        float b = Utils.getGastosSemana(2, mes, ano);
        float e = Utils.getGastosSemana(3, mes, ano);
        float d = Utils.getGastosSemana(4, mes, ano);

        float f = Utils.getGastosSemana(1, mes-1 ,ano);
        float g = Utils.getGastosSemana(2, mes-1 ,ano);
        float h = Utils.getGastosSemana(3, mes-1 ,ano);
        float i = Utils.getGastosSemana(4, mes-1 ,ano);

        float[] algo = {Utils.getGastosSemana(1, mes ,ano), Utils.getGastosSemana(2, mes, ano), Utils.getGastosSemana(3, mes, ano),  Utils.getGastosSemana(4, mes, ano)};
        float[] algo2 = {Utils.getGastosSemana(1, mes-1 ,ano), Utils.getGastosSemana(2, mes-1, ano), Utils.getGastosSemana(3, mes-1, ano),  Utils.getGastosSemana(4, mes-1, ano)};
        String[] labels = {"Semana1", "Semana2", "Semana3", "Semana4"};

        mesAtual = new LineSet(labels, algo);
        mesAnterior = new LineSet(labels, algo2);

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



        if(f>g && f>h)
            aux2 = f;
        else if(g>h)
            aux2 = g;
        else
            aux2 = h;

        if(aux2>i)
            aux3 = aux2;
        else
            aux3 = i;

        if(aux1>aux3){
            aux4=aux1;
        }else{
            aux4=aux3;
        }

        if(aux4 == 0){
            chartView.setAxisBorderValues(0, 1, 1);
        }else{
            if(aux4%2==0) {
                chartView.setAxisBorderValues(0, (int) aux4, (int) aux4 / 2);
            }else{
                chartView.setAxisBorderValues(0, (int) aux4 + 1, (((int) aux4) + 1) / 2);
            }
        }

        chartView.addData(mesAnterior);
        chartView.addData(mesAtual);

        chartView.setAxisThickness(Tools.fromDpToPx(1));
        chartView.setAxisColor(Color.parseColor("#4E000000"));

        Animation animation = new Animation(2000);
        animation.setEasing(new CubicEase());

        chartView.show(animation);

        orcamento = (TextView) view.findViewById(R.id.textView23);
        renda = (TextView) view.findViewById(R.id.textView24);
        gastos = (TextView) view.findViewById(R.id.textView25);
        saldo = (TextView) view.findViewById(R.id.textView27);

        orcamento.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(Utils.getSaldo(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        renda.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(Utils.getSaldoOrcamentoTotal(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        gastos.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(Utils.getSaidaMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        saldo.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(getSaldoMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
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

    @Override
    public void update(Observable observable, Object data) {
        orcamento.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(Utils.getSaldo(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        renda.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(Utils.getSaldoOrcamentoTotal(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        gastos.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(Utils.getSaidaMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        saldo.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(getSaldoMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));

        float[] algo = {Utils.getGastosSemana(1, mes ,ano), Utils.getGastosSemana(2, mes, ano), Utils.getGastosSemana(3, mes, ano),  Utils.getGastosSemana(4, mes, ano)};
        float[] algo2 = {Utils.getGastosSemana(1, mes-1 ,ano), Utils.getGastosSemana(2, mes-1, ano), Utils.getGastosSemana(3, mes-1, ano),  Utils.getGastosSemana(4, mes-1, ano)};

        float a = Utils.getGastosSemana(1, mes ,ano);
        float b = Utils.getGastosSemana(2, mes, ano);
        float e = Utils.getGastosSemana(3, mes, ano);
        float d = Utils.getGastosSemana(4, mes, ano);

        float f = Utils.getGastosSemana(1, mes-1 ,ano);
        float g = Utils.getGastosSemana(2, mes-1 ,ano);
        float h = Utils.getGastosSemana(3, mes-1 ,ano);
        float i = Utils.getGastosSemana(4, mes-1 ,ano);

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




        if(f>g && f>h)
            aux2 = f;
        else if(g>h)
            aux2 = g;
        else
            aux2 = h;

        if(aux2>i)
            aux3 = aux2;
        else
            aux3 = i;

        if(aux1>aux3){
            aux4=aux1;
        }else{
            aux4=aux3;
        }





        if(aux4 == 0){
            chartView.setAxisBorderValues(0, 1, 1);
        }else{
            if(aux4%2==0) {
                chartView.setAxisBorderValues(0, (int) aux4, (int) aux4 / 2);
            }else{
                chartView.setAxisBorderValues(0, (int) aux4 + 1, (((int) aux4) + 1) / 2);
            }
        }

        mesAtual.updateValues(algo);
        mesAnterior.updateValues(algo2);

        chartView.show();
    }
}
