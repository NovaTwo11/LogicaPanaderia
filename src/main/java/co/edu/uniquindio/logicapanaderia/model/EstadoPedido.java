package co.edu.uniquindio.logicapanaderia.model;

public enum EstadoPedido {
    PENDIENTE("pendiente"),
    EN_PREPARACION("en preparación"),
    EN_CAMINO("en camino"),
    ENTREGADO("entregado"),
    CANCELADO("cancelado");

    private final String valor;

    EstadoPedido(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoPedido desdeValor(String valor) {
        for (EstadoPedido estado : values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                System.out.println("✅ EstadoPedido encontrado: " + valor);
                return estado;
            }
        }
        System.out.println("❌ EstadoPedido no encontrado para: " + valor);
        throw new IllegalArgumentException("Estado no válido: " + valor);
    }


    @Override
    public String toString() {
        return valor;
    }
}
