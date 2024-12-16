package web.devaluga.filter;

import java.time.LocalDate;

public class PessoaFilter {

    private Long codigo;
    private String nome;
    private String cpf;
    private String telefone;
    private String email;
    private String endereco;
    private LocalDate nascimentoDe;
    private LocalDate nascimentoAte;
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
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
    public LocalDate getNascimentoDe() {
        return nascimentoDe;
    }
    public void setNascimentoDe(LocalDate nascimentoDe) {
        this.nascimentoDe = nascimentoDe;
    }
    public LocalDate getNascimentoAte() {
        return nascimentoAte;
    }
    public void setNascimentoAte(LocalDate nascimentoAte) {
        this.nascimentoAte = nascimentoAte;
    }
    
}
