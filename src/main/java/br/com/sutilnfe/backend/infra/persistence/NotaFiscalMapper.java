package br.com.sutilnfe.backend.infra.persistence;

import br.com.sutilnfe.backend.domain.NotaFiscal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class NotaFiscalMapper {

    public NotaFiscal toDomain(NotaFiscalEntity entity) {
        NotaFiscal nota = new NotaFiscal();
        nota.setId(entity.getId().toString());
        nota.setNumero(entity.getNumero());
        nota.setChaveAcesso(entity.getChaveAcesso());
        nota.setCompetencia(entity.getCompetencia());
        nota.setDataEmissao(entity.getDataEmissao());
        nota.setDescricaoServico(entity.getDescricaoServico());
        nota.setValorServico(entity.getValorServico());
        nota.setValorLiquido(entity.getValorLiquido());
        nota.setCodigoTributacao(entity.getCodigoTributacao());
        nota.setMunicipioPrestacao(entity.getMunicipioPrestacao());
        nota.setArquivoOrigem(entity.getArquivoOrigem());
        nota.setSplitPj(entity.getSplitPj());
        nota.setIncremento(entity.getIncremento());

        PessoaEntity emitente = entity.getEmitente();
        if (emitente != null) {
            nota.setEmitenteNome(emitente.getNome());
            nota.setEmitenteCnpjCpf(emitente.getDocumento());
        }

        PessoaEntity tomador = entity.getTomador();
        if (tomador != null) {
            nota.setTomadorNome(tomador.getNome());
            nota.setTomadorCnpjCpf(tomador.getDocumento());
        }

        return nota;
    }

    public void copyToEntity(NotaFiscal nota, NotaFiscalEntity entity, PessoaEntity emitente, PessoaEntity tomador) {
        entity.setId(UUID.fromString(nota.getId()));
        entity.setNumero(nota.getNumero());
        entity.setChaveAcesso(nota.getChaveAcesso());
        entity.setCompetencia(nota.getCompetencia());
        entity.setDataEmissao(nota.getDataEmissao());
        entity.setEmitente(emitente);
        entity.setTomador(tomador);
        entity.setDescricaoServico(nota.getDescricaoServico());
        entity.setValorServico(defaultValue(nota.getValorServico()));
        entity.setValorLiquido(defaultValue(nota.getValorLiquido()));
        entity.setCodigoTributacao(nota.getCodigoTributacao());
        entity.setMunicipioPrestacao(nota.getMunicipioPrestacao());
        entity.setArquivoOrigem(nota.getArquivoOrigem());
        entity.setSplitPj(nota.getSplitPj() == null ? 60 : nota.getSplitPj());
        entity.setIncremento(defaultValue(nota.getIncremento()));
    }

    private BigDecimal defaultValue(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
