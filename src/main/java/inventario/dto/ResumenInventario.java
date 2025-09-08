package inventario.dto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import inventario.modelo.Producto;

public class ResumenInventario {
    private final int cantidadProductos;
    private final long totalItems;
    private final BigDecimal valorTotal;
    private final List<Producto> topPorStock;
    private final List<Producto> stockBajo;

    public ResumenInventario(int cantidadProductos, long totalItems, BigDecimal valorTotal, List<Producto> topPorStock, List<Producto> stockBajo) {
        this.cantidadProductos = cantidadProductos;
        this.totalItems = totalItems;
        this.valorTotal = valorTotal;
        this.topPorStock = topPorStock;
        this.stockBajo = stockBajo;
    }

    //GETTERS
    public int getCantidadProductos() {
        return cantidadProductos;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public List<Producto> getTopPorStock() {
        return Collections.unmodifiableList(topPorStock);
    }

    public List<Producto> getStockBajo() {
        return Collections.unmodifiableList(stockBajo);
    }



}
