package br.edu.ufam.ceteli.mywallet.Classes.OCR;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AiltonFH on 03/09/2015.
 */
public class OCRResposta {
    String resposta;
    String[] vetorResposta;


    public OCRResposta(String res){
        resposta = res;
        vetorResposta = resposta.split("\n");

        Log.d("OCR", res);
    }

    public String getEmpresa(){
        String aux= "";
        for (String aux2: vetorResposta) {
            if (aux2.toLowerCase().contains("ltda")) {
                aux = aux2;
                return aux;
            }
        }
        return aux;

    }

    public String getTotal(){
        String aux= "";
        for (String aux2: vetorResposta ) {
            if(aux2.toLowerCase().contains("rs")) {
                aux = aux2.split("RS")[1];
                aux = processaValor(aux);

            }
        }


        return aux;
    }

    public String processaValor(String valorS){
        String padrao = ("[0-9] [0-9]");
        Pattern p = Pattern.compile(padrao);
        String teste2 = "";

        Matcher m = p.matcher(valorS);
        if(m.find()){

            StringBuilder s = new StringBuilder(valorS);
            s.setCharAt(m.start()+1, '.');
            teste2 = s.toString();

            teste2 = teste2.replaceAll(" ", "");
            //System.out.println(teste2);
        }

        return teste2;

    }

}
