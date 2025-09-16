package inventario.perf;

import java.math.BigDecimal;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import inventario.core.Inventario;
import inventario.modelo.Producto;

class InventarioPerformanceTest {

    private static Inventario seed(int n) {
        Inventario inv = new Inventario();
        Random rnd = new Random(7);
        for (int i = 0; i < n; i++) {
            String codigo = "P" + i;
            String nombre = "Prod_" + (i % 200); // repetidos a propósito
            String desc = (i % 37 == 0) ? "running item " + i : "desc_" + rnd.nextInt(10_000);
            inv.agregarProducto(new Producto(codigo, nombre, desc, new BigDecimal("1.00"), rnd.nextInt(100)));
        }
        return inv;
    }

    @Test
    @Timeout(2) // debe ejecutar en < 2s
    void buscarPorTexto_en_10000_items_es_razonable() {
        var inv = seed(10_000);
        var res = inv.buscarPorTexto("running");
        assertTrue(res.size() > 0, "Debe encontrar coincidencias 'running'");
    }

    @Test
    @Timeout(2) // debe ejecutar en < 2s
    void generarResumen_en_10000_items_es_razonable() {
        var inv = seed(10_000);
        var resumen = inv.generarResumen(5, 3);
        assertTrue(resumen.getTopPorStock().size() <= 5);
    }
}
