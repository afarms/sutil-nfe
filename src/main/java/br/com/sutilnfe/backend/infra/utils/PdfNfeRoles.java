package br.com.sutilnfe.backend.infra.utils;

import br.com.sutilnfe.backend.domain.NotaFiscal;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfNfeRoles {
    private static final Logger log = LoggerFactory.getLogger(PdfNfeRoles.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private PdfNfeRoles(){

    }

    public static NotaFiscal extrairDadosDoPDF(Path pdfPath) throws IOException {
        try (PDDocument document = PDDocument.load(pdfPath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(document);

            // DEBUG: Mostrar estrutura do texto
            log.debug("=== ESTRUTURA DO TEXTO EXTRAÍDO ===");
            String[] linhas = texto.split("\n");
            for (int i = 0; i < Math.min(linhas.length, 30); i++) {
                log.debug("Linha {}: [{}]", i, linhas[i].trim());
            }

            NotaFiscal nota = new NotaFiscal();
            nota.setNumero(extrairNumeroNota(texto));
            nota.setChaveAcesso(extrairChaveAcesso(texto));
            nota.setCompetencia(extrairCompetencia(texto));
            nota.setDataEmissao(extrairDataEmissao(texto));
            nota.setEmitenteNome(extrairEmitenteNome(texto));
            nota.setEmitenteCnpjCpf(extrairEmitenteCnpjCpf(texto));
            nota.setTomadorNome(extrairTomadorNome(texto));
            nota.setTomadorCnpjCpf(extrairTomadorCnpjCpf(texto));
            nota.setDescricaoServico(extrairDescricaoServico(texto));
            nota.setValorServico(extrairValorServico(texto));
            nota.setValorLiquido(extrairValorLiquido(texto));
            nota.setCodigoTributacao(extrairCodigoTributacao(texto));
            nota.setMunicipioPrestacao(extrairMunicipioPrestacao(texto));
            nota.setSplitPj(60); // padrão é ser 60% para PJ
            nota.setIncremento(BigDecimal.ZERO); // padrão é zero.

            // Log para verificar extração
            log.info("=== DADOS EXTRAÍDOS ===");
            log.info("Número: {}", nota.getNumero());
            log.info("Chave: {}", nota.getChaveAcesso());
            log.info("Emitente: {}", nota.getEmitenteNome());
            log.info("CNPJ Emitente: {}", nota.getEmitenteCnpjCpf());
            log.info("Tomador: {}", nota.getTomadorNome());
            log.info("CNPJ Tomador: {}", nota.getTomadorCnpjCpf());
            log.info("Valor: {}", nota.getValorServico());

            return nota;
        }
    }

    public static void moverParaProcessados(Path pdf, Path diretorioProcessadas) throws IOException {
        Path destino = diretorioProcessadas.resolve(pdf.getFileName());
        Files.move(pdf, destino, StandardCopyOption.REPLACE_EXISTING);
        log.debug("📂 PDF movido para: {}", destino);
    }

    // ========== MÉTODOS DE EXTRAÇÃO CORRIGIDOS ==========

    private static String extrairNumeroNota(String texto) {
        // Procura por "Número da NFS-e" seguido de quebra de linha e o número
        return extrairCampoComQuebraLinha(texto, "Número da NFS-e");
    }

    private static String extrairChaveAcesso(String texto) {
        // Versão 1: Captura qualquer sequência de dígitos após o rótulo
        Pattern pattern = Pattern.compile("Chave de Acesso da NFS-e\\s*\\n\\s*([0-9]+)");
        Matcher matcher = pattern.matcher(texto);

        if (matcher.find()) {
            String chave = matcher.group(1).trim();
            log.debug("Chave de acesso extraída ({} dígitos): {}", chave.length(), chave);

            // Validação do tamanho (opcional)
            if (chave.length() < 44) {
                log.warn("⚠️ Chave com menos de 44 dígitos: {}", chave);
            } else if (chave.length() > 50) {
                log.warn("⚠️ Chave com mais de 50 dígitos: {}", chave);
            }

            return chave;
        }

        // Versão 2: Fallback - procura por qualquer chave de acesso
        Pattern fallback = Pattern.compile("Chave de Acesso\\s*\\n\\s*([0-9]+)");
        Matcher fallbackMatcher = fallback.matcher(texto);

        if (fallbackMatcher.find()) {
            return fallbackMatcher.group(1).trim();
        }

        return null;
    }

    private static LocalDate extrairCompetencia(String texto) {
        String dataStr = extrairCampoComQuebraLinha(texto, "Competência da NFS-e");
        try {
            return dataStr != null ? LocalDate.parse(dataStr, DATE_FORMATTER) : null;
        } catch (Exception e) {
            log.warn("Erro ao parsear competência: {}", dataStr);
            return null;
        }
    }

    private static LocalDate extrairDataEmissao(String texto) {
        String dataStr = extrairCampoComQuebraLinha(texto, "Data e Hora da emissão da NFS-e");
        if (dataStr != null && dataStr.contains(" ")) {
            dataStr = dataStr.split(" ")[0]; // Pega apenas a data
        }
        try {
            return dataStr != null ? LocalDate.parse(dataStr, DATE_FORMATTER) : null;
        } catch (Exception e) {
            log.warn("Erro ao parsear data emissão: {}", dataStr);
            return null;
        }
    }

    private static String extrairEmitenteNome(String texto) {
        // Extrai da seção do EMITENTE
        String secaoEmitente = extrairSecao(texto, "EMITENTE DA NFS-e", "TOMADOR DO SERVIÇO");
        if (secaoEmitente != null) {
            return extrairCampoComQuebraLinha(secaoEmitente, "Nome / Nome Empresarial");
        }
        return null;
    }

    private static String extrairEmitenteCnpjCpf(String texto) {
        // Extrai da seção do EMITENTE
        String secaoEmitente = extrairSecao(texto, "EMITENTE DA NFS-e", "TOMADOR DO SERVIÇO");
        if (secaoEmitente != null) {
            return extrairCampoComQuebraLinha(secaoEmitente, "CNPJ / CPF / NIF");
        }
        return null;
    }

    private static String extrairTomadorNome(String texto) {
        // Extrai da seção do TOMADOR
        String secaoTomador = extrairSecao(texto, "TOMADOR DO SERVIÇO", "INTERMEDIÁRIO DO SERVIÇO");
        if (secaoTomador == null) {
            secaoTomador = extrairSecao(texto, "TOMADOR DO SERVIÇO", "SERVIÇO PRESTADO");
        }
        if (secaoTomador != null) {
            return extrairCampoComQuebraLinha(secaoTomador, "Nome / Nome Empresarial");
        }
        return null;
    }

    private static String extrairTomadorCnpjCpf(String texto) {
        // Extrai da seção do TOMADOR
        String secaoTomador = extrairSecao(texto, "TOMADOR DO SERVIÇO", "INTERMEDIÁRIO DO SERVIÇO");
        if (secaoTomador == null) {
            secaoTomador = extrairSecao(texto, "TOMADOR DO SERVIÇO", "SERVIÇO PRESTADO");
        }
        if (secaoTomador != null) {
            return extrairCampoComQuebraLinha(secaoTomador, "CNPJ / CPF / NIF");
        }
        return null;
    }

    private static String extrairDescricaoServico(String texto) {
        return extrairCampoComQuebraLinha(texto, "Descrição do Serviço");
    }

    private static BigDecimal extrairValorServico(String texto) {
        String valorStr = extrairCampoComQuebraLinha(texto, "Valor do Serviço");
        if (valorStr != null && valorStr.contains("R$")) {
            valorStr = valorStr.replace("R$", "").trim();
        }
        return converterParaBigDecimal(valorStr);
    }

    private static BigDecimal extrairValorLiquido(String texto) {
        String valorStr = extrairCampoComQuebraLinha(texto, "Valor Líquido da NFS-e");
        if (valorStr != null && valorStr.contains("R$")) {
            valorStr = valorStr.replace("R$", "").trim();
        }
        return converterParaBigDecimal(valorStr);
    }

    private static String extrairCodigoTributacao(String texto) {
        return extrairCampoComQuebraLinha(texto, "Código de Tributação Nacional");
    }

    private static String extrairMunicipioPrestacao(String texto) {
        return extrairCampoComQuebraLinha(texto, "Local da Prestação");
    }

    // ========== MÉTODOS AUXILIARES ==========

    private static String extrairCampoComQuebraLinha(String texto, String rotulo) {
        // Procura: "Rótulo\n Valor"
        Pattern pattern = Pattern.compile(Pattern.quote(rotulo) + "\\s*\\n\\s*([^\\n]+)");
        Matcher matcher = pattern.matcher(texto);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private static String extrairSecao(String texto, String inicioSecao, String fimSecao) {
        int inicio = texto.indexOf(inicioSecao);
        if (inicio == -1) return null;

        int fim = texto.indexOf(fimSecao, inicio + inicioSecao.length());
        if (fim == -1) return texto.substring(inicio);

        return texto.substring(inicio, fim);
    }

    private static BigDecimal converterParaBigDecimal(String valorStr) {
        if (valorStr == null || valorStr.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            // Remove pontos de milhar, substitui vírgula decimal por ponto
            String valorLimpo = valorStr.trim()
                    .replace("R$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .trim();
            return new BigDecimal(valorLimpo);
        } catch (NumberFormatException e) {
            log.warn("❌ Erro ao converter valor '{}' para BigDecimal", valorStr);
            return BigDecimal.ZERO;
        }
    }
}
