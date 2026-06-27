package br.com.sutilnfe.backend.application;

import br.com.sutilnfe.backend.domain.NotaFiscal;
import br.com.sutilnfe.backend.infra.filesystem.NotaFiscalJsonRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class NotaFiscalService {

    private final NotaFiscalJsonRepository repository;

    public NotaFiscalService(NotaFiscalJsonRepository repository) {
        this.repository = repository;
    }

    public List<NotaFiscal> listar(String ano) {
        return repository.listarTodas(Integer.parseInt(ano));
    }

    public Map<String, Long> anosDir(){
        return repository.getJsonsDir();
    }

    public void salvar(NotaFiscal nota) throws IOException {
        repository.salvar(nota);
    }

    public void deletar(NotaFiscal nota) {
        repository.delete(nota);
    }

    public NotaFiscal findNota(String id, int ano) {
        var optionalNota = repository.getNotaById(id, ano);
        return optionalNota.orElse(null);
    }
}