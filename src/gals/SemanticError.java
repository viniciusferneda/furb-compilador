package gals;

public class SemanticError extends AnalysisError{
    
    private Token token;

     public SemanticError(String msg, Token token) {
        super(msg);
        this.token = token;
    }

    public SemanticError(String msg){
        super(msg);
    }
    
    @Override
    public int getPosition() {
        return token.getPosition();
    } 
    
    public Token getToken() {
        return token;
    }

}
