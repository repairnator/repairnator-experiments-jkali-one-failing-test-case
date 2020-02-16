package ar.com.utn.dds.sge.creationals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import ar.com.utn.dds.sge.models.Administrador;
import ar.com.utn.dds.sge.models.Categoria;
import ar.com.utn.dds.sge.models.Cliente;
import ar.com.utn.dds.sge.models.Dispositivo;
import java.text.ParseException;




public class CasosDePruebaBuilder {
	
	public Cliente cliente1;
	public Cliente cliente2;
	public Cliente cliente3;
	public Cliente cliente4;
	public Administrador admin;
	public HashMap<String,Categoria>categorias;
	
public void crearDatosDePrueba() throws ParseException {
	// Fechas para cliente 1 y 2, y administrador
	Calendar fechaCliente1 = new GregorianCalendar();
	Calendar fechaCliente2 = new GregorianCalendar();
	Calendar fechaCliente3 = new GregorianCalendar();
	Calendar fechaCliente4 = new GregorianCalendar();
	
	Calendar fechaAdmin = new GregorianCalendar();

	fechaCliente1.setTime(new SimpleDateFormat("dd/MM/yyyy").parse("11/11/2011"));
	fechaCliente2.setTime(new SimpleDateFormat("dd/MM/yyyy").parse("15/08/2014"));
	fechaCliente3.setTime(new SimpleDateFormat("dd/MM/yyyy").parse("12/06/2014"));
	fechaCliente4.setTime(new SimpleDateFormat("dd/MM/yyyy").parse("09/04/2018"));
	
	fechaAdmin.setTime(new SimpleDateFormat("dd/MM/yyyy").parse("11/08/2010"));

	// Se crean todas las categorias
	categorias = new HashMap<String,Categoria>();
	
	categorias.put("R1", new Categoria("R1", 0.0f, 150.0f, 18.76f, 0.644f));
	categorias.put("R2", new Categoria("R2", 150.0f, 325.0f, 35.32f, 0.644f));
	categorias.put("R3", new Categoria("R3", 326.0f, 400.0f, 60.71f, 0.681f));
	categorias.put("R4", new Categoria("R4", 401.0f, 450.0f, 71.74f, 0.738f));
	categorias.put("R5", new Categoria("R5", 451.0f, 500.0f, 110.38f, 0.794f));
	categorias.put("R6", new Categoria("R6", 501.0f, 600.0f, 220.75f, 0.832f));
	categorias.put("R7", new Categoria("R7", 601.0f, 700.0f, 443.59f, 0.851f));
	categorias.put("R8", new Categoria("R8", 701.0f, 1400.0f, 545.96f, 0.851f));
	categorias.put("R9", new Categoria("R9", 1401.0f, 9999999.9f, 887.19f, 0.851f));

	// Se crea lista de dispositivos para el primer cliente
	List<Dispositivo> dispositivos = new ArrayList<Dispositivo>();
	List<Dispositivo> dispositivosVacios = new ArrayList<Dispositivo>();

	
	// Se agregan dispositivos del primer cliente
	dispositivos.add(new Dispositivo("Heladera", "Patrick", 106.00f, true));
	dispositivos.add(new Dispositivo("Televisor", "Lg", 10.00f, false));
	dispositivos.add(new Dispositivo("Home", "Sony", 7.00f, true));
	
	// Se crea primer cliente
	cliente1 = new Cliente("user1", "12345", "Juan", "Perez", "calle falsa 1234", fechaCliente1, "DNI", 34567890,
			123456, categorias.get("R1"), dispositivos, 106.00f);

	// Se crea nueva lista de dipositivos para el segundo cliente
	dispositivos = new ArrayList<Dispositivo>();

	// Se agrega dispositivo para segundo cliente
	dispositivos.add(new Dispositivo("PC", "Intel core i7", 160.00f, true));

	// Se crea segundo cliente
	cliente2 = new Cliente("user2", "987654", "Pedro", "Peposo", "Alberti 654", fechaCliente2, "DNI", 23765452,
			46354691, categorias.get("R2"), dispositivos, 430.00f);

	// Se crea nueva lista de dipositivos para el tercer cliente
	dispositivos = new ArrayList<Dispositivo>();

	// Se agrega dispositivo para tercer cliente
	dispositivos.add(new Dispositivo("Lavarropa", "Whirlpool", 90.00f, false));
	
	// Se crea tercer cliente
	cliente3 = new Cliente("user3", "qwerty", "Esteban", "Quito", "Medrano 950", fechaCliente3, "DNI", 23456789,
			43218765, categorias.get("R3"), dispositivos, 90.00f);
	
	//Se crea cuarto cliente
	cliente4 = new Cliente("user4", "0", "Maria", "Magdalena", " ", fechaCliente4, "DNI", 0,
			0, categorias.get("R4"), dispositivosVacios, 0.00f);

	
	// Creaci�n de administrador
	admin = new Administrador();
	admin.setId(1);
	admin.setUserName("user3");
	admin.setPassword("98gdf54");
	admin.setNombre("Carlos");
	admin.setApellido("Diaz");
	admin.setDomicilio("Moreto 2231");
	admin.setFechaAlta(fechaAdmin);

}
}

