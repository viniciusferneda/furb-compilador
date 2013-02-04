package gals;

public class SyntaticError extends AnalysisError{

    private Token token;

    public SyntaticError(String msg, Token token) {
        super(msg);
        this.token = token;
    }
      
    @Override
    public int getPosition() {
        return token.getPosition();
    } 
    
    public Token getToken() {
        return token;
    }
           
         
}
