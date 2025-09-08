package inventario.modelo;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import inventario.excepciones.ValidacionException;

class ProductoTest {
    @Test
    void crearProducto_basico_ok() {
        Producto p = new Producto("P1", "Teclado", new BigDecimal("19990.50"));
        assertEquals("P1", p.getCodigo());
        assertEquals("Teclado", p.getNombre());
        assertEquals(new BigDecimal("19990.50"), p.getPrecio());
        assertEquals(0, p.getStock());
        assertEquals("", p.getDescripcion());
    }

    @Test
    void crearProducto_completo_ok() {
        Producto p = new Producto("P2", "Mouse", "Gamer", new BigDecimal("15000"), 7);
        assertEquals("P2", p.getCodigo());
        assertEquals("Mouse", p.getNombre());
        assertEquals("Gamer", p.getDescripcion());
        assertEquals(new BigDecimal("15000.00"), p.getPrecio());
        assertEquals(7, p.getStock());
    }

    @Test
    void crearProducto_datosInvalidos_lanza() {
        assertThrows(ValidacionException.class, () ->
                new Producto("", "X", new BigDecimal("1.00")));
        assertThrows(ValidacionException.class, () ->
                new Producto("P3", "", new BigDecimal("1.00")));
        assertThrows(ValidacionException.class, () ->
                new Producto("P4", "X", new BigDecimal("-1")));
        assertThrows(ValidacionException.class, () ->
                new Producto("P5", "X", "desc", new BigDecimal("1.00"), -5));
    }

    @Test
    void actualizarPrecio_ok_y_redondeo() {
        Producto p = new Producto("P6", "Prod", new BigDecimal("10"));
        p.actualizarPrecio(new BigDecimal("12.345"));
        assertEquals(new BigDecimal("12.35"), p.getPrecio()); // HALF_UP
    }

    @Test
    void actualizarPrecio_invalido_lanza() {
        Producto p = new Producto("P7", "Prod", new BigDecimal("10"));
        assertThrows(ValidacionException.class, () -> p.actualizarPrecio(new BigDecimal("-1")));
        assertThrows(ValidacionException.class, () -> p.actualizarPrecio(null));
    }

    @Test
    void stock_set_aumentar_disminuir_ok() {
        Producto p = new Producto("P8", "Prod", new BigDecimal("1.00"));
        p.setStock(5);
        assertEquals(5, p.getStock());

        p.aumentarStock(3);
        assertEquals(8, p.getStock());

        p.disminuirStock(2);
        assertEquals(6, p.getStock());
    }

    @Test
    void stock_operaciones_invalidas_lanza() {
        Producto p = new Producto("P9", "Prod", new BigDecimal("1.00"));
        assertThrows(ValidacionException.class, () -> p.setStock(-1));
        assertThrows(ValidacionException.class, () -> p.aumentarStock(0));
        assertThrows(ValidacionException.class, () -> p.disminuirStock(0));
        assertThrows(ValidacionException.class, () -> p.disminuirStock(1)); // dejaría negativo
    }

    @Test
    void equals_hashCode_porCodigo() {
        Producto a = new Producto("PX", "A", new BigDecimal("1.00"));
        Producto b = new Producto("PX", "B", new BigDecimal("2.00"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
