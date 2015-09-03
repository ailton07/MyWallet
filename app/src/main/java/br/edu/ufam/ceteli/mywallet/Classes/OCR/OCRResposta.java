package br.edu.ufam.ceteli.mywallet.Classes.OCR;

/**
 * Created by AiltonFH on 02/09/2015.
 */
public class OCRResposta {
    String resposta;
    String[] vetorResposta;


    public OCRResposta(String res){
        resposta = res;
        vetorResposta = resposta.split("\n");
    }

    public String getEmpresa(){
        String aux= "";
        for (String aux2: vetorResposta) {
            if (aux2.toLowerCase().contains("ltda")) {
                aux = aux2;
                return aux2;
            }
        }
        return aux;

    }

    public String getTotal(){
        String aux= "";
        for (String aux2: vetorResposta ) {
            if(aux2.toLowerCase().contains("total")){
                aux = aux2;
                return aux2;
            }
            else if(aux2.toLowerCase().contains("r$")){
                aux = aux2;
                return aux2;
            }

        }
        return aux;
    }

}
