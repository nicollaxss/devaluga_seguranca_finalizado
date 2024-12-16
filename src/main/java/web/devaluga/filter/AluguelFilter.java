package web.devaluga.filter;

public class AluguelFilter {

    private Long codigo;
    private String nomeUsuario;
    private String nomeProduto;
    private Float valorMinimo;
    private Float valorMaximo;

    public Float getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(Float valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public Float getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(Float valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
}
