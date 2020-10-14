package com.projeto.camfexpress.model;

public class Validacoes {
    public boolean numero(String numero){
        String validanumero = numero.replace( "-" , "")
                .replace( "(" , "")
                .replace( ")" , "")
                .replace( " " , "");
        boolean validar = numero.matches(".((10)|([1-9][1-9]).)[9][0-9]{4}-[0-9]{4}");

        if(validanumero.equals("") == true || validanumero.length() != 11 || validar == false){
            return false;
        }

        return true;
    }

    public boolean valorENulo(String valor){
        if(valor.equals("")){
            return false;
        }

        return true;
    }

    public boolean verificarEmail(String email){
        boolean verificar = email.matches("^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$");

        if (email != null && email.length() > 0 && verificar == true) {
            return true;
        }

        return true;
    }

    public boolean verificarCEP(String cep){
        boolean validaCep = cep.matches("\\d{5}-\\d{3}");

        if(cep.equals("") || cep.length() != 8 && validaCep == false){
            return false;
        }

        return true;
    }

    public boolean validarCPFCNPJ(String cpfcnpj){
        String cpfcnpjSimples = cpfcnpj.replace("-", "").replace(".", "").replace("/", "");
        int cpfcnpjLength = cpfcnpjSimples.length();
        boolean validando = false;

        if(cpfcnpjLength == 14){
            if (cpfcnpjSimples.equals("00000000000000") ||
                cpfcnpjSimples.equals("11111111111111") ||
                cpfcnpjSimples.equals("22222222222222") ||
                cpfcnpjSimples.equals("33333333333333") ||
                cpfcnpjSimples.equals("44444444444444") ||
                cpfcnpjSimples.equals("55555555555555") ||
                cpfcnpjSimples.equals("66666666666666") ||
                cpfcnpjSimples.equals("77777777777777") ||
                cpfcnpjSimples.equals("88888888888888") ||
                cpfcnpjSimples.equals("99999999999999") ||
                cpfcnpjSimples.equals("")){
                validando = false;
            }

            char dig13, dig14;
            int sm, i, r, num, peso;

            sm = 0;
            peso = 2;

            for (i=11; i>=0; i--) {
                num = (int)(cpfcnpjSimples.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig13 = '0';
            else dig13 = (char)((11-r) + 48);

            sm = 0;
            peso = 2;
            for (i=12; i>=0; i--) {
                num = (int)(cpfcnpjSimples.charAt(i)- 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig14 = '0';
            else dig14 = (char)((11-r) + 48);

            if ((dig13 == cpfcnpjSimples.charAt(12)) && (dig14 == cpfcnpjSimples.charAt(13)))
                validando = true;
            else validando = false;

        }else if(cpfcnpjLength == 11){
            if (cpfcnpjSimples.equals("00000000000") ||
                cpfcnpjSimples.equals("11111111111") ||
                cpfcnpjSimples.equals("22222222222") ||
                cpfcnpjSimples.equals("33333333333") ||
                cpfcnpjSimples.equals("44444444444") ||
                cpfcnpjSimples.equals("55555555555") ||
                cpfcnpjSimples.equals("66666666666") ||
                cpfcnpjSimples.equals("77777777777") ||
                cpfcnpjSimples.equals("88888888888") ||
                cpfcnpjSimples.equals("99999999999") ||
                cpfcnpjSimples.equals("")){
                validando = false;
            }

            char dig10, dig11;
            int sm, i, r, num, peso;
                sm = 0;
                peso = 10;
                for (i=0; i<9; i++) {
                    num = (int)(cpfcnpjSimples.charAt(i) - 48);
                    sm = sm + (num * peso);
                    peso = peso - 1;
                }

                r = 11 - (sm % 11);
                if ((r == 10) || (r == 11))
                    dig10 = '0';
                else dig10 = (char)(r + 48);

                sm = 0;
                peso = 11;
                for(i=0; i<10; i++) {
                    num = (int)(cpfcnpjSimples.charAt(i) - 48);
                    sm = sm + (num * peso);
                    peso = peso - 1;
                }

                r = 11 - (sm % 11);
                if ((r == 10) || (r == 11))
                    dig11 = '0';
                else dig11 = (char)(r + 48);

                if ((dig10 == cpfcnpjSimples.charAt(9)) && (dig11 == cpfcnpjSimples.charAt(10)))
                    validando = true;
                else validando = false;
        }else{
            return false;
        }

        return validando;
    }

    public boolean validarData(String dataTexto){
        String[] datasSeparadas = dataTexto.split("/");
        int dia = Integer.parseInt(datasSeparadas[0]);
        int mes = Integer.parseInt(datasSeparadas[1]);
        int ano = Integer.parseInt(datasSeparadas[2]);

        if((dia >= 1 && dia <= 31) && (mes >= 1 && mes <= 12) && (ano >= 1900 && ano <= 2020)){
            return true;
        }

        return false;
    }
}
