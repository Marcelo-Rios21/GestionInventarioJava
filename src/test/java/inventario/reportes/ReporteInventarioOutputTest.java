package inventario.reportes;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import inventario.dto.ResumenInventario;
import inventario.modelo.Producto;

class ReporteInventarioOutputTest {

    @Test
    void imprimirResumen_emite_lineas_clave() {
        // Arrange: datos conocidos
        Producto p1 = new Producto("R1", "Prod1", "", new BigDecimal("1000"), 2);
        Producto p2 = new Producto("R2", "Prod2", "", new BigDecimal("500"), 10);
        Producto p3 = new Producto("R3", "Prod3", "", new BigDecimal("200"), 1);

        ResumenInventario r = new ResumenInventario(
                3, 13, new BigDecimal("7200.00"),
                List.of(p2, p1), // top por stock
                List.of(p3)  // stock bajo
        );

        // Capturar System.out
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(bos));
        try {
            ReporteInventario.imprimirResumen(r);
        } finally {
            System.setOut(old);
        }

        String out = bos.toString(StandardCharsets.UTF_8);
        // Asserts robustos (no dependen de formato exacto de moneda)
        assertTrue(out.contains("RESUMEN DE INVENTARIO"), "Debe imprimir encabezado");
        assertTrue(out.contains("Productos distintos: 3"), "Debe imprimir cantidad de productos");
        assertTrue(out.contains("Total de items: 13"), "Debe imprimir total de items");
        assertTrue(out.contains("Top por stock:"), "Debe imprimir sección de top");
        assertTrue(out.contains("Stock bajo umbral:"), "Debe imprimir sección de stock bajo");
        assertTrue(out.contains("R2"), "Debe listar R2 en top");
        assertTrue(out.contains("R1"), "Debe listar R1 en top");
        assertTrue(out.contains("R3"), "Debe listar R3 en stock bajo");
    }
}