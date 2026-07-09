package br.com.sutilnfe.backend.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notas_fiscais")
public class NotaFiscalEntity {

    @Id
    private UUID id;

    private String numero;

    @Column(name = "chave_acesso", nullable = false)
    private String chaveAcesso;

    private LocalDate competencia;

    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "ano_emissao", insertable = false, updatable = false)
    private Integer anoEmissao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "emitente_id", nullable = false)
    private PessoaEntity emitente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tomador_id", nullable = false)
    private PessoaEntity tomador;

    @Column(name = "descricao_servico")
    private String descricaoServico;

    @Column(name = "valor_servico", nullable = false)
    private BigDecimal valorServico;

    @Column(name = "valor_liquido", nullable = false)
    private BigDecimal valorLiquido;

    @Column(name = "codigo_tributacao")
    private String codigoTributacao;

    @Column(name = "municipio_prestacao")
    private String municipioPrestacao;

    @Column(name = "arquivo_origem")
    private String arquivoOrigem;

    @Column(name = "split_pj", nullable = false)
    private Integer splitPj;

    @Column(nullable = false)
    private BigDecimal incremento;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public Integer getAnoEmissao() {
        return anoEmissao;
    }

    public PessoaEntity getEmitente() {
        return emitente;
    }

    public void setEmitente(PessoaEntity emitente) {
        this.emitente = emitente;
    }

    public PessoaEntity getTomador() {
        return tomador;
    }

    public void setTomador(PessoaEntity tomador) {
        this.tomador = tomador;
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
        return splitPj;
    }

    public void setSplitPj(Integer splitPj) {
        this.splitPj = splitPj;
    }

    public BigDecimal getIncremento() {
        return incremento;
    }

    public void setIncremento(BigDecimal incremento) {
        this.incremento = incremento;
    }
}
