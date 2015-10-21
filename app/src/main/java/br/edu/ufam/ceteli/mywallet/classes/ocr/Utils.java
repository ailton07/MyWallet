package br.edu.ufam.ceteli.mywallet.classes.ocr;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.activities.ResultActivity;
import br.edu.ufam.ceteli.mywallet.classes.Entrada;
import br.edu.ufam.ceteli.mywallet.classes.Entry;

/**
 * Created by AiltonFH on 03/10/2015.
 */
public class Utils {

 //Método Chamador do popup, deve ser colocado na Activity que vai aparecer o popup
//    public void popup(String texto) {
//
//        int[] location = new int[2];
//        Toolbar button = (Toolbar) findViewById(R.id.toolbar);
//
//        // Get the x, y location and store it in the location[] array
//        // location[0] = x, location[1] = y.
//        button.getLocationOnScreen(location);
//
//        //Initialize the Point with x, and y positions
//        Point p = new Point();
//        p.x = location[0];
//        p.y = location[1];
//        //Alterar ResultActivity para a Activity corrente
//        Utils.showPopup(ResultActivity.this, p, texto);
//
//    }

    // The method that displays the popup.
   public static void showPopup(final Activity context, Point p, String texto) {
        int popupWidth = 250;
        int popupHeight = 225;

        // Inflate the popup_layout_up.xmlxml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout_up, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);


        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.close);

        TextView textoPopup = (TextView) layout.findViewById(R.id.textViewDicas);
        textoPopup.setText(texto);



        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }


    static public float getSaldoMes(int mes, int ano){
        // 0 -> Entrada e 1 -> Saída
        float saldo = 0;
        float saldo1 = 0;
        float saldo2 = 0;
        float saldoTot = 0;
        List<Entrada> values = Entrada.getEntradasMesAno(mes, ano);
        List<Entry> valores = Entry.getOrcamento(mes, ano);

        for (Entrada in:
                values) {
            if (in.getTipo() == 0) {
                saldo += in.getValor();
            } else {
                saldo = saldo - in.getValor();
            }
        }

        for (Entry x: valores){
            saldo1 += x.getOrcamento();
            saldo2 += x.getBonus();
        }

        saldoTot = saldo + saldo1 + saldo2;


        return saldoTot;

    }

    static public float getOrcamentoTotalMes(int mes, int ano){
        // 0 -> Entrada e 1 -> Saída
        float saldo = 0;
        float saldo1 = 0;
        float saldo2 = 0;
        float saldoTot = 0;
        List<Entrada> values = Entrada.getEntradasMesAno(mes, ano);
        List<Entry> valores = Entry.getOrcamento(mes, ano);

        for (Entrada in:
                values) {
            if (in.getTipo() == 0) {
                saldo += in.getValor();
            }
        }

        for (Entry x: valores){
            saldo1 += x.getOrcamento();
            saldo2 += x.getBonus();
        }

        saldoTot = saldo + saldo1 + saldo2;


        return saldoTot;

    }

    static public float getEntradaMes(int mes, int ano){
        // 0 -> Entrada e 1 -> Saída
        float entrada = 0;
        List<Entrada> values = Entrada.getEntradasMesAno(mes, ano);

        for (Entrada in:
                values) {
            if(in.getTipo() == 0){
                entrada += in.getValor();
            }

        }

        return entrada;

    }


    static public float getSaidaMes(int mes, int ano){
        // 0 -> Entrada e 1 -> Saída
        float saida = 0;
        List<Entrada> values = Entrada.getEntradasMesAno(mes, ano);

        for (Entrada in:
                values) {
            if(in.getTipo() == 1){
                saida += in.getValor();
            }
        }

        return saida;

    }

    static public float getSaidaDia(int dia, int mes, int ano){
        // 0 -> Entrada e 1 -> Saída
        float saida = 0;
        List<Entrada> values = Entrada.getEntradasDia(dia, mes, ano);

        for (Entrada in:
                values) {
            if(in.getTipo() == 1){
                saida += in.getValor();
            }
        }

        return saida;

    }



    static public float getSaldoOrcamentoTotal(int mes, int ano){

        float saldo1 = 0;
        List<Entry> valores = Entry.getOrcamento(mes, ano);

        for (Entry x : valores){
            saldo1 = saldo1 + x.getTotal();
        }

        return saldo1;
    }


    static public float getSaldoOrcamento(int mes, int ano){

        float orcamento = 0;
        List<Entry> valOrc = Entry.getOrcamento(mes, ano);

        for(Entry y : valOrc){
            orcamento += y.getOrcamento();
        }
        return orcamento;
    }


    static public float getSaldoBonus(int mes, int ano){

        float bonus = 0;
        List<Entry> valBon = Entry.getOrcamento(mes, ano);

        for(Entry y : valBon){
            bonus += y.getBonus();
        }
        return bonus;
    }

    static public float getGastosCasa(int mes, int ano){
        float Scasa=0;
        List<Entrada> Lcasa = Entrada.getEntradasMesAno(mes, ano);

        for(Entrada y : Lcasa){
            if(y.getTipo()==1) {
                if (y.getCategoria() == 1) {
                    Scasa += y.getValor();
                }
            }
        }
        return Scasa;
    }

    static public float getGastosAlimenticios(int mes, int ano){
        float Salimentos=0;
        List<Entrada> Lalimenticios = Entrada.getEntradasMesAno(mes, ano);

        for(Entrada y : Lalimenticios){
            if(y.getTipo()==1) {
                if (y.getCategoria() == 2)
                    Salimentos += y.getValor();
            }
        }
        return Salimentos;
    }

    static public float getGastosEntrentenimento(int mes, int ano){
        float Sent=0;
        List<Entrada> Lent = Entrada.getEntradasMesAno(mes, ano);

        for(Entrada y : Lent){
            if(y.getTipo()==1) {
                if (y.getCategoria() == 3)
                    Sent += y.getValor();
            }
        }
        return Sent;
    }

    static public float getOutrosGastos(int mes, int ano){
        float Soutros=0;
        List<Entrada> Loutros = Entrada.getEntradasMesAno(mes, ano);

        for(Entrada y : Loutros){
            if(y.getTipo()==1) {
                if (y.getCategoria() == 5)
                    Soutros += y.getValor();
            }
        }
        return Soutros;
    }

    static public float getGastosTransporte(int mes, int ano){
        float Stransporte=0;
        List<Entrada> Ltransporte = Entrada.getEntradasMesAno(mes, ano);

        for(Entrada y : Ltransporte){
            if(y.getTipo()==1) {
                if (y.getCategoria() == 4)
                    Stransporte += y.getValor();
            }
        }
        return Stransporte;
    }



    }
