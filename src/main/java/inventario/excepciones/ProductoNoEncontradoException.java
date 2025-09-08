package inventario.excepciones;

public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(String codigo) {
        super("Producto no encontrado: " + codigo);
    }
    
}
