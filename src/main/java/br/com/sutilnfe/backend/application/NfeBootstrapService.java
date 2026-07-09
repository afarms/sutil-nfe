package br.com.sutilnfe.backend.application;

import br.com.sutilnfe.backend.infra.filesystem.NfePathConfig;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
public class NfeBootstrapService {

    private static final Logger log = LoggerFactory.getLogger(NfeBootstrapService.class);

    private final NfePathConfig paths;

    public NfeBootstrapService(NfePathConfig paths) {
        this.paths = paths;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(paths.naoProcessadas());
            Files.createDirectories(paths.processadas());
            Files.createDirectories(paths.erros());

            log.info("Estrutura de diretorios criada com sucesso");
            log.info("   Nao processadas: {}", paths.naoProcessadas());
            log.info("   Processadas: {}", paths.processadas());
            log.info("   Erros: {}", paths.erros());
        } catch (IOException e) {
            throw new RuntimeException("Falha ao criar estrutura NFEs", e);
        }
    }
}
