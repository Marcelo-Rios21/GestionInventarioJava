package inventario.ui;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

import inventario.core.Inventario;
import inventario.dto.ResumenInventario;
import inventario.excepciones.ValidacionException;
import inventario.modelo.Producto;
import inventario.reportes.ReporteInventario;

public class MenuPrincipal {
    private static final Scanner input = new Scanner(System.in).useLocale(Locale.US);
    private static final Inventario inventario = new Inventario();

    public static void mostrar() {
        int op;

        do {
            imprimirMenu();
            op = leerEntero("Opción: ", 0, 7);
            try {
                switch (op) {
                    case 1 -> agregar();
                    case 2 -> actualizar();
                    case 3 -> eliminar();
                    case 4 -> buscarPorId();
                    case 5 -> buscarPorTexto();
                    case 6 -> listar();
                    case 7 -> informe();
                    case 0 -> System.out.println("Saliendo...");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (ValidacionException ex) {
                System.out.println("ERROR: " + ex.getMessage());
            } catch (Exception e) {
                System.out.println("Error Inesperado: " + e.getMessage());
            }
        } while (op != 0);
    }

    private static void imprimirMenu() {
        System.out.println("--- MENÚ INVENTARIO ---");
        System.out.println("1) Agregar producto.");
        System.out.println("2) Actualizar producto.");
        System.out.println("3) Eliminar producto.");
        System.out.println("4) Buscar por ID.");
        System.out.println("5) Buscar por nombre/descripcion.");
        System.out.println("6) Listar todos.");
        System.out.println("7) Informe (resumen).");
        System.out.println("0) Salir.");
    }

    private static void agregar() {
        System.out.println("\n--- Agregar producto ---");
        String codigo = leerTexto("Código: ", true);
        String nombre = leerTexto("Nombre: ", true);
        String descripcion = leerTexto("Descripcion (opcional): ", false);
        BigDecimal precio = leerMoneda("Precio: ");
        int stock = leerEntero("Stock: ", 0, Integer.MAX_VALUE);

        Producto p = new Producto(codigo, nombre, descripcion, precio, stock);
        inventario.agregarProducto(p);
        System.out.println("Producto agregado.\n");
    }

    private static void actualizar() {
        System.out.println("\n--- Actualizar producto ---");
        String codigo = leerTexto("Codigo a actualizar: ", true);
        Optional<Producto> opt = inventario.buscarPorId(codigo);
        if (opt.isEmpty()) { System.out.println("No existe ese producto.\n"); return; }
        Producto p = opt.get();

        System.out.println("Actualizando: " + p);
        System.out.println("1) Nombre  2) Descripción  3) Precio  4) Stock");
        int campo = leerEntero("Seleccione campo: ", 1, 4);

        inventario.actualizarProducto(codigo, prod -> {
            switch (campo) {
                case 1 -> prod.setNombre(leerTexto("Nuevo nombre: ", true));
                case 2 -> prod.setDescripcion(leerTexto("Nueva descripcion: ", false));
                case 3 -> prod.actualizarPrecio(leerMoneda("Nuevo precio: "));
                case 4 -> prod.setStock(leerEntero("Nuevo stock: ", 0, Integer.MAX_VALUE));
                default -> throw new ValidacionException("Campo invalido");
            }
        });
        System.out.println("Producto actualizado.\n");
    }

    private static void eliminar() {
        System.out.println("\n--- Eliminar producto ---");
        String codigo = leerTexto("Codigo a eliminar: ", true);
        boolean ok = inventario.eliminarProducto(codigo);
        System.out.println(ok ? "Eliminado.\n" : "No existia ese codigo.\n");
    }

    private static void buscarPorId() {
        System.out.println("\n--- Buscar por ID ---");
        String codigo = leerTexto("Codigo: ", true);
        inventario.buscarPorId(codigo).ifPresentOrElse(p -> System.out.println(p.descripcionDetallada()),
            () -> System.out.println("No encontrado."));
        System.out.println();
    }

    private static void buscarPorTexto() {
        System.out.println("\n--- Buscar por nombre/descripcion ---");
        String q = leerTexto("Texto: ", true);
        List<Producto> res = inventario.buscarPorTexto(q);
        if (res.isEmpty()) System.out.println("Sin resultados.");
        else res.forEach(System.out::println);
        System.out.println();
    }

    private static void listar() {
        System.out.println("\n--- Listado ---");
        List<Producto> todos = inventario.listarTodos();
        if (todos.isEmpty()) System.out.println("Inventario vacio.");
        else todos.forEach(System.out::println);
        System.out.println();
    }

    private static void informe() {
        System.out.println("\n--- Informe ---");
        int topN = leerEntero("Top por stock (N): ", 1, 20);
        int umbral = leerEntero("Umbral stock bajo: ", 0, Integer.MAX_VALUE);
        ResumenInventario r = inventario.generarResumen(topN, umbral);
        ReporteInventario.imprimirResumen(r);
    }

    private static int leerEntero(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = input.nextLine().trim();
            try {
                int v = Integer.parseInt(s);
                if (v < min || v > max) throw new NumberFormatException();
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entre " + min + " y " + max + ".");
            }
        }
    }

    private static BigDecimal leerMoneda(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = input.nextLine().trim().replace(",", ".");
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un valor numerico valido (ej: 19990.50).");
            }
        }
    }

    private static String leerTexto(String prompt, boolean obligatorio) {
        while (true) {
            System.out.print(prompt);
            String s = input.nextLine().trim();
            if (!obligatorio || !s.isEmpty()) return s;
            System.out.println("Este campo es obligatorio.");
        }
    }
}
