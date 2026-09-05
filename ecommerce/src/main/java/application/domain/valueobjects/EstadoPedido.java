package com.ecommerce.domain.valueobjects;

/**
 * EstadoPedido
 * ------------
 * Representa la etapa en la que se encuentra una compra dentro de
 * Ecommerce. (Pedido.estado)
 *
 * Ciclo definido en el documento:
 *   Carrito -> Pendiente de Pago -> Pagado -> Despachado -> Entregado/Finalizado
 *
 * Un pedido en estado ENTREGADO no puede ser modificado.
 */
public enum EstadoPedido implements DomainCatalog {

    CARRITO("CARRITO", "Carrito",
            "Los productos todavía se encuentran en selección provisional."),
    PENDIENTE_PAGO("PENDIENTE_PAGO", "Pendiente de Pago",
            "El pedido espera la confirmación financiera."),
    PAGADO("PAGADO", "Pagado",
            "El pago fue confirmado y comienza el alistamiento."),
    DESPACHADO("DESPACHADO", "Despachado",
            "El pedido salió físicamente de la bodega."),
    ENTREGADO("ENTREGADO", "Entregado / Finalizado",
            "La entrega fue completada satisfactoriamente.");

    private final String code;
    private final String name;
    private final String description;

    EstadoPedido(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /** Un pedido finalizado (ENTREGADO) no puede ser modificado. */
    public boolean esFinal() {
        return this == ENTREGADO;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
