package projetoprincipiosdesign;

public class SemDesconto implements Desconto {
    @Override
    public double aplicar(double subtotal) {
        return subtotal;
    }
}
