package projetoprincipiosdesign;

public class Endereco {
    private String logradouro;
    private Cidade cidade;

    public Endereco(String logradouro, Cidade cidade) {
        this.logradouro = logradouro;
        this.cidade = cidade;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public String getNomeCidade() {
        return cidade.getNome();
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }
}
