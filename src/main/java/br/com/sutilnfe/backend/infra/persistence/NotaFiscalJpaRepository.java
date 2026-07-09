package br.com.sutilnfe.backend.infra.persistence;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotaFiscalJpaRepository extends JpaRepository<NotaFiscalEntity, UUID> {

    @EntityGraph(attributePaths = {"emitente", "tomador"})
    List<NotaFiscalEntity> findByAnoEmissaoOrderByDataEmissaoDescCreatedAtDesc(Integer anoEmissao);

    @EntityGraph(attributePaths = {"emitente", "tomador"})
    Optional<NotaFiscalEntity> findByIdAndAnoEmissao(UUID id, Integer anoEmissao);

    @EntityGraph(attributePaths = {"emitente", "tomador"})
    Optional<NotaFiscalEntity> findByChaveAcesso(String chaveAcesso);

    boolean existsByChaveAcesso(String chaveAcesso);

    @Query("""
            select n.anoEmissao as ano, count(n) as total
            from NotaFiscalEntity n
            group by n.anoEmissao
            order by n.anoEmissao desc
            """)
    List<AnoQuantidadeProjection> countByAnoEmissao();

    @Query("select n from NotaFiscalEntity n where n.id = :id")
    @EntityGraph(attributePaths = {"emitente", "tomador"})
    Optional<NotaFiscalEntity> findByIdWithPessoas(@Param("id") UUID id);
}
