/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exap2;

/**
 *
 * @author oracle
 */
import java.io.Serializable;


public class Platos implements Serializable{
   private String codigop;
   private String nomep;
   private int grasa;
public Platos()
	{
		
	}

        public Platos(String codigo, String nome,int grasa)
	{
		this.codigop = codigo;
		this.nomep = nome;
                this.grasa= grasa;
	}

    public String getCodigop() {
        return codigop;
    }

    public void setCodigop(String codigop) {
        this.codigop = codigop;
    }

    public String getNomep() {
        return nomep;
    }

    public void setNomep(String nomep) {
        this.nomep = nomep;
    }

    public int getGrasa() {
        return grasa;
    }

    public void setGrasa(int grasa) {
        this.grasa = grasa;
    }

    @Override
    public String toString() {
        return"\tcodigop: " + codigop + "\n \t nomep=" + nomep + "\n\t grasa total:\t" + grasa;
    }

	
 
   
}