package models;

/**
 *
 * @author Vinícius Ferneda de Lima
 */
public enum TipoID {
    
    tpInt("int","int64","System.Int64"),
    tpFloat("float","float64","System.Double"),
    tpDate("date","int64","System.Int64"),
    tpTime("time","int64","System.Int64"),
    tpBoolean("boolean","bool","System.Boolean"),
    tpString("string","string","System.String");
            
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
