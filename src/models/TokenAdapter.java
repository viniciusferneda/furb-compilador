package models;

import gals.Token;


/**
 *
 * @author Vinícius Ferneda de Lima
 */
public class TokenAdapter  {
    private Token token;
    private int linha;
    private ClasseID classe;

    public TokenAdapter(Token token, int linha, ClasseID classe) {
         this.token = token;
         this.linha = linha;
         this.classe = classe;
    }

    public int getColuna() {
        return token.getPosition();
    }

    public int getLinha() {
        return linha;
    }

    public ClasseID getClasse() {
        return classe;
    }

    public String getLexeme() {
        return token.getLexeme();
    }

    




}
