package ar.com.utn.dds.sge.models;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Clase que modela a un usuario teniendo en cuenta los atributos que hereda de
 * la clase Usuario.
 * 
 * @author Grupo 2
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "tipo_doc", "nro_doc", "telefono", "categoria", "dispositivos"})
public class Cliente extends Usuario{

	@JsonProperty("tipo_doc")
	@NotNull(message = "El tipo de documento no puede estar vacio")
	private String tipo_doc;

	@JsonProperty("nro_doc")
	@NotNull(message = "El numero de documento no puede estar vacio")
	private Integer nro_doc;

	@JsonProperty("telefono")
	@NotNull(message = "El telefono no puede estar vacio")
	private Integer telefono;

	@JsonProperty("categoria")
	@NotNull(message = "El cliente debe pertenecer a una categoria")
	private Categoria categoria;

	@JsonProperty("dispositivosInteligentes")
	private List<Dispositivo> dispositivosInteligentes;

	public Cliente() {
		super();
	}
	
	public Cliente(String userName, String password, String nombre, String apellido, String domicilio,
			Calendar fechaAlta, String tipoDoc, Integer nroDoc, Integer telefono, Categoria categoria,
			List<Dispositivo> dispositivos, Float consumoMensual) {

		super(userName, password, nombre, apellido, domicilio, fechaAlta);
		this.tipo_doc = tipoDoc;
		this.nro_doc = nroDoc;
		this.telefono = telefono;
		this.categoria = categoria;
		this.dispositivosInteligentes = dispositivos;

	}
	
	
	/**
	 * Metodo que obtiene los dispositivos encendidos del cliente
	 * @return Dispositivos que estan encendidos
	 */
	public Stream<Dispositivo> dispositivosEncendidos(){
		Stream<Dispositivo> dispositivosEncendidos = dispositivosInteligentes.stream().filter(dispositivo->dispositivo.getEstado());
		return dispositivosEncendidos;
	}

	/**
	 * Metodo que indica si el cliente tiene algun dispositivo encendido.
	 * @return Indicador que indica si hay algun dispositivo encendido
	 */
	public Boolean hayDispositivosEncendidos() {
		return dispositivosInteligentes.stream().anyMatch(dispositivo->dispositivo.getEstado());
	}
	
	public Boolean hayDispositivosApagados() {
		return !this.hayDispositivosEncendidos();
	}

	/**
	 * Metodo que indica la cantidad de dispositivos que tiene un cliente
	 * @return Cantidad de dispositivos del cliente
	 */
	public int cantDispositivos() {
		return dispositivosInteligentes.size();
	}
	
	/**
	 * Metodo que indica la cantidad de dispositivos encendidos que tiene un cliente
	 * @return Cantidad de dispositivos encendidos que tiene el cliente
	 */
	public int cantDispositivosEncendidos() {
		return (int)this.dispositivosEncendidos().count();
	}

	/**
	 * Metodo que indica la cantidad de dispositivos apagados que tiene un cliente
	 * @return Cantidad de dispositivos apagados que tiene el cliente
	 */
	public int cantDispositivosApagados() {
		return this.cantDispositivos() - this.cantDispositivosEncendidos();
	}
	
	/**
	 * Metodo que calcula el consumo total en kWh de un cliente segun los dispositivos que tenga
	 * registrado en el sistma.
	 * @return Consumo total del cliente en kWh
	 */
	public Float calcularConsumo(){
		return this.sumaFlotante(this.dispositivosEncendidos().map(dispositivo->dispositivo.getConsumo()).collect(Collectors.toList()));
	}
	
	/**
	 * Metodo para calcular la sumatoria de numeros decimales de tipo Float.
	 * 
	 * @param lista Lista de numeros  de tipo Float
	 * @return Sumatoria de numeros de tipo Float
	 */
	private Float sumaFlotante(List<Float> lista){
		Float sum = 0.0f;
		for(Float elemento : lista){
			sum += elemento;
		}
		return sum;
	}
	
	/**
	 * Metodo que obtiene el importe total en pesos por el consumo total de los dispositivos del cliente.
	 * @return Importe total en pesos de lo consumido por el cliente
	 */
	public Float obtenerImporte() {
		return categoria.calcularImporte(this.calcularConsumo());
	}
	
	/**
	 * Metodo que agrega un dispositivo al cliente.
	 * @param disp Dispositivo a agregar
	 */
	public void agregarDispositivo(Dispositivo disp) {
		this.dispositivosInteligentes.add(disp);
	}
	
	
	/*
	 * Getters y Setters
	 */

	public String getTipoDoc() {
		return tipo_doc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipo_doc = tipoDoc;
	}

	public Integer getNroDoc() {
		return nro_doc;
	}

	public void setNroDoc(Integer nroDoc) {
		this.nro_doc = nroDoc;
	}

	public Integer getTelefono() {
		return telefono;
	}

	public void setTelefono(Integer telefono) {
		this.telefono = telefono;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public List<Dispositivo> getDispositivos() {
		return dispositivosInteligentes;
	}

	public void setDispositivos(List<Dispositivo> dispositivos) {
		this.dispositivosInteligentes = dispositivos;
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((categoria == null) ? 0 : categoria.hashCode());
		result = prime * result + ((dispositivosInteligentes == null) ? 0 : dispositivosInteligentes.hashCode());
		result = prime * result + ((nro_doc == null) ? 0 : nro_doc.hashCode());
		result = prime * result + ((telefono == null) ? 0 : telefono.hashCode());
		result = prime * result + ((tipo_doc == null) ? 0 : tipo_doc.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		if (categoria == null) {
			if (other.categoria != null)
				return false;
		} else if (!categoria.equals(other.categoria))
			return false;
		if (dispositivosInteligentes == null) {
			if (other.dispositivosInteligentes != null)
				return false;
		} else if (!dispositivosInteligentes.equals(other.dispositivosInteligentes))
			return false;
		if (nro_doc == null) {
			if (other.nro_doc != null)
				return false;
		} else if (!nro_doc.equals(other.nro_doc))
			return false;
		if (telefono == null) {
			if (other.telefono != null)
				return false;
		} else if (!telefono.equals(other.telefono))
			return false;
		if (tipo_doc == null) {
			if (other.tipo_doc != null)
				return false;
		} else if (!tipo_doc.equals(other.tipo_doc))
			return false;
		return true;
	}

	
}