package br.edu.ufam.ceteli.mywallet.Classes.OCR;

/**
 * Created by AiltonFH on 03/09/2015.
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
                aux = aux2.split("RS")[1];
                return aux2.split("RS")[1];
            }
            else if(aux2.toLowerCase().contains("pagamento")){
                aux = aux2.split("RS")[1];
                return aux2.split("RS")[1];
            }
            else if(aux2.contains("RS")){
                aux = aux2.split("RS")[1];
                return aux2.split("RS")[1];
            }

        }
        return aux;
    }

}
