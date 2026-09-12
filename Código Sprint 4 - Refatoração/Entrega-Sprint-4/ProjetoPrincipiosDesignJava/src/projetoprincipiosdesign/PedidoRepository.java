package projetoprincipiosdesign;

@FunctionalInterface
public interface PedidoRepository {
    void salvar(Pedido pedido, double total);
}
