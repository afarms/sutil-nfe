package br.com.sutilnfe.backend.api;

import br.com.sutilnfe.backend.infra.filesystem.NfePathConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SystemController {

    private final NfePathConfig paths;

    public SystemController(NfePathConfig paths) {
        this.paths = paths;
    }

    @GetMapping("/api/status")
    public Map<String, Object> status() {
        return Map.of(
                "status", "ONLINE",
                "basePath", paths.baseDir().toAbsolutePath().toString(),
                "pastas", Map.of(
                        "naoprocessada", paths.naoProcessadas().toString(),
                        "processada", paths.processadas().toString(),
                        "erros", paths.erros().toString()
                )
        );
    }
}
