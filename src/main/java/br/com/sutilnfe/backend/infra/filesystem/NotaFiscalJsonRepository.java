package br.com.sutilnfe.backend.infra.filesystem;

import br.com.sutilnfe.backend.domain.NotaFiscal;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class NotaFiscalJsonRepository {

    private final NfePathConfig paths;
    private final ObjectMapper mapper;

    public NotaFiscalJsonRepository(NfePathConfig paths, ObjectMapper mapper) {
        this.paths = paths;
        this.mapper = mapper;
    }

    public void salvar(NotaFiscal nota) throws IOException {
        String nomeArquivo = gerarNomeArquivo(nota);

        int ano = nota.getDataEmissao().getYear();

        Path pastaAno = paths.jsonsByYear(ano);

        // cria o diretório se não existir
        Files.createDirectories(pastaAno);

        Path arquivoJson = pastaAno.resolve(nomeArquivo);

        String json = mapper.writeValueAsString(nota);
        Files.writeString(arquivoJson, json);

        System.out.println("✅ JSON salvo em: " + arquivoJson.toAbsolutePath());
    }


    public List<NotaFiscal> listarTodas(int ano) {
        List<NotaFiscal> notas = new ArrayList<>();
        Path pastaAno = paths.jsonsByYear(ano);

        try (Stream<Path> arquivos = Files.list(pastaAno)) {
            arquivos
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(p -> {
                        NotaFiscal nf = mapper.readValue(p.toFile(), NotaFiscal.class);
                        notas.add(nf);
                    });
        } catch (IOException e) {
            throw new RuntimeException("Erro ao listar JSONs de notas fiscais", e);
        }

        return notas.reversed();
    }

    public Map<String, Long> getJsonsDir() {
        try (Stream<Path> dirs = Files.list(paths.jsons())) {
            return dirs
                    .filter(Files::isDirectory)
                    .collect(Collectors.toMap(
                            dir -> dir.getFileName().toString(),
                            this::countJsonFiles
                    ));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao listar diretórios de JSONs", e);
        }
    }

    private long countJsonFiles(Path dir) {
        try (Stream<Path> arquivos = Files.list(dir)) {
            return (int) arquivos
                    .filter(p -> p.toString().endsWith(".json"))
                    .count();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler diretório: " + dir, e);
        }
    }

    private String gerarNomeArquivo(NotaFiscal nota) {
        if (nota.getNumero() != null && !nota.getNumero().trim().isEmpty()) {
            return "nota_" + nota.getNumero() + "_" + nota.getId() + ".json";
        } else if (nota.getChaveAcesso() != null && !nota.getChaveAcesso().trim().isEmpty()) {
            return "nota_" + nota.getChaveAcesso().substring(Math.max(0, nota.getChaveAcesso().length() - 10)) + ".json";
        } else {
            return "nota_" + nota.getId() + ".json";
        }
    }

    // Método para verificar se uma nota já existe
    public boolean existeNotaComChave(String chaveAcesso, int ano) {
        try (Stream<Path> arquivos = Files.list(paths.jsonsByYear(ano))) {
            return arquivos
                    .filter(p -> p.toString().endsWith(".json"))
                    .anyMatch(arquivo -> {
                        try {
                            String json = Files.readString(arquivo);
                            NotaFiscal nota = mapper.readValue(json, NotaFiscal.class);
                            return chaveAcesso.equals(nota.getChaveAcesso());
                        } catch (IOException e) {
                            return false;
                        }
                    });
        } catch (IOException e) {
            return false;
        }
    }

    public Optional<NotaFiscal> getNotaById(String id, int ano){
        Path pastaAno = paths.jsonsByYear(ano);
        try (Stream<Path> arquivos = Files.list(pastaAno)) {
            return arquivos
                    .filter(p -> p.toString().endsWith(".json"))
                    .map(arquivo -> {
                        try {
                            String json = Files.readString(arquivo);
                            return mapper.readValue(json, NotaFiscal.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(nota -> id.equals(nota.getId()))
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public Optional<NotaFiscal> obterNotaFiscal(String chaveAcesso, int year) throws IOException {
        try (Stream<Path> arquivos = Files.list(paths.jsonsByYear(year))) {
            return arquivos
                    .filter(p -> p.toString().endsWith(".json"))
                    .map(arquivo -> {
                        try {
                            String json = Files.readString(arquivo);
                            return mapper.readValue(json, NotaFiscal.class);
                        } catch (IOException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(nota -> chaveAcesso.equals(nota.getChaveAcesso()))
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public void delete(NotaFiscal nota) {
        System.out.println(nota.toString());
        String chaveAcesso = nota.getChaveAcesso();
        if (!existeNotaComChave(chaveAcesso, nota.getDataEmissao().getYear())) {
            throw new RuntimeException("Nota fiscal não encontrada para exclusão");
        }

        int year = nota.getDataEmissao().getYear();
        try (Stream<Path> arquivos = Files.list(paths.jsonsByYear(year))) {
            Optional<Path> arquivoNota = arquivos
                    .filter(p -> p.toString().endsWith(".json"))
                    .filter(arquivo -> {
                        try {
                            String json = Files.readString(arquivo);
                            NotaFiscal nf = mapper.readValue(json, NotaFiscal.class);
                            return chaveAcesso.equals(nf.getChaveAcesso());
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .findFirst();

            if (arquivoNota.isEmpty()) {
                throw new RuntimeException("Arquivo JSON da nota fiscal não encontrado");
            }

            Files.delete(arquivoNota.get());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar JSON da nota fiscal", e);
        }
    }
}
