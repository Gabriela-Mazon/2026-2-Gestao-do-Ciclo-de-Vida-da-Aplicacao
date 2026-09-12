package projetoprincipiosdesign;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class PedidoRepositoryArquivo implements PedidoRepository {
    private final Path arquivo;

    public PedidoRepositoryArquivo() {
        this(Path.of("pedidos.txt"));
    }

    public PedidoRepositoryArquivo(Path arquivo) {
        this.arquivo = Objects.requireNonNull(arquivo, "O arquivo de pedidos é obrigatório.");
    }

    @Override
    public void salvar(Pedido pedido, double total) {
        String linha = pedido.getNomeCliente() + ";" + total + System.lineSeparator();
        try {
            Files.writeString(arquivo, linha, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar o pedido em arquivo.", e);
        }
    }
}
