package projetoprincipiosdesign;

@FunctionalInterface
public interface Desconto {
    /** Retorna o total a pagar após aplicar a regra ao subtotal. */
    double aplicar(double subtotal);
}
