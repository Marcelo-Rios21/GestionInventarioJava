package inventario.core;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import inventario.dto.ResumenInventario;
import inventario.excepciones.ValidacionException;
import inventario.modelo.Producto;

class InventarioTest {
    private Inventario inv;

    @BeforeEach
    void setUp() {
        inv = new Inventario();
    }

    private static Producto prod(String c, String n, String d, String precio, int stock) {
        return new Producto(c, n, d, new BigDecimal(precio), stock);
    }

    @Test
    void agregarProducto_ok_y_duplicado_lanza() {
        Producto p = prod("A1", "Teclado", "mecánico", "19990.00", 10);
        inv.agregarProducto(p);
        assertTrue(inv.contiene("A1"));
        assertEquals(1, inv.size());

        // Duplicado
        assertThrows(ValidacionException.class, () -> inv.agregarProducto(prod("A1", "Otro", "", "1000", 1)));
    }

    @Test
    void agregarProducto_null_lanza() {
        assertThrows(ValidacionException.class, () -> inv.agregarProducto(null));
    }

    @Test
    void eliminarProducto_existente_true_inexistente_false() {
        inv.agregarProducto(prod("B1", "Mouse", "", "5000", 3));
        assertTrue(inv.eliminarProducto("B1"));   // existía
        assertFalse(inv.eliminarProducto("B1"));  // ya no existe
    }

    @Test
    void eliminarProducto_codigoInvalido_lanza() {
        assertThrows(ValidacionException.class, () -> inv.eliminarProducto(" "));
    }

    @Test
    void buscarPorId_present_y_empty_en_blanco() {
        inv.agregarProducto(prod("C1", "Monitor", "", "120000", 2));
        assertTrue(inv.buscarPorId("C1").isPresent());
        assertTrue(inv.buscarPorId(" ").isEmpty());
    }

    @Test
    void buscarPorNombre_parcial_caseInsensitive() {
        inv.agregarProducto(prod("D1", "Camisa", "azul", "10000", 5));
        inv.agregarProducto(prod("D2", "Camiseta", "roja", "8000", 7));
        inv.agregarProducto(prod("D3", "Pantalón", "negro", "15000", 2));

        List<Producto> r = inv.buscarPorNombre("Cami");
        assertEquals(2, r.size());
        assertTrue(r.stream().allMatch(p -> p.getNombre().toLowerCase().contains("cami".toLowerCase())));
    }

    @Test
    void buscarPorTexto_en_nombre_o_descripcion() {
        inv.agregarProducto(prod("E1", "Zapatillas", "running", "30000", 4));
        inv.agregarProducto(prod("E2", "Polera", "para running", "12000", 6));
        inv.agregarProducto(prod("E3", "Calcetines", "algodón", "3000", 10));

        List<Producto> r = inv.buscarPorTexto("running");
        assertEquals(2, r.size());
    }

    @Test
    void listarTodos_ordenPorCodigo() {
        inv.agregarProducto(prod("Z9", "Ultimo", "", "1", 1));
        inv.agregarProducto(prod("A0", "Primero", "", "1", 1));
        List<Producto> todos = inv.listarTodos();
        assertEquals(List.of("A0", "Z9"),
                todos.stream().map(Producto::getCodigo).toList());
    }

    @Test
    void listarOrdenado_porNombre() {
        inv.agregarProducto(prod("1", "B", "", "1", 1));
        inv.agregarProducto(prod("2", "A", "", "1", 1));
        var orden = inv.listarOrdenado(Comparator.comparing(Producto::getNombre));
        assertEquals(List.of("A", "B"), orden.stream().map(Producto::getNombre).toList());
    }

    @Test
    void generarResumen_metricasCorrectas() {
        inv.agregarProducto(prod("R1", "Prod1", "", "1000", 2));  // valor 2000
        inv.agregarProducto(prod("R2", "Prod2", "", "500", 10));  // valor 5000
        inv.agregarProducto(prod("R3", "Prod3", "", "200", 1));   // valor 200

        ResumenInventario r = inv.generarResumen(2, 2);

        assertEquals(3, r.getCantidadProductos());
        assertEquals(13, r.getTotalItems());
        assertEquals(0, new BigDecimal("7200.00").compareTo(r.getValorTotal())); // 2000+5000+200

        // TopN por stock (desc): R2(10), R1(2)
        assertEquals(List.of("R2", "R1"), r.getTopPorStock().stream().map(Producto::getCodigo).toList());

        // Stock bajo (< 2): sólo R3(1)
        assertEquals(List.of("R3"), r.getStockBajo().stream().map(Producto::getCodigo).toList());
    }
}
