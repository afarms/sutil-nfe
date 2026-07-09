package br.com.sutilnfe.backend.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PessoaJpaRepository extends JpaRepository<PessoaEntity, UUID> {

    Optional<PessoaEntity> findByDocumento(String documento);
}
