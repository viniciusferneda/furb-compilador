package models;

import gals.Constants;
import gals.LexicalError;
import gals.Lexico;
import gals.SemanticError;
import gals.Semantico;
import gals.Sintatico;
import gals.SyntaticError;
import gals.Token;

/**
 * @author Vinicius Ferneda de Lima
 */
public class Compilador {

    private Lexico lexico;
    private Sintatico sintatico;
    
    public Compilador() {
        this.lexico = new Lexico();
        this.sintatico = new Sintatico();
    }
    
    /**
     * Realiza a validação lexica do código
     * @param codigoCompilar
     * @return 
     * @throws LexicalErrorAdapter
     * @throws Exception 
     */
    public String compilar(String codigoCompilar) throws LexicalErrorAdapter, Exception {
        StringBuilder codigoGerado = new StringBuilder();
        
        try {
            
            lexico.setInput(new java.io.StringReader(codigoCompilar));
            
            sintatico.parse(lexico, new Semantico(codigoGerado));

        } catch (LexicalError ex) {
            int linha = getLinha(codigoCompilar, ex.getPosition());                    
            String msg;

            if (ex.getMessage().toUpperCase().contains("CARACTERE")) {
                msg = "Erro na linha " + linha + " - " + codigoCompilar.charAt(ex.getPosition()) + " símbolo inválido";
            } else if (ex.getMessage().toUpperCase().contains("PALAVRARESERVADA")) {
                msg = "Erro na linha " + linha + " - " + recuperaPalavra(codigoCompilar, ex.getPosition()) + " palavra reservada inválida";
            } else if (ex.getMessage().toUpperCase().contains("CONSTLIT")) {
                msg = "Erro na linha " + linha + " - constante literal inválida ou não finalizada";
            } else if (ex.getMessage().toUpperCase().contains("CONSTNUM")) {
                msg = "Erro na linha " + linha + " - constante numérica inválida";
            } else if (ex.getMessage().toUpperCase().contains("IDENTIFICADOR")) {
                msg = "Erro na linha " + linha + " - " + recuperaPalavra(codigoCompilar, ex.getPosition()) + " identificador inválido";
            } else {
                msg = "Erro na linha " + linha + " – comentário de bloco não finalizado";
            }

            throw new LexicalErrorAdapter(msg, ex, linha);
        } catch (SyntaticError ex) {
            int linha = getLinha(codigoCompilar, ex.getPosition());
            
            String strClasse = getStrClasseToken(ex.getToken());
            
            String msg = "Erro na linha " + linha + ": ";
            if (ex.getToken().getLexeme().equals("$")) {
                msg += SyntaticErrorAdapter.trataMensagem("encontrado fim do programa, {0}.", ex.getMessage());
            } else {
                msg += SyntaticErrorAdapter.trataMensagem("encontrado {0} {2}.",ex.getToken().getLexeme(),strClasse,ex.getMessage());
            }                
            throw new SyntaticErrorAdapter(msg, ex, linha);
        } catch (SemanticError ex) {   
            int linha = getLinha(codigoCompilar, ex.getPosition());
            
            String msg = "Erro na linha " + linha + ": " + ex.getMessage();
            
            throw new SemanticErrorAdapter(msg, ex, linha); 
        }
        
        return codigoGerado.toString();
    }
    
    private String recuperaPalavra(String linha, int posicaoInicial){
        String palavra = "";
        
        for (int i = posicaoInicial; i < linha.length(); i++) {
            if(!" ".equals(String.valueOf(linha.charAt(i)))){
                palavra += linha.charAt(i);
            }else{
                break;
            }            
        }
        
        return palavra;
    }

    private int getLinha(String codigoFonte, int posicaoCaracter) {
        String texto = codigoFonte.substring(0, posicaoCaracter);
        String quebraLinha = "\n";
        String[] linhas = texto.split(quebraLinha);
        return linhas.length;
    }

    private String getStrClasseToken(Token token) {
        ClasseID classe = null;
        switch (token.getId()) {
            case Constants.t_palavraReservada:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_and:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_array:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_character:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_do:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_endDo:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_exit:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_false:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_if:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_ifFalseDo:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_ifTrueDo:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_logical:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_not:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_number:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_or:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_read:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_round:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_true:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_trunk:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_write:
                classe = ClasseID.palavraReservada;
                break;
            case Constants.t_identificador:
                classe = ClasseID.identificador;
                break;
            case Constants.t_constNum:
                classe = ClasseID.constanteNumerica;
                break;
            case Constants.t_constLit:
                classe = ClasseID.constanteLiteral;
                break;
            case Constants.t_TOKEN_25:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_26:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_27:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_28:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_29:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_30:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_31:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_32:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_33:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_34:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_35:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_36:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_37:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_38:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_39:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_40:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_41:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_42:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_43:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_44:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_45:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_46:
                classe = ClasseID.simboloEspecial;
                break;
            case Constants.t_TOKEN_47:
                classe = ClasseID.simboloEspecial;
                break;
        }  
        
        if (classe == null){
            return "";
        }
        
        return classe.getDescricao();
    }

    private String preencheEspaco(int stringSize, int qtdDesejada){
        String vazio = "";
        for (int i = 0; i < qtdDesejada-stringSize; i++) {
            vazio += " ";
        }
        return vazio;
    }
}
