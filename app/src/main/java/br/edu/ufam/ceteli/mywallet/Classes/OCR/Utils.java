package br.edu.ufam.ceteli.mywallet.Classes.OCR;

import android.util.Log;

import java.util.List;

import br.edu.ufam.ceteli.mywallet.Classes.Entrada;
import br.edu.ufam.ceteli.mywallet.Classes.Entry;

/**
 * Created by AiltonFH on 03/10/2015.
 */
public class Utils {


    static public float getSaldoMes(int mes, int ano){
        // 0 -> Entrada e 1 -> Saída
        float saldo = 0;
        List<Entrada> values = Entrada.getEntradasMesAno(mes, ano);

        for (Entrada in:
                values) {
            if(in.getTipo() == 0){
                saldo += in.getValor();
            }
            else{
                saldo = saldo - in.getValor();
            }

        }

        return saldo;

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


    static public float getSaldoTotal(int mes, int ano){

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
        Log.i("App123", "orcamento0: " + String.valueOf(orcamento));

        for(Entry y : valOrc){
            Log.i("App123", "orcamento1: " + String.valueOf(orcamento));
            orcamento += y.getOrcamento();
            Log.i("App123", "orcamento1: " + String.valueOf(orcamento));
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

}
