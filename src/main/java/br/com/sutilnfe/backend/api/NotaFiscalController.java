package br.com.sutilnfe.backend.api;

import br.com.sutilnfe.backend.api.dto.NotaRequest;
import br.com.sutilnfe.backend.application.NotaFiscalService;
import br.com.sutilnfe.backend.application.ProcessarPdfService;
import br.com.sutilnfe.backend.domain.NotaFiscal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notas")
@CrossOrigin
public class NotaFiscalController {
    private final Logger logger = LoggerFactory.getLogger(NotaFiscalController.class);

    private final NotaFiscalService service;
    private final ProcessarPdfService processarPdfService;

    public NotaFiscalController(
            NotaFiscalService service,
            ProcessarPdfService processarPdfService
    ) {
        this.service = service;
        this.processarPdfService = processarPdfService;
    }

    @GetMapping("{ano}")
    public List<NotaFiscal> listar(@PathVariable String ano) {
        return service.listar(ano);
    }

    @GetMapping("/anos")
    public Map<String, Long> anos() {
        return service.anosDir();
    }

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody NotaFiscal nota) throws IOException {
        service.salvar(nota);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/atualizar")
    public ResponseEntity<Void> atualizar(@RequestBody NotaRequest nota) throws IOException {
        logger.info("Atualizando nota {}", nota);
        var oldNota = service.findNota(nota.id(), nota.getAno());

        if (oldNota == null) {
            logger.warn("Nenhuma nota foi encontrada para a request: {}", nota);
            return ResponseEntity.notFound().build();
        }

        service.deletar(oldNota);

        oldNota.setIncremento(nota.incremento());
        oldNota.setSplitPj(nota.splitPj());
        oldNota.setNumero(nota.numero());
        oldNota.setDescricaoServico(nota.descricao());

        service.salvar(oldNota);
        logger.info("Nota {} atualizada com sucesso", nota);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/processar-pdfs")
    public String processarPdfs() {
        int total = processarPdfService.processar();
        return "PDFs processados: " + total;
    }

    @DeleteMapping("/{year}/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int year, @PathVariable String id) {
        var nota = service.findNota(id, year);
        if (nota != null) {
            service.deletar(nota);
            return ResponseEntity.ok().build();
        }
        //Error message
        logger.error("Nota {} não localizada", id);
        return ResponseEntity.notFound().build();
    }
}
