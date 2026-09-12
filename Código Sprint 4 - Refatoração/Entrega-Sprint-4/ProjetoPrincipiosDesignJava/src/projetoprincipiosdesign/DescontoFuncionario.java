package projetoprincipiosdesign;

public class DescontoFuncionario implements Desconto {
    @Override
    public double aplicar(double subtotal) {
        return subtotal * 0.80;
    }
}
