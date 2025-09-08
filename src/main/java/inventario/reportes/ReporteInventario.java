package inventario.reportes;

import java.text.NumberFormat;
import java.util.Locale;

import inventario.dto.ResumenInventario;
import inventario.modelo.Producto;

public class ReporteInventario {

    private ReporteInventario() {

    }
    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance(Locale.of("es","CL"));

    public static void imprimirResumen(ResumenInventario r) {
        System.out.println("\n=== RESUMEN DE INVENTARIO ===");
        System.out.println("Productos distintos: " + r.getCantidadProductos());
        System.out.println("Total de items: " + r.getTotalItems());
        System.out.println("Valor total: " + MONEDA.format(r.getValorTotal()));

        System.out.println("\nTop por stock:");
        for (Producto p : r.getTopPorStock()) {
            System.out.println(" - " + p);
        }

        System.out.println("\nStock bajo umbral:");
        if (r.getStockBajo().isEmpty()) System.out.println(" - (ninguno)");
        for (Producto p : r.getStockBajo()) {
            System.out.println(" - " + p);
        }
        System.out.println("==============================\n");
    }
}
