package br.com.sutilnfe.backend.infra.persistence;

import br.com.sutilnfe.backend.domain.NotaFiscal;
import br.com.sutilnfe.backend.domain.NotaFiscalRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class NotaFiscalPostgresRepository implements NotaFiscalRepository {

    private final NotaFiscalJpaRepository notaFiscalJpaRepository;
    private final PessoaJpaRepository pessoaJpaRepository;
    private final NotaFiscalMapper mapper;

    public NotaFiscalPostgresRepository(
            NotaFiscalJpaRepository notaFiscalJpaRepository,
            PessoaJpaRepository pessoaJpaRepository,
            NotaFiscalMapper mapper
    ) {
        this.notaFiscalJpaRepository = notaFiscalJpaRepository;
        this.pessoaJpaRepository = pessoaJpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void salvar(NotaFiscal nota) {
        validarNota(nota);
        if (nota.getId() == null || nota.getId().isBlank()) {
            nota.setId(UUID.randomUUID().toString());
        }

        PessoaEntity emitente = resolverPessoa(nota.getEmitenteNome(), nota.getEmitenteCnpjCpf(), "emitente");
        PessoaEntity tomador = resolverPessoa(nota.getTomadorNome(), nota.getTomadorCnpjCpf(), "tomador");

        UUID id = UUID.fromString(nota.getId());
        NotaFiscalEntity entity = notaFiscalJpaRepository.findById(id).orElseGet(NotaFiscalEntity::new);
        mapper.copyToEntity(nota, entity, emitente, tomador);
        notaFiscalJpaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotaFiscal> listarTodas(int ano) {
        return notaFiscalJpaRepository.findByAnoEmissaoOrderByDataEmissaoDescCreatedAtDesc(ano)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getAnosComQuantidade() {
        Map<String, Long> anos = new LinkedHashMap<>();
        notaFiscalJpaRepository.countByAnoEmissao()
                .forEach(row -> anos.put(String.valueOf(row.getAno()), row.getTotal()));
        return anos;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeNotaComChave(String chaveAcesso) {
        return notaFiscalJpaRepository.existsByChaveAcesso(chaveAcesso);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotaFiscal> getNotaById(String id, int ano) {
        return parseUuid(id)
                .flatMap(uuid -> notaFiscalJpaRepository.findByIdAndAnoEmissao(uuid, ano))
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotaFiscal> obterNotaFiscal(String chaveAcesso) {
        return notaFiscalJpaRepository.findByChaveAcesso(chaveAcesso)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void delete(NotaFiscal nota) {
        parseUuid(nota.getId()).ifPresent(notaFiscalJpaRepository::deleteById);
    }

    private PessoaEntity resolverPessoa(String nome, String documento, String papel) {
        String documentoNormalizado = PessoaNormalizer.normalizarDocumento(documento);
        if (PessoaNormalizer.isBlank(nome) || PessoaNormalizer.isBlank(documentoNormalizado)) {
            throw new IllegalArgumentException("Dados obrigatorios do " + papel + " nao foram extraidos");
        }

        PessoaEntity pessoa = pessoaJpaRepository.findByDocumento(documentoNormalizado)
                .orElseGet(PessoaEntity::new);
        pessoa.setNome(nome.trim());
        pessoa.setDocumento(documentoNormalizado);
        pessoa.setTipoDocumento(PessoaNormalizer.tipoDocumento(documentoNormalizado));
        return pessoaJpaRepository.save(pessoa);
    }

    private void validarNota(NotaFiscal nota) {
        if (nota.getDataEmissao() == null) {
            throw new IllegalArgumentException("Data de emissao nao encontrada no PDF");
        }
        if (PessoaNormalizer.isBlank(nota.getChaveAcesso())) {
            throw new IllegalArgumentException("Chave de acesso nao encontrada no PDF");
        }
    }

    private Optional<UUID> parseUuid(String id) {
        try {
            return Optional.of(UUID.fromString(id));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}
