package br.com.sutilnfe.backend.application;

import br.com.sutilnfe.backend.domain.NotaFiscal;
import br.com.sutilnfe.backend.domain.NotaFiscalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotaFiscalService {

    private final NotaFiscalRepository repository;

    public NotaFiscalService(NotaFiscalRepository repository) {
        this.repository = repository;
    }

    public List<NotaFiscal> listar(String ano) {
        return repository.listarTodas(Integer.parseInt(ano));
    }

    public Map<String, Long> anosDir(){
        return repository.getAnosComQuantidade();
    }

    public void salvar(NotaFiscal nota) {
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
