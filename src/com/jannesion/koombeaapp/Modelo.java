package com.jannesion.koombeaapp;


public class Modelo {
	private String foto;
	private String nombre;
	private String idObjecto;
	private String fechaDeNacimiento;
	private long edad;

	public Modelo(String foto, String nombre, String fecha) {
		super();
		this.setFoto(foto);
		this.setNombre(nombre);
		this.setFechaDeNacimiento(fecha);
	}

	public Modelo(String foto, String nombre, String fecha, long edad,String IdObjecto) {
		super();
		this.setFoto(foto);
		this.setNombre(nombre);
		this.setFechaDeNacimiento(fecha);
		this.setEdad(edad);
		this.setIdObjecto(IdObjecto);
	}
	
	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getIdObjecto() {
		return idObjecto;
	}
	
	public void setIdObjecto(String value) {
		this.idObjecto = value;
	}

	public String getFechaDeNacimiento() {
		return fechaDeNacimiento;
	}

	public void setFechaDeNacimiento(String fechaDeNacimiento) {
		this.fechaDeNacimiento = fechaDeNacimiento;
	}
	
	
	public long getEdad() {
		return edad;
	}

	public void setEdad(long edad) {
		this.edad = edad;
	}
}
