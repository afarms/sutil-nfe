package br.com.sutilnfe.backend.infra.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "erros_importacao_nfe")
public class ErroImportacaoNfeEntity {

    @Id
    private UUID id;

    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;

    private String caminho;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "ocorrido_em", nullable = false)
    private OffsetDateTime ocorridoEm;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (ocorridoEm == null) {
            ocorridoEm = now;
        }
        createdAt = now;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public OffsetDateTime getOcorridoEm() {
        return ocorridoEm;
    }

    public void setOcorridoEm(OffsetDateTime ocorridoEm) {
        this.ocorridoEm = ocorridoEm;
    }
}
