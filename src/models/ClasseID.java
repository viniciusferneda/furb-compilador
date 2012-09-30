package models;

/**
 *
 * @author Vinicius Ferneda de Lima
 */
public enum ClasseID {
    
    palavraReservada(Constantes.PALAVRARESERVADA),
    identificador(Constantes.IDENTIFICADOR),
    constanteNumerica(Constantes.CONSTANTENUMERICA),
    constanteLiteral(Constantes.CONSTANTELITERAL),
    comentario(Constantes.COMENTARIO),
    simboloEspecial(Constantes.SIMBOLOESPECIAL);

    private String descricao;

    private ClasseID(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }  
    
}
