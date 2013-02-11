package models;

/**
 *
 * @author Vinícius Ferneda de Lima
 */
public enum TipoID {
    
    tpNumber("number","float64","System.Double"),
    tpInt("int","int64","System.Int64"),
    tpLogical("logical","bool","System.Boolean"),
    tpCharacter("character","string","System.String");
            
    private String descricao;
    private String tipo;
    private String classe;

    private TipoID(String descricao, String tipo, String classe) {
        this.descricao = descricao;
        this.tipo = tipo;
        this.classe = classe;
    }   

    public String getDescricao() {
        return descricao;
    }

    public String getClasse() {
        return classe;
    }

    public String getTipo() {
        return tipo;
    }
}
