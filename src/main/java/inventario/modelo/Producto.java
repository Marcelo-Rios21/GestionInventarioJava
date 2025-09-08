package inventario.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import inventario.excepciones.ValidacionException;

public class Producto {
    private final String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int stock;

    public Producto(String codigo, String nombre, BigDecimal precio) {
        this(codigo, nombre, null, precio, 0);
    }

    public Producto(String codigo, String nombre, String descripcion, BigDecimal precio, int stock) {
        this.codigo = norm(codigo);
        this.nombre = norm(nombre);
        this.descripcion = descripcion == null ? "" : descripcion.trim();
        this.precio = normalizarPrecio(precio);
        this.stock = validarStock(stock);
        validarInvariantes();
    }

    private void validarInvariantes() {
        if (codigo.isEmpty()) {
            throw new ValidacionException("Codigo no puede estar vacio.");
        }
        if (nombre.isEmpty()) {
            throw new ValidacionException("Nombre no puede estar vacio.");
        }
        if (precio.signum() < 0) {
            throw new ValidacionException("Precio no puede ser negativo.");
        }
        if (stock < 0) {
            throw new ValidacionException("Stock no puede ser negativo.");
        }
    }

    private static String norm(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static BigDecimal normalizarPrecio(BigDecimal nuevoPrecio) {
        if (nuevoPrecio == null) throw new ValidacionException("El precio no puede ser null.");
        if (nuevoPrecio.signum() < 0) throw new ValidacionException("El precio no puede ser negativo.");
        return nuevoPrecio.setScale(2, RoundingMode.HALF_UP);
    }

    private static int validarStock(int s) {
        if (s < 0) throw new ValidacionException("El stock no puede ser negativo.");
        return s;
    }

    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        setPrecio(nuevoPrecio);
    }

    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ValidacionException("Cantidad a aumentar debe ser > 0.");
        }
        setStock(this.stock + cantidad);
    }

    public void disminuirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ValidacionException("Cantidad a disminuir debe ser > 0.");
        }
        if (this.stock - cantidad < 0) {
            throw new ValidacionException("No puede dejar el stock negativo.");
        }
        setStock(this.stock - cantidad);
    }

    public String descripcionDetallada() {
        return "Producto[codigo = " + codigo + ", nombre = " + nombre + ", precio = " + precio + ", stock = " + stock + ", desc = " + (descripcion == null ?
        "" : descripcion) +  "]";
    }

    //GETTERS
    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getStock() {
        return stock;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public String getCodigo() {
        return codigo;
    }

    //SETTERS
    public void setPrecio(BigDecimal nuevoPrecio) {
        if (nuevoPrecio == null) {
            throw new ValidacionException("El precio no puede ser null.");
        }
        if (nuevoPrecio.signum() < 0) {
            throw new ValidacionException("El precio no puede ser negativo.");
        }
        this.precio = nuevoPrecio.setScale(2, RoundingMode.HALF_UP);
    }

    public void setNombre(String nombre) {
        this.nombre = norm(nombre);
        if (this.nombre.isEmpty()) {
            throw new ValidacionException("El nombre no puede estar vacio.");
        }
    }

    public void setStock(int nuevoStock) {
        if (nuevoStock < 0) {
            throw new ValidacionException("El stock no puede ser negativo.");
        }
        this.stock = nuevoStock;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion.trim();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto)) return false;
        Producto p = (Producto) o;
        return Objects.equals(codigo, p.codigo);
    }

    @Override public int hashCode() { 
        return Objects.hash(codigo); 
    }
    
    @Override public String toString() {
        return codigo + " - " + nombre + " ($" + precio + ", stock=" + stock + ")";
    }
}
