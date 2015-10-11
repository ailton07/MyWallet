package br.edu.ufam.ceteli.mywallet.classes;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

import static br.edu.ufam.ceteli.mywallet.classes.OperacoesData.getDataFormatada;

@Table(name = "Entry")
public class Entry extends Model {

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
    }

    public String toString() {

        return "Orcamento: " + orcamento + "\n" + "Bonus: " + bonus + "\n" + "Total: " + total;

    }

    public static List<Entry> getGeralOrcamento() {
        return new Select().from(Entry.class).execute();
    }

    public static List<Entry> getOrcamento(int mes, int ano){
        String mes1 = String.valueOf(mes);
        String ano1 = String.valueOf(ano);

        return new Select().from(Entry.class).where("DataOrcamento > ? and DataOrcamento < ?", (ano1 + mes1 + "00"), (ano1 + mes1 + "32")).execute();
    }
}
