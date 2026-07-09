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

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "arquivos_nfe")
public class ArquivoNfeEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nota_fiscal_id")
    private NotaFiscalEntity notaFiscal;

    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;

    private String caminho;

    @Column(nullable = false)
    private String status;

    @Column(name = "mensagem_erro")
    private String mensagemErro;

    @Column(name = "processado_em")
    private OffsetDateTime processadoEm;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
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

    public NotaFiscalEntity getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(NotaFiscalEntity notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public OffsetDateTime getProcessadoEm() {
        return processadoEm;
    }

    public void setProcessadoEm(OffsetDateTime processadoEm) {
        this.processadoEm = processadoEm;
    }
}
