package br.com.sutilnfe.backend.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NotaFiscal {

    private String id;
    private String numero;
    private String chaveAcesso;
    private LocalDate competencia;
    private LocalDate dataEmissao;
    private String emitenteNome;
    private String emitenteCnpjCpf;
    private String tomadorNome;
    private String tomadorCnpjCpf;
    private String descricaoServico;
    private BigDecimal valorServico;
    private BigDecimal valorLiquido;
    private String codigoTributacao;
    private String municipioPrestacao;
    private String arquivoOrigem;
    private Integer splitPj; // Porcentagem da nota fiscal, normalmente definido como 60% para PJ
    private BigDecimal incremento;

    public NotaFiscal() {
    }

    public NotaFiscal(String id, String numero, String chaveAcesso, LocalDate competencia, LocalDate dataEmissao, String emitenteNome, String emitenteCnpjCpf, String tomadorNome, String tomadorCnpjCpf, String descricaoServico, BigDecimal valorServico, BigDecimal valorLiquido, String codigoTributacao, String municipioPrestacao, String arquivoOrigem, Integer splitPj) {
        this.id = id;
        this.numero = numero;
        this.chaveAcesso = chaveAcesso;
        this.competencia = competencia;
        this.dataEmissao = dataEmissao;
        this.emitenteNome = emitenteNome;
        this.emitenteCnpjCpf = emitenteCnpjCpf;
        this.tomadorNome = tomadorNome;
        this.tomadorCnpjCpf = tomadorCnpjCpf;
        this.descricaoServico = descricaoServico;
        this.valorServico = valorServico;
        this.valorLiquido = valorLiquido;
        this.codigoTributacao = codigoTributacao;
        this.municipioPrestacao = municipioPrestacao;
        this.arquivoOrigem = arquivoOrigem;
        this.splitPj = splitPj;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public LocalDate getCompetencia() {
        return competencia;
    }

    public void setCompetencia(LocalDate competencia) {
        this.competencia = competencia;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getEmitenteNome() {
        return emitenteNome;
    }

    public void setEmitenteNome(String emitenteNome) {
        this.emitenteNome = emitenteNome;
    }

    public String getEmitenteCnpjCpf() {
        return emitenteCnpjCpf;
    }

    public void setEmitenteCnpjCpf(String emitenteCnpjCpf) {
        this.emitenteCnpjCpf = emitenteCnpjCpf;
    }

    public String getTomadorNome() {
        return tomadorNome;
    }

    public void setTomadorNome(String tomadorNome) {
        this.tomadorNome = tomadorNome;
    }

    public String getTomadorCnpjCpf() {
        return tomadorCnpjCpf;
    }

    public void setTomadorCnpjCpf(String tomadorCnpjCpf) {
        this.tomadorCnpjCpf = tomadorCnpjCpf;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getCodigoTributacao() {
        return codigoTributacao;
    }

    public void setCodigoTributacao(String codigoTributacao) {
        this.codigoTributacao = codigoTributacao;
    }

    public String getMunicipioPrestacao() {
        return municipioPrestacao;
    }

    public void setMunicipioPrestacao(String municipioPrestacao) {
        this.municipioPrestacao = municipioPrestacao;
    }

    public String getArquivoOrigem() {
        return arquivoOrigem;
    }

    public void setArquivoOrigem(String arquivoOrigem) {
        this.arquivoOrigem = arquivoOrigem;
    }

    public Integer getSplitPj() {
        return this.splitPj;
    }

    public void setSplitPj(Integer splitPj) {
        this.splitPj = splitPj;
    }

    public BigDecimal getIncremento() {return this.incremento;}

    public void setIncremento(BigDecimal incremento) {this.incremento = incremento;}

    @Override
    public String toString() {
        return "NotaFiscal{" +
                "id='" + id + '\'' +
                ", numero='" + numero + '\'' +
                ", chaveAcesso='" + chaveAcesso + '\'' +
                ", competencia=" + competencia +
                ", dataEmissao=" + dataEmissao +
                ", emitenteNome='" + emitenteNome + '\'' +
                ", emitenteCnpjCpf='" + emitenteCnpjCpf + '\'' +
                ", tomadorNome='" + tomadorNome + '\'' +
                ", tomadorCnpjCpf='" + tomadorCnpjCpf + '\'' +
                ", descricaoServico='" + descricaoServico + '\'' +
                ", valorServico=" + valorServico +
                ", valorLiquido=" + valorLiquido +
                ", codigoTributacao='" + codigoTributacao + '\'' +
                ", municipioPrestacao='" + municipioPrestacao + '\'' +
                ", arquivoOrigem='" + arquivoOrigem + '\'' +
                ", splitPj=" + splitPj +
                ", incremento=" + incremento +
                '}';
    }
}
