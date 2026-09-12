package projetoprincipiosdesign;

public class DescontoAluno implements Desconto {
    @Override
    public double aplicar(double subtotal) {
        return subtotal * 0.90;
    }
}
