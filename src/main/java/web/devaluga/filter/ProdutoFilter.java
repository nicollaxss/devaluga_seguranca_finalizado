package web.devaluga.filter;

public class ProdutoFilter {

    private Long codigo;
    private String nome;
    private String descricao;
    private Float precoDiario;

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "codigo: " + codigo + "\nnome: " + nome + "\ndescricao: " + descricao;
    }

    public Float getPrecoDiario() {
        return precoDiario;
    }

    public void setPrecoDiario(Float precoDiario) {
        this.precoDiario = precoDiario;
    }

}
