package projetoprincipiosdesign;

import java.util.OptionalDouble;

public class Entrega implements TipoEntrega {
    @Override
    public OptionalDouble calcularFrete(double total) {
        return OptionalDouble.of(15.0);
    }
}
