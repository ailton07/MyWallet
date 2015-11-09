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

    public static List<Entry> getOrcamentoDia(int dia, int mes, int ano){
        String diaS = String.valueOf(dia);
        String mesS = String.valueOf(mes);
        String anoS = String.valueOf(ano);

        return new Select().from(Entry.class).where("DataOrcamento == ? ", (anoS + mesS + diaS) ).execute();
    }

    public static List<Entry> getOrcamentoSemana(int semana, int mes ,int ano){
        //String semanaS = String.valueOf(semana);
        String mesS = String.valueOf(mes);
        String anoS = String.valueOf(ano);

        if(semana == 1){
            return new Select().from(Entry.class).where("DataOrcamento > ? and DataOrcamento < ? ", (anoS + mesS + "00"), (anoS + mesS + "07")).execute();
        }else if(semana == 2){
            return new Select().from(Entry.class).where("DataOrcamento > ? and DataOrcamento < ? ", (anoS + mesS + "08"), (anoS + mesS + "15")).execute();
        }else if(semana == 3){
            return new Select().from(Entry.class).where("DataOrcamento > ? and DataOrcamento < ? ", (anoS + mesS + "16"), (anoS + mesS + "23")).execute();
        }else
            return new Select().from(Entry.class).where("DataOrcamento > ? and DataOrcamento < ? ", (anoS + mesS + "24"), (anoS + mesS + "32")).execute();
    }
}
