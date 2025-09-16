package inventario.core;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import inventario.dto.ResumenInventario;
import inventario.modelo.Producto;

class ConfiabilidadSmokeTest {

    @Test
    void flujo_completo_basico_no_lanza_excepcion() {
        assertDoesNotThrow(() -> {
            Inventario inv = new Inventario();
            inv.agregarProducto(new Producto("A1", "Teclado", "", new BigDecimal("19990.00"), 10));
            inv.agregarProducto(new Producto("B1", "Mouse", "", new BigDecimal("5000.00"), 3));

            inv.buscarPorId("A1");
            inv.buscarPorNombre("Tecl");
            inv.buscarPorTexto("Mouse");
            inv.listarTodos();
            inv.listarOrdenado(java.util.Comparator.comparing(Producto::getNombre));

            ResumenInventario r = inv.generarResumen(3, 2);
            // Una pasada por los datos del resumen
            r.getTopPorStock();
            r.getStockBajo();

            // Eliminar producto que no existe NO debe lanzar: retorna false
            boolean eliminado = inv.eliminarProducto("ZZ");
            assertFalse(eliminado);
        });
    }
}