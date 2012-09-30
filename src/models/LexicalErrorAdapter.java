package models;

import gals.LexicalError;

/**
 *
 * @author Vinicius Ferneda de Lima
 */
public class LexicalErrorAdapter extends Exception{
    
    private LexicalError erro;
    private int linha;

    public LexicalErrorAdapter(String msg, LexicalError erro, int linha) {
        super(msg);
        this.erro = erro;
        this.linha = linha;
    }

    public int getColuna() {
        if (erro == null){
            return -1;
        }
        return erro.getPosition();
    }

    public int getLinha() {
        return linha;
    }
    
}
