package gals;

public class SyntaticError extends Exception{
    
    private Token token;
    
    public SyntaticError(String msg, Token token){
        super(msg);
        this.token = token;
    }

    public SyntaticError(String msg){
        super(msg);
    }
    
    public int getPosition() {
        return token.getPosition();
    }
    
    public Token getToken() {
        return token;
    }

}
