package br.com.sutilnfe.backend.infra.filesystem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class NfePathConfig {

    @Value("${nfe.base-path:NFEs}")
    private String basePath;

    public Path baseDir() {
        return Paths.get(basePath);
    }

    public Path naoProcessadas() {
        return baseDir().resolve("naoprocessada");
    }

    public Path processadas() {
        return baseDir().resolve("processada");
    }

    public Path jsons() {
        return baseDir().resolve("arquivos_json");
    }

    public Path erros() {
        return baseDir().resolve("erros");
    }

    public Path jsonsByYear(int year){
        return this.jsons().resolve(String.valueOf(year));
    }
}