package models;

import gals.SyntaticError;

/**
 * @author Vinicius Ferneda de Lima
 */
public class SyntaticErrorAdapter extends Exception{
    private SyntaticError erro;
    private int linha;    

    public SyntaticErrorAdapter(String msg, SyntaticError erro, int linha) {        
        super(msg);        
        this.erro = erro;
        this.linha = linha;
    }
    
    public static String trataMensagem(String msg, String... parametros) {
        if (parametros != null) { 
            for (int i = 0; i < parametros.length; i++) {                                  
                msg = msg.replace("{"+i+"}", parametros[i]);
            }
        }
        return msg;
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
