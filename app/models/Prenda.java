package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import play.db.jpa.Blob;
import play.db.jpa.Model;

@Entity
public class Prenda extends Model {

	public String tipo;
	public String equipo;
	public String talla;
	public int cantidadComprada;
	public int cantidadStock;
	public double precio;
	public Blob imagen;
	
	public Prenda(String tipo, String equipo, String talla, int cantidadStock, double precio) {
		super();
		this.tipo = tipo;
		this.equipo = equipo;
		this.talla = talla;
		this.cantidadComprada=cantidadComprada;
		this.cantidadStock=cantidadStock;
		this.precio=precio;
	
		
	}
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getEquipo() {
		return equipo;
	}

	public void setEquipo(String equipo) {
		this.equipo = equipo;
	}

	public String getTalla() {
		return talla;
	}

	public void setTalla(String talla) {
		this.talla = talla;
		//this.save();
	}

	public int getCantidadComprada() {
		return cantidadComprada;
	}

	public void setCantidadComprada(int cantidadComprada) {
		this.cantidadComprada = cantidadComprada;
		//this.save();
	}
	
	public int getCantidadStock() {
		return cantidadStock;
	
	}

	public void setCantidadStock(int cantidadStock) {
		this.cantidadStock = cantidadStock;
		//this.save();
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
		//this.save();
	}

	


}
