package models;

import gals.SemanticError;

/**
 *
 * @author Vinícius Ferneda de Lima
 */
public class SemanticErrorAdapter extends Exception {
    private SemanticError erro;
    private int linha;    

    public SemanticErrorAdapter(String msg, SemanticError erro, int linha) {        
        super(msg);        
        this.erro = erro;
        this.linha = linha;
    }
    
    public int getColuna() {
        if (erro == null)
            return -1;
        return erro.getPosition();
    }

    public int getLinha() {
        return linha;
    }
}
