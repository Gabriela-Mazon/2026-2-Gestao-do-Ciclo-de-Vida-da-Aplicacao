package projetoprincipiosdesign;

public class PagamentoBoleto implements IPagamento, GeraBoleto {
    @Override
    public void pagar(double valor) {
        System.out.printf("Boleto registrado: R$ %.2f%n", valor);
    }

    @Override
    public void gerarBoleto(double valor) {
        System.out.printf("Linha digitável gerada para R$ %.2f%n", valor);
    }
}
