package br.com.sutilnfe.backend.application;

import br.com.sutilnfe.backend.domain.NotaFiscal;
import br.com.sutilnfe.backend.domain.NotaFiscalRepository;
import br.com.sutilnfe.backend.infra.filesystem.NfePathConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static br.com.sutilnfe.backend.infra.utils.PdfNfeRoles.extrairDadosDoPDF;
import static br.com.sutilnfe.backend.infra.utils.PdfNfeRoles.moverParaProcessados;

@Service
public class ProcessarPdfService {

    private static final Logger log = LoggerFactory.getLogger(ProcessarPdfService.class);

    private final NfePathConfig paths;
    private final NotaFiscalRepository repository;
    private final ArquivoNfeService arquivoNfeService;

    public ProcessarPdfService(
            NfePathConfig paths,
            NotaFiscalRepository repository,
            ArquivoNfeService arquivoNfeService
    ) {
        this.paths = paths;
        this.repository = repository;
        this.arquivoNfeService = arquivoNfeService;
    }

    public int processar() {
        int processados = 0;

        Path diretorioEntrada = paths.naoProcessadas();
        Path diretorioProcessadas = paths.processadas();
        Path diretorioErros = paths.erros();

        try {
            Files.createDirectories(diretorioProcessadas);
            Files.createDirectories(diretorioErros);

            try (Stream<Path> arquivos = Files.list(diretorioEntrada)) {
                List<Path> pdfs = arquivos
                        .filter(p -> p.toString().toLowerCase().endsWith(".pdf"))
                        .toList();

                log.info("Encontrados {} arquivos PDF para processar", pdfs.size());

                for (Path pdf : pdfs) {
                    UUID arquivoId = null;
                    log.info("Processando PDF: {}", pdf.getFileName());

                    try {
                        arquivoId = arquivoNfeService.registrarProcessando(pdf);

                        NotaFiscal nota = extrairDadosDoPDF(pdf);
                        validarNotaExtraida(nota);

                        if (repository.existeNotaComChave(nota.getChaveAcesso())) {
                            log.warn("Nota com chave {} ja existe no sistema", nota.getChaveAcesso());
                            arquivoNfeService.marcarProcessado(arquivoId, null);
                            moverParaProcessados(pdf, diretorioProcessadas);
                            continue;
                        }

                        nota.setId(UUID.randomUUID().toString());
                        nota.setArquivoOrigem(pdf.getFileName().toString());

                        repository.salvar(nota);
                        arquivoNfeService.marcarProcessado(arquivoId, nota.getId());

                        log.info("Nota processada com sucesso: numero={}, chave={}", nota.getNumero(), nota.getChaveAcesso());
                        moverParaProcessados(pdf, diretorioProcessadas);
                        processados++;
                    } catch (Exception e) {
                        log.error("Erro ao processar arquivo {}: {}", pdf.getFileName(), e.getMessage(), e);
                        arquivoNfeService.registrarErro(arquivoId, pdf, e.getMessage());
                        moverParaErros(pdf, diretorioErros);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Erro geral ao processar PDFs", e);
            throw new RuntimeException("Erro ao processar PDFs", e);
        }

        log.info("Processamento concluido. Total de PDFs processados com sucesso: {}", processados);
        return processados;
    }

    private void validarNotaExtraida(NotaFiscal nota) {
        if (nota.getChaveAcesso() == null || nota.getChaveAcesso().isBlank()) {
            throw new IllegalArgumentException("Chave de acesso nao encontrada no PDF");
        }
        if (nota.getDataEmissao() == null) {
            throw new IllegalArgumentException("Data de emissao nao encontrada no PDF");
        }
        if (nota.getEmitenteNome() == null || nota.getEmitenteNome().isBlank()) {
            throw new IllegalArgumentException("Nome do emitente nao encontrado no PDF");
        }
        if (nota.getEmitenteCnpjCpf() == null || nota.getEmitenteCnpjCpf().isBlank()) {
            throw new IllegalArgumentException("Documento do emitente nao encontrado no PDF");
        }
        if (nota.getTomadorNome() == null || nota.getTomadorNome().isBlank()) {
            throw new IllegalArgumentException("Nome do tomador nao encontrado no PDF");
        }
        if (nota.getTomadorCnpjCpf() == null || nota.getTomadorCnpjCpf().isBlank()) {
            throw new IllegalArgumentException("Documento do tomador nao encontrado no PDF");
        }
    }

    private void moverParaErros(Path pdf, Path diretorioErros) {
        try {
            Path destinoErro = diretorioErros.resolve(pdf.getFileName());
            Files.move(pdf, destinoErro, StandardCopyOption.REPLACE_EXISTING);
            log.info("PDF movido para diretorio de erros: {}", destinoErro);
        } catch (IOException ioException) {
            log.error("Falha ao mover PDF para diretorio de erros", ioException);
        }
    }
}
