package models;

import gals.Token;

/**
 *
 * @author Vinícius Ferneda de Lima
 */
public class Identificador {

    private Token token;
    private TipoID tipo;
    private int id;

    public int getId() {
        return id;
    }

    public Identificador(Token token, TipoID tipo, int id) {
         this.token = token;
         this.tipo = tipo;
         this.id = id;
    }

    public int getColuna() {
        return token.getPosition();
    }

    public TipoID getTipo() {
        return tipo;
    }

    public String getNome() {
        return token.getLexeme();
    }
}
