package projetoprincipiosdesign;

import java.util.OptionalDouble;

public class EntregaRetiradaLoja implements TipoEntrega {
    @Override
    public OptionalDouble calcularFrete(double total) {
        if (total < 50.0) {
            return OptionalDouble.empty();
        }

        return OptionalDouble.of(0.0);
    }
}
