package br.edu.ufam.ceteli.mywallet.Classes.OCR;

import java.util.List;

import br.edu.ufam.ceteli.mywallet.Classes.Entrada;

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

}
