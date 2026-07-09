package br.com.sutilnfe.backend.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NotaFiscalRepository {

    void salvar(NotaFiscal nota);

    List<NotaFiscal> listarTodas(int ano);

    Map<String, Long> getAnosComQuantidade();

    boolean existeNotaComChave(String chaveAcesso);

    Optional<NotaFiscal> getNotaById(String id, int ano);

    Optional<NotaFiscal> obterNotaFiscal(String chaveAcesso);

    void delete(NotaFiscal nota);
}
