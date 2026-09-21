 // Sprint 6 - Atividade de revisão — Conceitos fundamentais de Orientação a Objetos
// Aluna: Gabriela Mazon Rabello de Souza
// Matrícula: 2025.1.08.006


package atividade;
import java.util.ArrayList;
import java.util.List;

interface IConsole {
    void ligar();
    double calcularPreco();
    String getNome();
}

class DadosConsole {
    private final String nome;
    private final double precoBase;

    public DadosConsole(String nome, double precoBase) {
        if (precoBase < 0) {
            throw new IllegalArgumentException("O preço não pode ser menor que 0.");
        }
        this.nome = nome;
        this.precoBase = precoBase;
    }
    public String getNome() {
        return nome;
    }
    public double getPrecoBase() {
        return precoBase;
    }
}

class Nintendo implements IConsole {
    private final DadosConsole dados;
    public Nintendo(String nome, double precoBase) {
        this.dados = new DadosConsole(nome, precoBase);
    }
    @Override
    public String getNome() {
        return dados.getNome();
    }
    @Override
    public void ligar() {
        System.out.println("Nintendo ligado.");
    }
    @Override
    public double calcularPreco() {
        return dados.getPrecoBase() * 1.10;
    }
}

class Playstation implements IConsole {
    protected final DadosConsole dados;
    public Playstation(String nome, double precoBase) {
        this.dados = new DadosConsole(nome, precoBase);
    }
    @Override
    public String getNome() {
        return dados.getNome();
    }
    @Override
    public void ligar() {
        System.out.println("Playstation ligado.");
    }
    @Override
    public double calcularPreco() {
        return dados.getPrecoBase() * 1.20;
    }
}

class PlaystationPortatil extends Playstation {
    public PlaystationPortatil(String nome, double precoBase) {
        super(nome, precoBase);
    }
    @Override // sobrescrevendo o método ligar() do PS
    public void ligar() {
        System.out.println("Playstation Portátil ligado.");
    }
    @Override // sobrescrevendo o método calcularPreco() do PS
    public double calcularPreco() {
        return dados.getPrecoBase() * 1.15;
    }
}

class Xbox implements IConsole {
    private final DadosConsole dados;
    public Xbox(String nome, double precoBase) {
        this.dados = new DadosConsole(nome, precoBase);
    }
    @Override
    public String getNome() {
        return dados.getNome();
    }
    @Override
    public void ligar() {
        System.out.println("Xbox ligado.");
    }
    @Override
    public double calcularPreco() {
        return dados.getPrecoBase() * 1.18;
    }
}

class Loja {
    public void venderConsole(IConsole console) {
        console.ligar();

        double precoFinal = console.calcularPreco();

        System.out.println(console.getNome() + " -> Preço final: R$ " + precoFinal);
    }
    public void venderVarios(List<IConsole> consoles) {
        for (IConsole console : consoles) {
            venderConsole(console);
        }
    }
    public double calcularFaturamentoTotal(
        List<IConsole> consoles) {

        double total = 0;

        for (IConsole console : consoles) {
            total = total + console.calcularPreco();
        }

        return total;
        }
    }

public class Atividade_POO_Problema {

    public static void main(String[] args) {
        IConsole nintendo = new Nintendo("Nintendo Switch", 2000);
        IConsole playstation = new Playstation("Playstation 5", 3000);
        IConsole portatil = new PlaystationPortatil("Playstation Portátil", 2500);

        List<IConsole> consoles = new ArrayList<>();

        consoles.add(nintendo);
        consoles.add(playstation);
        consoles.add(portatil);

        Loja loja = new Loja();

        System.out.println("=== Venda sem Xbox ===");

        loja.venderVarios(consoles);

        double totalSemXbox = loja.calcularFaturamentoTotal(consoles);

        System.out.println( "Faturamento total: R$ " + totalSemXbox);

        IConsole xbox = new Xbox("Xbox Series X", 2800);

        consoles.add(xbox);

        System.out.println();
        System.out.println("=== Venda com Xbox ===");

        loja.venderVarios(consoles);

        double totalComXbox = loja.calcularFaturamentoTotal(consoles);

        System.out.println("Faturamento total: R$ " + totalComXbox);
}
}

// ===================== PARTE 1 — Encapsulamento =====================
// TODO (1.1): os atributos de Console estão públicos, permitindo que
// qualquer código altere nome/tipo/preco livremente (inclusive para
// valores inválidos, como um preco negativo). Torne os atributos privados.
// TODO (1.2): como os atributos agora são privados, crie métodos getters
// para nome, tipo e preco (pense: por que não criar também setters aqui?).

// ===================== PARTE 2 — Construtores =====================
// TODO (2.1): hoje um Console pode ser criado "pela metade" (por exemplo,
// esquecendo de definir o preco, que ficaria 0.0). Crie um construtor em
// Console que exija nome, tipo e preco, garantindo que todo objeto nasça
// em um estado válido.
// TODO (2.2): atualize o Main para criar os consoles usando o novo
// construtor, em vez de criar o objeto vazio e preencher os atributos um
// a um.

// ===================== PARTE 3 — Interface e Composição =====================
// TODO (3.1): crie uma interface IConsole com três métodos: void ligar(),
// double calcularPreco() e String getNome().
// TODO (3.2): crie uma classe DadosConsole com os atributos privados nome
// e precoBase, um construtor que os receba, e getters para os dois. Essa
// classe vai representar os dados que todo console tem em comum.
// TODO (3.3): crie as classes Nintendo e Playstation, cada uma implementando
// IConsole. Em vez de repetir nome/precoBase dentro delas, cada uma deve
// ter um atributo do tipo DadosConsole e delegar a ele as chamadas de
// getNome() e getPrecoBase() (isso é composição: "ter um" DadosConsole, em
// vez de repetir os mesmos atributos em cada classe).
// TODO (3.4): mova para Nintendo e Playstation a lógica que hoje está no
// if/else da Loja: a mensagem de ligar() e o percentual usado em
// calcularPreco() de cada uma (10% para Nintendo, 20% para Playstation).
// REFLEXÃO: por que usar uma classe separada (DadosConsole) em vez de
// colocar nome/precoBase diretamente dentro de Nintendo e Playstation?
// RESPOSTA: O DadosConsole evitará duplicação e centralizará dados e validações comuns, se a validação do preço mudar, apenas uma classe é alterada.

// ===================== PARTE 4 — Herança (usada de forma apropriada) =====================
// TODO (4.1): a classe Console original também previa um console
// "portátil". Em vez de fazer PlaystationPortatil implementar IConsole do
// zero (repetindo tudo o que já existe em Playstation), crie
// PlaystationPortatil como uma subclasse: class PlaystationPortatil extends
// Playstation. (Dica: para isso funcionar, o atributo que guarda o
// DadosConsole em Playstation precisa deixar de ser private e virar
// protected, para que a subclasse consiga acessá-lo.)
// TODO (4.2): sobrescreva (@Override) o método ligar() em
// PlaystationPortatil com a mensagem específica dela.
// TODO (4.3): sobrescreva (@Override) também o método calcularPreco() em
// PlaystationPortatil, usando um percentual próprio (por exemplo, 15% em
// vez dos 20% herdados de Playstation) — afinal, a versão portátil pode ter
// uma composição de custos diferente da versão de mesa.
// REFLEXÃO: PlaystationPortatil está sobrescrevendo DOIS métodos herdados
// de Playstation. Isso é um problema? Compare com o caso de
// PlaystationPortatil.jogarDisco() que vocês viram no material de SOLID
// (Solucao_P1), que lançava UnsupportedOperationException. Qual a diferença
// entre sobrescrever um método para mudar SEU CÁLCULO e sobrescrever um
// método para RECUSAR fazer o que ele promete (violando o Princípio de
// Substituição de Liskov)?
// RESPOSTA: Sobrescrever os dois métodos não é um problema porque o portátil continua cumprindo os contratos, apenas com implementações diferentes. 
// Já o método jogarDisco() violava o contrato da superclasse, pois não cumpria a função de disco, violando o princípio de substituição de Liskov.

// ===================== PARTE 5 — Polimorfismo e Extensibilidade (OCP) =====================
// TODO (5.1): reescreva venderConsole() da Loja para que ele receba um
// IConsole (não mais um Console) e apenas chame console.ligar() e
// console.calcularPreco(), sem nenhum if/else ou instanceof.
// TODO (5.2): crie em Loja um método venderVarios(List<IConsole> consoles)
// que chame venderConsole() para cada elemento de uma lista.
// TODO (5.3): crie em Loja um método
// calcularFaturamentoTotal(List<IConsole> consoles), que retorne a soma de
// calcularPreco() de todos os consoles da lista.
// TODO (5.4) [extensibilidade]: crie uma nova classe Xbox implementando
// IConsole (com seu próprio percentual, por exemplo 18%), SEM alterar
// nenhuma linha da classe Loja.
// TODO (5.5): no Main, monte uma List<IConsole> com os consoles já
// existentes, chame venderVarios() e calcularFaturamentoTotal(); em
// seguida, adicione um Xbox a essa lista e chame os dois métodos de novo,
// sem alterar Loja.
// DESAFIO FINAL: o que precisou mudar em Loja para o Xbox passar a
// funcionar? O que isso demonstra sobre o Princípio Aberto/Fechado (OCP)?
// RESPOSTA: Nada precisou mudar, Loja não precisa saber de novas classes, apenas trabalhar com a interface IConsole. 
// Isso foi possível pois a Loja depende da abstração IConsole e utiliza polimorfismo para chamar ligar() e calcularPreco().
// Com relação ao OCP, a Loja está aberta para extensãno mas fechada para modificação.