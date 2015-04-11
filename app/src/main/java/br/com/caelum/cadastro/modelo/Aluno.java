package br.com.caelum.cadastro.modelo;

import java.io.Serializable;

/**
 * Created by android5193 on 08/04/15.
 */
public class Aluno implements Serializable {
    private Long id;
    private String nome;
    private String telefone;
    private String endereco;
    private String site;

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    private String caminhoFoto;

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    private Double nota;

    @Override
    public String toString(){
        return String.format("%1$s - %2$s", this.id, this.nome);
    }
}
