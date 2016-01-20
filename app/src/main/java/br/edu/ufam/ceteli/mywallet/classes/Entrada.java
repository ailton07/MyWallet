package br.edu.ufam.ceteli.mywallet.classes;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Observer;

import static br.edu.ufam.ceteli.mywallet.classes.OperacoesData.getDataFormatada;

//Data

@Table(name = "Entrada")
public class Entrada extends Model {
    private ObserverUpdate observable = null;


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
        observable = new ObserverUpdate();
    }

    public void addObserverClass(Observer observer){
        observable.addObserver(observer);
    }

    public void delObserverClass(Observer observer){
        observable.deleteObserver(observer);
    }

    public void salvar(){
        this.save();
        observable.notifyObservers();
    }


    @Override
    public String toString() {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String comb1 = "";
        float val = Float.parseFloat(numberFormat.format(valor));

        if(categoria == 1){
            comb1 = "Residencia";
        }else if(categoria == 2){
            comb1 = "Alimenticios";
        }else if(categoria == 3){
            comb1 = "Entretenimento";
        }else if(categoria == 4){
            comb1 = "Transporte";
        }else if(categoria == 5){
            comb1 = "Saude";
        }else {
            comb1 = "Ocasional";
        }

        return getDataCompra()+  "       " +  comb1  + " \n "+ "Estebelecimento: "+ estabelecimento  + "\n" + "Valor: " + val
                + "\n" +  "Descricao: " + descricao;
    }

    public String toString1(){
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String comb1 = "";
        float val = Float.parseFloat(numberFormat.format(valor));

        if(categoria == 1){
            comb1 = "Residencia";
        }else if(categoria == 2){
            comb1 = "Alimenticios";
        }else if(categoria == 3){
            comb1 = "Entretenimento";
        }else if(categoria == 4){
            comb1 = "Transporte";
        }else if(categoria == 5){
            comb1 = "Saude";
        }else {
            comb1 = "Ocasional";
        }

        return getDataCompra()+  "       " + " \n "+ "Estebelecimento: "+ estabelecimento  + "\n" + "Valor: " + val
                + "\n" +  "Descricao: " + descricao;
    }

    public static List<Entrada> getComments() {
        return new Select().from(Entrada.class).orderBy("DataCompra DESC").execute();
    }
    public static List<Entrada> getEntrada(){
        return new Select().from(Entrada.class).where("Tipo = 0").orderBy("DataCompra DESC").execute();
    }
    public static List<Entrada> getSaida(){
        return new Select().from(Entrada.class).where("Tipo = 1").orderBy("DataCompra DESC").execute();
    }
    public static List<Entrada> getCategoriaCasa(){
        return new Select().from(Entrada.class).where("Categoria = 1").orderBy("DataCompra DESC").execute();
    }
    public static List<Entrada> getCategoriaRestaurante(){
        return new Select().from(Entrada.class).where("Categoria = 2").orderBy("DataCompra DESC").execute();
    }
    public static List<Entrada> getCategoriaLazer(){
        return new Select().from(Entrada.class).where("Categoria = 3").orderBy("DataCompra DESC").execute();
    }
    public static List<Entrada> getCategoriaTransporte(){
        return new Select().from(Entrada.class).where("Categoria = 4").orderBy("DataCompra DESC").execute();
    }
    public static List<Entrada> getCategoriaSaude(){
        return new Select().from(Entrada.class).where("Categoria = 5").orderBy("DataCompra DESC").execute();
    }
    public static List<Entrada> getCategoriaOcasional(){
        return new Select().from(Entrada.class).where("Categoria = 6").orderBy("DataCompra DESC").execute();
    }


    public static List<Entrada> delComments(int pos){
        return new Delete().from(Entrada.class).where("Id = ?", pos).execute();
    }

    // Pega todas as entradas de determinado mês e de determinado ano.
    // DataCompra => YYYYmmDD
    public static List<Entrada> getEntradasMesAno(int mes, int ano){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes - 1);
        return new Select().from(Entrada.class).where("DataCompra >= ? and DataCompra <= ? ", dateFormat.format(c.getTime()) + "01", dateFormat.format(c.getTime()) + Integer.toString(c.getActualMaximum(Calendar.DAY_OF_MONTH))).execute();
    }

    public static List<Entrada> getEntradasDia(int dia, int mes, int ano){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        Log.e("DATE", dateFormat.format(Calendar.getInstance().getTime()));

        return new Select().from(Entrada.class).where("DataCompra == ? ",  dateFormat.format(Calendar.getInstance().getTime())).execute();
    }

    public static List<Entrada> getEntradasSemana(int semana, int mes ,int ano){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes - 1);

        if(semana == 1){
        return new Select().from(Entrada.class).where("DataCompra > ? and DataCompra <= ? ", dateFormat.format(c.getTime()) + "00", dateFormat.format(c.getTime()) + "07").execute();
        }else if(semana == 2){
            return new Select().from(Entrada.class).where("DataCompra >= ? and DataCompra <= ? ", dateFormat.format(c.getTime()) + "08", dateFormat.format(c.getTime()) + "15").execute();
        }else if(semana == 3){
            return new Select().from(Entrada.class).where("DataCompra >= ? and DataCompra <= ? ", dateFormat.format(c.getTime()) + "16", dateFormat.format(c.getTime()) + "23").execute();
        }else
            return new Select().from(Entrada.class).where("DataCompra >= ? and DataCompra < ? ", dateFormat.format(c.getTime()) + "24", dateFormat.format(c.getTime()) + "32").execute();
        }
}
