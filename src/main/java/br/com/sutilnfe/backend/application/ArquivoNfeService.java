package br.com.sutilnfe.backend.application;

import br.com.sutilnfe.backend.infra.persistence.ArquivoNfeEntity;
import br.com.sutilnfe.backend.infra.persistence.ArquivoNfeJpaRepository;
import br.com.sutilnfe.backend.infra.persistence.ErroImportacaoNfeEntity;
import br.com.sutilnfe.backend.infra.persistence.ErroImportacaoNfeJpaRepository;
import br.com.sutilnfe.backend.infra.persistence.NotaFiscalJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ArquivoNfeService {

    private final ArquivoNfeJpaRepository arquivoNfeJpaRepository;
    private final ErroImportacaoNfeJpaRepository erroImportacaoNfeJpaRepository;
    private final NotaFiscalJpaRepository notaFiscalJpaRepository;

    public ArquivoNfeService(
            ArquivoNfeJpaRepository arquivoNfeJpaRepository,
            ErroImportacaoNfeJpaRepository erroImportacaoNfeJpaRepository,
            NotaFiscalJpaRepository notaFiscalJpaRepository
    ) {
        this.arquivoNfeJpaRepository = arquivoNfeJpaRepository;
        this.erroImportacaoNfeJpaRepository = erroImportacaoNfeJpaRepository;
        this.notaFiscalJpaRepository = notaFiscalJpaRepository;
    }

    @Transactional
    public UUID registrarProcessando(Path arquivo) {
        ArquivoNfeEntity entity = new ArquivoNfeEntity();
        entity.setNomeArquivo(arquivo.getFileName().toString());
        entity.setCaminho(arquivo.toAbsolutePath().toString());
        entity.setStatus("PROCESSANDO");
        return arquivoNfeJpaRepository.save(entity).getId();
    }

    @Transactional
    public void marcarProcessado(UUID arquivoId, String notaFiscalId) {
        ArquivoNfeEntity arquivo = arquivoNfeJpaRepository.findById(arquivoId)
                .orElseThrow(() -> new IllegalArgumentException("Arquivo NFE nao encontrado: " + arquivoId));

        if (notaFiscalId != null && !notaFiscalId.isBlank()) {
            notaFiscalJpaRepository.findById(UUID.fromString(notaFiscalId))
                    .ifPresent(arquivo::setNotaFiscal);
        }

        arquivo.setStatus("PROCESSADO");
        arquivo.setMensagemErro(null);
        arquivo.setProcessadoEm(OffsetDateTime.now());
        arquivoNfeJpaRepository.save(arquivo);
    }

    @Transactional
    public void registrarErro(UUID arquivoId, Path arquivo, String motivo) {
        String mensagem = motivo == null || motivo.isBlank() ? "Erro desconhecido ao importar PDF" : motivo;

        if (arquivoId != null) {
            arquivoNfeJpaRepository.findById(arquivoId).ifPresent(entity -> {
                entity.setStatus("ERRO");
                entity.setMensagemErro(mensagem);
                entity.setProcessadoEm(OffsetDateTime.now());
                arquivoNfeJpaRepository.save(entity);
            });
        }

        ErroImportacaoNfeEntity erro = new ErroImportacaoNfeEntity();
        erro.setNomeArquivo(arquivo.getFileName().toString());
        erro.setCaminho(arquivo.toAbsolutePath().toString());
        erro.setMotivo(mensagem);
        erro.setOcorridoEm(OffsetDateTime.now());
        erroImportacaoNfeJpaRepository.save(erro);
    }
}
