package br.edu.ufam.ceteli.mywallet.classes.ocr;

import android.util.Log;

import java.util.List;

import br.edu.ufam.ceteli.mywallet.classes.Entrada;
import br.edu.ufam.ceteli.mywallet.classes.Entry;

/**
 * Created by AiltonFH on 03/10/2015.
 */
public class Utils {


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
