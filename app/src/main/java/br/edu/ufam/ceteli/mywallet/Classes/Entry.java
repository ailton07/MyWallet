package br.edu.ufam.ceteli.mywallet.Classes;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;

@Table(name = "Entry")
public class Entry extends Model{

    @Column(name="Orcamento")
    private float orcamento;

    @Column(name="Bonus")
    private float bonus;

    public float getOrcamento() {
        return orcamento;
    }
    public void setOrcamento (float orcamento) {
        this.orcamento = orcamento;
    }
    public float getBonus() {
        return bonus;
    }
    public void setBonus(float bonus) {this.bonus = bonus;}

    public Entry() {
        super();
    }

    public String toString(){

        return "Orcamento: " + orcamento + "\n" + "Bonus: " + bonus;

    }

    public static ArrayList<Entry> getGeralOrcamento() {
        return new Select().from(Entry.class).execute();
    }

}