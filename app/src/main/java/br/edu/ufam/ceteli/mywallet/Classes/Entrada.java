package br.edu.ufam.ceteli.mywallet.Classes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Entrada")
public class Entrada extends Model {
    @Column(name="Tipo")
    private int tipo;

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
    private Date dataInsercao;
    //private String dataInsercao;

    @Column(name="DataCompra")
    private String dataCompra;

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
    public Date getDataInsercao() {
        return dataInsercao;
    }

    public String getDataInsercaoFormatada() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //FIX

//        return (String.valueOf(dateFormat.format(dataInsercao)));
        return null;
    }

    public void setDataInsercao(Date dataInsercao) {
        this.dataInsercao = dataInsercao;
    }
    public String getDataCompra() {
        return dataCompra;
    }
    public void setDataCompra(String dataCompra) {
        this.dataCompra = dataCompra;
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

    public static ArrayList<Entrada> getComments() {
        return new Select().from(Entrada.class).execute();
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

    public static ArrayList<Entrada> getEntrada(){
        return new Select().from(Entrada.class).where("Tipo = 0").execute();
    }
    public static ArrayList<Entrada> getSaida(){
        return new Select().from(Entrada.class).where("Tipo = 1").execute();
    }
    public static ArrayList<Entrada> getCategoriaCasa(){
        return new Select().from(Entrada.class).where("Categoria = 1").execute();
    }
    public static ArrayList<Entrada> getCategoriaRestaurante(){
        return new Select().from(Entrada.class).where("Categoria = 2").execute();
    }
    public static ArrayList<Entrada> getCategoriaLazer(){
        return new Select().from(Entrada.class).where("Categoria = 3").execute();
    }
    public static ArrayList<Entrada> getCategoriaOcasional(){
        return new Select().from(Entrada.class).where("Categoria = 4").execute();
    }
}
