package projetoprincipiosdesign;

import java.util.OptionalDouble;

public interface TipoEntrega {
    /**
     * Para um total finito e não negativo, retorna o frete quando a modalidade
     * está disponível, ou OptionalDouble.empty() quando não está.
     * Indisponibilidade de uma modalidade não deve lançar exceção.
     */
    OptionalDouble calcularFrete(double total);
}
