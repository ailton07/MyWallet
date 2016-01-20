package br.edu.ufam.ceteli.mywallet.classes;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Observer;

import static br.edu.ufam.ceteli.mywallet.classes.OperacoesData.getDataFormatada;

@Table(name = "Entry")
public class Entry extends Model {
    private ObserverUpdate observable = null;

    @Column(name = "Orcamento")
    private float orcamento;

    @Column(name = "Bonus")
    private float bonus;

    @Column(name = "Total")
    private float total;

    @Column(name="DataOrcamento")
    private int dataOrcamento;

    public float getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(float orcamento) {
        this.orcamento = orcamento;
    }

    public float getBonus() {
        return bonus;
    }

    public void setBonus(float bonus) {
        this.bonus = bonus;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getDataOrcamento() {

        return getDataFormatada(dataOrcamento);
        //return dataCompra;
    }
    public void setDataOrcamento(String dataOrcamento) {

        this.dataOrcamento = Integer.valueOf(dataOrcamento);
    }

    public Entry() {
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

    public String toString() {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        float orc = Float.parseFloat(numberFormat.format(orcamento));
        float bon = Float.parseFloat(numberFormat.format(bonus));
        float tot = Float.parseFloat(numberFormat.format(total));

        return "Orcamento: " + orc + "\n" + "Bonus: " + bon + "\n" + "Total: " + tot;

    }

    public static List<Entry> getGeralOrcamento() {
        return new Select().from(Entry.class).execute();
    }

    public static List<Entry> getOrcamento(int mes, int ano){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes - 1);

        return new Select().from(Entry.class).where("DataOrcamento >= ? and DataOrcamento <= ?", dateFormat.format(c.getTime()) + "01", dateFormat.format(c.getTime()) + Integer.toString(c.getActualMaximum(Calendar.DAY_OF_MONTH))).execute();
    }

    public static List<Entry> getOrcamentoDia(int dia, int mes, int ano){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        Log.e("DATE", dateFormat.format(Calendar.getInstance().getTime()));

        return new Select().from(Entry.class).where("DataOrcamento == ? ", dateFormat.format(Calendar.getInstance().getTime())).execute();
    }

    public static List<Entry> getOrcamentoSemana(int semana, int mes ,int ano){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes - 1);

        if(semana == 1){
            return new Select().from(Entry.class).where("DataOrcamento > ? and DataOrcamento <= ? ", dateFormat.format(c.getTime()) + "00", dateFormat.format(c.getTime()) + "07").execute();
        }else if(semana == 2){
            return new Select().from(Entry.class).where("DataOrcamento >= ? and DataOrcamento <= ? ", dateFormat.format(c.getTime()) + "08", dateFormat.format(c.getTime()) + "15").execute();
        }else if(semana == 3){
            return new Select().from(Entry.class).where("DataOrcamento >= ? and DataOrcamento <= ? ", dateFormat.format(c.getTime()) + "16", dateFormat.format(c.getTime()) + "23").execute();
        }else
            return new Select().from(Entry.class).where("DataOrcamento >= ? and DataOrcamento < ? ", dateFormat.format(c.getTime()) + "24", dateFormat.format(c.getTime()) + "32").execute();
    }
}
