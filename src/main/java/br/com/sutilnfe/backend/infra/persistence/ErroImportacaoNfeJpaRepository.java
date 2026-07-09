package br.com.sutilnfe.backend.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ErroImportacaoNfeJpaRepository extends JpaRepository<ErroImportacaoNfeEntity, UUID> {
}
