package br.com.sutilnfe.backend.infra.persistence;

final class PessoaNormalizer {

    private PessoaNormalizer() {
    }

    static String normalizarDocumento(String documento) {
        if (documento == null) {
            return null;
        }
        String digitos = documento.replaceAll("\\D", "");
        return digitos.isBlank() ? documento.trim() : digitos;
    }

    static String tipoDocumento(String documentoNormalizado) {
        if (documentoNormalizado == null) {
            return null;
        }
        return switch (documentoNormalizado.length()) {
            case 11 -> "CPF";
            case 14 -> "CNPJ";
            default -> "OUTRO";
        };
    }

    static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
