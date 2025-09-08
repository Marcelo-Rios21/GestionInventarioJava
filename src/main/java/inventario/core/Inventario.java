package inventario.core;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import inventario.dto.ResumenInventario;
import inventario.excepciones.ProductoNoEncontradoException;
import inventario.excepciones.ValidacionException;
import inventario.modelo.Producto;

public class Inventario {
    private final Map<String, Producto> productos = new HashMap<>();

    public void agregarProducto(Producto producto) {
        if (producto == null) {
            throw new ValidacionException("El producto no puede ser null.");
        }
        String codigo = producto.getCodigo();
        if (codigo == null || codigo.isBlank()) {
            throw new ValidacionException("Codigo invalido.");
        }
        if (productos.containsKey(codigo)) {
            throw new ValidacionException("Ya esiste un producto con el codigo " + codigo);
        }
        productos.put(codigo, producto);
    }

    public boolean eliminarProducto(String codigo) {
        if (codigo == null || codigo.isBlank()) throw new ValidacionException("Codigo invalido");
        return productos.remove(codigo) != null;
    }

    public void actualizarProducto(String codigo, Consumer<Producto> cambios) {
        if (codigo == null || codigo.isBlank()) throw new ValidacionException("Codigo invalido");
        Producto p = productos.get(codigo);
        if (p == null) throw new ProductoNoEncontradoException(codigo);
        cambios.accept(p);
    }

    public Optional<Producto> buscarPorId(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return Optional.empty();
        } 
        return Optional.ofNullable(productos.get(codigo));
    }

    public List<Producto> buscarPorNombre(String nombreParcial) {
        if (nombreParcial == null || nombreParcial.isBlank()) {
            return List.of();
        }
        String needle = nombreParcial.toLowerCase(Locale.ROOT);
        return productos.values().stream().filter(p -> p.getNombre().toLowerCase(Locale.ROOT).contains(needle))
                .sorted(Comparator.comparing(Producto::getNombre)).toList();
    }

    public List<Producto> buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        String needle = texto.toLowerCase(Locale.ROOT);
        return productos.values().stream().filter(p -> p.getNombre().toLowerCase(Locale.ROOT).contains(needle)
                || p.getDescripcion().toLowerCase(Locale.ROOT).contains(needle)).sorted(Comparator.comparing(Producto::getNombre))
                .toList();
    }

    public List<Producto> listarTodos() {
        return productos.values().stream().sorted(Comparator.comparing(Producto::getCodigo)).toList();
    }

    public List<Producto> listarOrdenado(Comparator<Producto> cmp) {
        return productos.values().stream().sorted(cmp).toList();
    }

    public ResumenInventario generarResumen(int topN, int umbralBajo) {
        final int n = (topN < 1) ? 3 : topN;
        final int u = Math.max(0, umbralBajo);

        int cant = productos.size();
        long totalItems = productos.values().stream().mapToLong(Producto::getStock).sum();

        BigDecimal valorTotal = productos.values().stream().map(p -> p.getPrecio().multiply(BigDecimal.valueOf(p.getStock())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Producto> top = productos.values().stream().sorted(Comparator.comparingInt(Producto::getStock)
            .reversed()).limit(n).toList();

        List<Producto> bajos = productos.values().stream().filter(p -> p.getStock() < u).sorted(Comparator.comparingInt(Producto::getStock))
            .toList();

        return new ResumenInventario(cant, totalItems, valorTotal, top, bajos);
    }

    public int size() { 
        return productos.size(); 
    }

    public boolean contiene(String codigo) { 
        return productos.containsKey(codigo); 
    }
}
