package br.com.sutilnfe.backend.application;

import br.com.sutilnfe.backend.domain.NotaFiscal;
import br.com.sutilnfe.backend.infra.filesystem.NfePathConfig;
import br.com.sutilnfe.backend.infra.filesystem.NotaFiscalJsonRepository;
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
    private final NotaFiscalJsonRepository repository;


    public ProcessarPdfService(
            NfePathConfig paths,
            NotaFiscalJsonRepository repository
    ) {
        this.paths = paths;
        this.repository = repository;
    }

    public int processar() {
        int processados = 0;

        Path diretorioEntrada = paths.naoProcessadas();
        Path diretorioProcessadas = paths.processadas();
        Path diretorioErros = paths.processadas().resolveSibling("erros");

        try {
            Files.createDirectories(diretorioProcessadas);
            Files.createDirectories(diretorioErros);

            try (Stream<Path> arquivos = Files.list(diretorioEntrada)) {
                List<Path> pdfs = arquivos
                        .filter(p -> p.toString().toLowerCase().endsWith(".pdf"))
                        .toList();

                log.info("Encontrados {} arquivos PDF para processar", pdfs.size());

                for (Path pdf : pdfs) {
                    log.info("📄 Processando PDF: {}", pdf.getFileName());

                    try {
                        // Extrair dados do PDF
                        NotaFiscal nota = extrairDadosDoPDF(pdf);

                        // Validar dados mínimos
                        if (nota.getChaveAcesso() == null || nota.getChaveAcesso().isEmpty()) {
                            throw new IllegalArgumentException("Chave de acesso não encontrada no PDF");
                        }

                        // Verificar se já existe
                        int year = nota.getDataEmissao().getYear();
                        if (repository.existeNotaComChave(nota.getChaveAcesso(), year)) {
                            log.warn("Nota com chave {} já existe no sistema", nota.getChaveAcesso());
                            moverParaProcessados(pdf, diretorioProcessadas);
                            continue;
                        }

                        // Configurar dados adicionais
                        nota.setId(UUID.randomUUID().toString());
                        nota.setArquivoOrigem(pdf.getFileName().toString());

                        // Salvar como JSON
                        repository.salvar(nota);

                        log.info("✅ Nota processada com sucesso:");
                        log.info("   Número: {}", nota.getNumero());
                        log.info("   Chave: {}", nota.getChaveAcesso());
                        log.info("   Emitente: {}", nota.getEmitenteNome());
                        log.info("   Tomador: {}", nota.getTomadorNome());
                        log.info("   Valor: R$ {}", nota.getValorServico());

                        // Mover arquivo PDF processado
                        moverParaProcessados(pdf, diretorioProcessadas);

                        processados++;

                    } catch (Exception e) {
                        log.error("❌ Erro ao processar arquivo {}: {}", pdf.getFileName(), e.getMessage());
                        e.printStackTrace();

                        // Mover para diretório de erros
                        try {
                            Path destinoErro = diretorioErros.resolve(pdf.getFileName());
                            Files.move(pdf, destinoErro, StandardCopyOption.REPLACE_EXISTING);
                            log.info("📂 PDF movido para diretório de erros: {}", destinoErro);
                        } catch (IOException ioException) {
                            log.error("❌ Falha ao mover PDF para diretório de erros", ioException);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ Erro geral ao processar PDFs", e);
            throw new RuntimeException("Erro ao processar PDFs", e);
        }

        log.info("✅ Processamento concluído. Total de PDFs processados com sucesso: {}", processados);
        return processados;
    }

}

