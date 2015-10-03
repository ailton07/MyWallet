package br.edu.ufam.ceteli.mywallet.Classes;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

//Data
import static br.edu.ufam.ceteli.mywallet.Classes.OperacoesData.getDataFormatada;

@Table(name = "Entrada")
public class Entrada extends Model {
    @Column(name="Tipo")
    private int tipo; // 0 -> Entrada e 1 -> Saída

    @Column(name="Descricao")
    private String descricao;
    //private String produto;

    @Column(name="Valor")
    private float valor;

    @Column(name="Estabelecimento")
    private String estabelecimento;

    @Column(name="Categoria")
    private int categoria;

    @Column(name="DataInsercao")
    private String dataInsercao;
    //private Date dataInsercao;

    @Column(name="DataCompra")
    private int dataCompra;

    // Getter e Setter
    public int getTipo() {
        return tipo;
    }
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    public float getValor() {
        return valor;
    }
    public void setValor(float valor) {
        this.valor = valor;
    }

    public String getEstabelecimento() {
        return estabelecimento;
    }
    public void setEstabelecimento(String estabelecimento) {
        this.estabelecimento = estabelecimento;
    }
    public int getCategoria() {
        return categoria;
    }
    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }
    public String getDataInsercao() {
        return getDataFormatada(dataInsercao);
    }

    public String getDataInsercaoFormatada() {

        return getDataFormatada(dataInsercao);
    }

    public void setDataInsercao(String dataInsercao) {
        this.dataInsercao = (dataInsercao);
    }
    public String getDataCompra() {

        return getDataFormatada(dataCompra);
        //return dataCompra;
    }
    public void setDataCompra(String dataCompra) {

        this.dataCompra = Integer.valueOf(dataCompra);
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Entrada() {
        super();
    }

    // Will be used by the ArrayAdapter in the ListView

    @Override
    public String toString() {

        String comb1 = "";

        if(categoria == 1){
            comb1 = "Casa";
        }else if(categoria == 2){
            comb1 = "Restaurante";
        }else if(categoria == 3){
            comb1 = "Lazer";
        }else if(categoria == 4){
            comb1 = "Ocasional";
        }else{
            comb1 = "Mensal";
        }


        return getDataCompra()+  "       " +  comb1  + " \n "+ "Estebelecimento: "+ estabelecimento  + "\n" + "Valor: " + valor
                + "\n" +  "Descricao: " + descricao;
    }

    public static ArrayList<Entrada> getComments() {
        return new Select().from(Entrada.class).orderBy("DataCompra DESC").execute();
    }
    public static ArrayList<Entrada> getEntrada(){
        return new Select().from(Entrada.class).where("Tipo = 0").orderBy("DataCompra DESC").execute();
    }
    public static ArrayList<Entrada> getSaida(){
        return new Select().from(Entrada.class).where("Tipo = 1").orderBy("DataCompra DESC").execute();
    }
    public static ArrayList<Entrada> getCategoriaCasa(){
        return new Select().from(Entrada.class).where("Categoria = 1").orderBy("DataCompra DESC").execute();
    }
    public static ArrayList<Entrada> getCategoriaRestaurante(){
        return new Select().from(Entrada.class).where("Categoria = 2").orderBy("DataCompra DESC").execute();
    }
    public static ArrayList<Entrada> getCategoriaLazer(){
        return new Select().from(Entrada.class).where("Categoria = 3").orderBy("DataCompra DESC").execute();
    }
    public static ArrayList<Entrada> getCategoriaOcasional(){
        return new Select().from(Entrada.class).where("Categoria = 4").orderBy("DataCompra DESC").execute();
    }

    // Pega todas as entradas de determinado mês e de determinado ano.
    // DataCompra => YYYYmmDD
    public static ArrayList<Entrada> getEntradasMesAno(int mes, int ano){
        String mesS = String.valueOf(mes);
        String anoS = String.valueOf(ano);

        return new Select().from(Entrada.class).where("DataCompra > ? and DataCompra < ?", (anoS + mesS +"00"), (anoS+ mesS + "32") ).execute();
    }
}
