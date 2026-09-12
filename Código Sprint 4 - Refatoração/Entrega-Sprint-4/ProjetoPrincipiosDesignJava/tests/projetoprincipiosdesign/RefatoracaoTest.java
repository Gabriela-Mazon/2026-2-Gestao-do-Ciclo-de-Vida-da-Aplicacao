package projetoprincipiosdesign;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;

/** Testes executáveis com o próprio JDK, sem dependências externas. */
public final class RefatoracaoTest {
    private static int aprovados;

    @FunctionalInterface
    private interface Acao {
        void executar() throws Exception;
    }

    @FunctionalInterface
    private interface AcaoComDiretorio {
        void executar(Path diretorio) throws Exception;
    }

    private RefatoracaoTest() {
    }

    public static void main(String[] args) throws Exception {
        Locale anterior = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("pt-BR"));
        try {
            testar("Desconto de aluno: R$ 144,00", () -> conferirTotal(new DescontoAluno(), 144.0));
            testar("Desconto de professor: R$ 136,00", () -> conferirTotal(new DescontoProfessor(), 136.0));
            testar("Desconto de funcionário: R$ 128,00", () -> conferirTotal(new DescontoFuncionario(), 128.0));
            testar("Pedido sem desconto: R$ 160,00", () -> conferirTotal(new SemDesconto(), 160.0));
            testar("Pedido sem itens mantém total zero", RefatoracaoTest::pedidoVazio);
            testar("Cidade e nome acompanham alterações do domínio", RefatoracaoTest::dadosDoCliente);
            testar("Finalização por cartão preserva pagamento e resumo", () -> conferirPagamento(new PagamentoCartao(), "Pagamento no cartão: R$ 144,00"));
            testar("Finalização por PIX preserva pagamento", () -> conferirPagamento(new PagamentoPix(), "PIX pago: R$ 144,00"));
            testar("Finalização por boleto preserva geração da linha", RefatoracaoTest::finalizacaoBoleto);
            testar("Registro direto de boleto permanece disponível", RefatoracaoTest::registroBoleto);
            testar("Parcelamento por cartão permanece disponível", RefatoracaoTest::parcelamento);
            testar("Repositório cria arquivo UTF-8 com nome e total", RefatoracaoTest::criarArquivo);
            testar("Repositório acrescenta pedidos sem apagar anteriores", RefatoracaoTest::acrescentarArquivo);
            testar("Erro real de escrita mantém causa e mensagem", RefatoracaoTest::falhaDeEscrita);
            testar("Finalização usa o desconto injetado e salva antes de pagar", RefatoracaoTest::ordemEDesconto);
            testar("Falha do repositório interrompe pagamento e confirmação", RefatoracaoTest::falhaDoRepositorio);
            testar("Falha de pagamento não imprime confirmação de sucesso", RefatoracaoTest::falhaDePagamento);
            testar("Novo desconto e pagamento funcionam sem alterar o serviço", RefatoracaoTest::extensao);
            testar("Dependências obrigatórias são verificadas na construção", RefatoracaoTest::dependenciasObrigatorias);
            testar("Entrega em domicílio mantém frete de R$ 15,00", RefatoracaoTest::entregaDomicilio);
            testar("Retirada abaixo de R$ 50 fica indisponível", RefatoracaoTest::retiradaIndisponivel);
            testar("Retirada a partir de R$ 50 é gratuita", RefatoracaoTest::retiradaGratuita);
            testar("Mesmo consumidor trata ambas as entregas pelo contrato", RefatoracaoTest::substituicaoDasEntregas);
            System.out.println();
            System.out.println("RESULTADO: " + aprovados + " testes aprovados; 0 falhas.");
        } finally {
            Locale.setDefault(anterior);
        }
    }

    private static Pedido exemplo() {
        return new Pedido(new Cliente("Ana", new Endereco("Rua das Flores", new Cidade("Belo Horizonte"))),
            List.of(new ItemPedido("Livro de Engenharia de Software", 120.0, 1), new ItemPedido("Caderno", 20.0, 2)));
    }

    private static PedidoService servico(Desconto desconto) {
        return new PedidoService((pedido, total) -> { }, desconto, valor -> { });
    }

    private static void conferirTotal(Desconto desconto, double esperado) {
        igual(esperado, servico(desconto).calcularTotal(exemplo()));
    }

    private static void pedidoVazio() {
        Pedido pedido = new Pedido(exemplo().getCliente(), List.of());
        for (Desconto desconto : List.of(new DescontoAluno(), new DescontoProfessor(), new DescontoFuncionario(), new SemDesconto())) {
            igual(0.0, servico(desconto).calcularTotal(pedido));
        }
    }

    private static void dadosDoCliente() {
        Pedido pedido = exemplo();
        PedidoService service = servico(new SemDesconto());
        igual("Belo Horizonte", service.obterCidadeEntrega(pedido));
        pedido.getCliente().setNome("João");
        pedido.getCliente().getEndereco().setCidade(new Cidade("Alfenas"));
        igual("João", pedido.getNomeCliente());
        igual("Alfenas", service.obterCidadeEntrega(pedido));
        pedido.setCliente(new Cliente("Maria", new Endereco("Rua A", new Cidade("Varginha"))));
        igual("Varginha", service.obterCidadeEntrega(pedido));
    }

    private static void conferirPagamento(IPagamento pagamento, String mensagem) throws Exception {
        List<Double> salvos = new ArrayList<>();
        PedidoService service = new PedidoService((pedido, total) -> salvos.add(total), new DescontoAluno(), pagamento);
        String saida = capturar(() -> service.finalizarPedido(exemplo()));
        igual(List.of(144.0), salvos);
        contem(saida, "Cliente: Ana");
        contem(saida, "Total: R$ 144,00");
        contem(saida, mensagem);
        contem(saida, "Enviando mensagem para Ana: pedido finalizado.");
    }

    private static void finalizacaoBoleto() throws Exception {
        GeraBoleto boleto = new PagamentoBoleto();
        conferirPagamento(boleto::gerarBoleto, "Linha digitável gerada para R$ 144,00");
    }

    private static void registroBoleto() throws Exception {
        String saida = capturar(() -> new PagamentoBoleto().pagar(144.0));
        igual("Boleto registrado: R$ 144,00" + System.lineSeparator(), saida);
    }

    private static void parcelamento() throws Exception {
        Parcelavel cartao = new PagamentoCartao();
        String saida = capturar(() -> cartao.parcelar(120.0, 3));
        igual("Cartão parcelado em 3x de R$ 40,00" + System.lineSeparator(), saida);
    }

    private static void criarArquivo() throws Exception {
        comDiretorioTemporario(diretorio -> {
            Path arquivo = diretorio.resolve("pedidos.txt");
            Pedido pedido = exemplo();
            pedido.getCliente().setNome("João");
            new PedidoRepositoryArquivo(arquivo).salvar(pedido, 144.0);
            igual("João;144.0" + System.lineSeparator(), Files.readString(arquivo, StandardCharsets.UTF_8));
        });
    }

    private static void acrescentarArquivo() throws Exception {
        comDiretorioTemporario(diretorio -> {
            Path arquivo = diretorio.resolve("pedidos.txt");
            PedidoRepository repository = new PedidoRepositoryArquivo(arquivo);
            Pedido pedido = exemplo();
            repository.salvar(pedido, 144.0);
            pedido.getCliente().setNome("Maria");
            repository.salvar(pedido, 136.0);
            igual(List.of("Ana;144.0", "Maria;136.0"), Files.readAllLines(arquivo, StandardCharsets.UTF_8));
        });
    }

    private static void falhaDeEscrita() throws Exception {
        comDiretorioTemporario(diretorio -> {
            RuntimeException erro = deveLancar(RuntimeException.class,
                () -> new PedidoRepositoryArquivo(diretorio).salvar(exemplo(), 144.0));
            igual("Erro ao salvar o pedido em arquivo.", erro.getMessage());
            verificar(erro.getCause() instanceof IOException, "A causa IOException deve ser preservada.");
        });
    }

    private static void ordemEDesconto() throws Exception {
        List<String> eventos = new ArrayList<>();
        PedidoService service = new PedidoService(
            (pedido, total) -> eventos.add("salvar:" + total),
            new DescontoProfessor(),
            total -> eventos.add("pagar:" + total));
        String saida = capturar(() -> service.finalizarPedido(exemplo()));
        igual(List.of("salvar:136.0", "pagar:136.0"), eventos);
        contem(saida, "Total: R$ 136,00");
    }

    private static void falhaDoRepositorio() throws Exception {
        List<Double> pagamentos = new ArrayList<>();
        IllegalStateException falha = new IllegalStateException("Falha simulada ao salvar");
        PedidoService service = new PedidoService((pedido, total) -> { throw falha; }, new DescontoAluno(), pagamentos::add);
        String saida = capturar(() -> verificar(deveLancar(IllegalStateException.class,
            () -> service.finalizarPedido(exemplo())) == falha, "Deve propagar a falha do repositório."));
        verificar(pagamentos.isEmpty(), "Não deve pagar se não conseguiu salvar.");
        verificar(!saida.contains("pedido finalizado"), "Não deve confirmar a operação que falhou.");
    }

    private static void falhaDePagamento() throws Exception {
        List<Double> salvos = new ArrayList<>();
        PedidoService service = new PedidoService((pedido, total) -> salvos.add(total), new DescontoAluno(),
            total -> { throw new IllegalStateException("Falha simulada ao pagar"); });
        String saida = capturar(() -> deveLancar(IllegalStateException.class, () -> service.finalizarPedido(exemplo())));
        igual(List.of(144.0), salvos);
        verificar(!saida.contains("pedido finalizado"), "Não deve confirmar pagamento que falhou.");
    }

    private static void extensao() throws Exception {
        List<Double> salvos = new ArrayList<>();
        List<Double> pagamentos = new ArrayList<>();
        Desconto convenio = subtotal -> subtotal * 0.95;
        IPagamento transferencia = pagamentos::add;
        PedidoService service = new PedidoService((pedido, total) -> salvos.add(total), convenio, transferencia);
        capturar(() -> service.finalizarPedido(exemplo()));
        igual(List.of(152.0), salvos);
        igual(List.of(152.0), pagamentos);
    }

    private static void dependenciasObrigatorias() throws Exception {
        PedidoRepository repository = (pedido, total) -> { };
        IPagamento pagamento = valor -> { };
        deveLancar(NullPointerException.class, () -> new PedidoService(null, new DescontoAluno(), pagamento));
        deveLancar(NullPointerException.class, () -> new PedidoService(repository, null, pagamento));
        deveLancar(NullPointerException.class, () -> new PedidoService(repository, new DescontoAluno(), null));
    }

    private static void entregaDomicilio() {
        TipoEntrega entrega = new Entrega();
        for (double total : new double[]{0.0, 49.99, 50.0, 160.0}) {
            igual(15.0, entrega.calcularFrete(total).orElseThrow());
        }
    }

    private static void retiradaIndisponivel() {
        TipoEntrega retirada = new EntregaRetiradaLoja();
        for (double total : new double[]{0.0, 49.99}) {
            verificar(retirada.calcularFrete(total).isEmpty(), "Retirada abaixo de R$ 50 deve estar indisponível.");
        }
    }

    private static void retiradaGratuita() {
        TipoEntrega retirada = new EntregaRetiradaLoja();
        for (double total : new double[]{50.0, 50.01, 160.0}) {
            igual(0.0, retirada.calcularFrete(total).orElseThrow());
        }
    }

    private static void substituicaoDasEntregas() {
        List<TipoEntrega> tipos = List.of(new Entrega(), new EntregaRetiradaLoja());
        List<Double> fretesDisponiveis = new ArrayList<>();
        for (double total : new double[]{49.99, 50.0}) {
            for (TipoEntrega tipo : tipos) {
                OptionalDouble cotacao = tipo.calcularFrete(total);
                cotacao.ifPresent(fretesDisponiveis::add);
            }
        }
        igual(List.of(15.0, 15.0, 0.0), fretesDisponiveis);
    }

    private static void testar(String nome, Acao acao) throws Exception {
        acao.executar();
        aprovados++;
        System.out.println("[OK] " + nome);
    }

    private static String capturar(Acao acao) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream anterior = System.out;
        try (PrintStream captura = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            System.setOut(captura);
            try {
                acao.executar();
            } finally {
                System.setOut(anterior);
            }
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private static void comDiretorioTemporario(AcaoComDiretorio acao) throws Exception {
        Path diretorio = Files.createTempDirectory("sprint4-teste-");
        try {
            acao.executar(diretorio);
        } finally {
            try (var caminhos = Files.walk(diretorio)) {
                for (Path caminho : caminhos.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(caminho);
                }
            }
        }
    }

    private static <T extends Throwable> T deveLancar(Class<T> tipo, Acao acao) throws Exception {
        try {
            acao.executar();
        } catch (Throwable erro) {
            if (tipo.isInstance(erro)) {
                return tipo.cast(erro);
            }
            throw new AssertionError("Exceção diferente da esperada: " + tipo.getName(), erro);
        }
        throw new AssertionError("Esperava exceção " + tipo.getName());
    }

    private static void igual(Object esperado, Object atual) {
        verificar(esperado.equals(atual), "Esperado: " + esperado + "; recebido: " + atual);
    }

    private static void igual(double esperado, double atual) {
        verificar(Double.isFinite(atual) && Math.abs(esperado - atual) < 0.0000001,
            "Esperado: " + esperado + "; recebido: " + atual);
    }

    private static void contem(String texto, String trecho) {
        verificar(texto.contains(trecho), "Saída não contém: " + trecho);
    }

    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
