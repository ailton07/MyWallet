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
    private String categoria;

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
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
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


        return getDataCompra()+" "+ descricao  + " " + valor;
    }
}
