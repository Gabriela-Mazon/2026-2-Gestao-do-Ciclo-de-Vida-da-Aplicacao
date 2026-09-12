package projetoprincipiosdesign;

import java.util.Objects;

public class PedidoService {
    private final PedidoRepository repository;
    private final Desconto desconto;
    private final IPagamento pagamento;

    public PedidoService(PedidoRepository repository, Desconto desconto, IPagamento pagamento) {
        this.repository = Objects.requireNonNull(repository, "O repositório é obrigatório.");
        this.desconto = Objects.requireNonNull(desconto, "A regra de desconto é obrigatória.");
        this.pagamento = Objects.requireNonNull(pagamento, "A forma de pagamento é obrigatória.");
    }

    public double calcularTotal(Pedido pedido) {
        double total = 0.0;

        for (ItemPedido item : pedido.getItens()) {
            total += item.getPreco() * item.getQuantidade();
        }

        return desconto.aplicar(total);
    }

    public String obterCidadeEntrega(Pedido pedido) {
        return pedido.getCidadeEntrega();
    }

    public void finalizarPedido(Pedido pedido) {
        double total = calcularTotal(pedido);

        System.out.println("Salvando pedido em arquivo...");
        repository.salvar(pedido, total);

        System.out.println("Gerando resumo do pedido...");
        System.out.println("Cliente: " + pedido.getNomeCliente());
        System.out.printf("Total: R$ %.2f%n", total);

        pagamento.pagar(total);

        System.out.println(
            "Enviando mensagem para " + pedido.getNomeCliente() + ": pedido finalizado."
        );
    }
}
