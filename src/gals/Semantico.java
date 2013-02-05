package gals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Identificador;
import models.TipoID;

public class Semantico implements Constants {

    private Token token; //token da chamada do executeAction
    private StringBuilder codigoSaida; //codigo gerado pela rotina
    private StringBuilder codigoGerado; //codigo gerado pela rotina
    private StringBuilder codigoDeclaracao; //codigo auxiliar para a declaracao de variaveis 
    private Map<String, Identificador> identificadores; //tabela de simbolos    
    private List<Token> ids; //lista de identificadores
    private Stack<String> desviosSE; //pilha de desvios do comando SE ELSE END
    private Stack<String> desviosLOOP; //pilha de desvios do comando LOOP END
    private Stack<TipoID> tipos; //pilha de tipos
    private Token operadorRelacional; //operador relacional
    private Token operadorAtribuicao; //operador relacional de atribuicao
    private Token id_module; //identificador como nome do programa
    private int tamMaxPilhaTipos; //guarda o tamanho máximo da pilha de tipos para saber qual o tamanho da pilha deve ser gerada no CIL
    private int qtdDesviosSE;
    private int qtdDesviosELSE;
    private int qtdDesviosLOOP;

    public Semantico(StringBuilder codigoSaida) {
        this.codigoSaida = codigoSaida;
        this.codigoGerado = new StringBuilder();
        this.codigoDeclaracao = null;
        this.identificadores = new HashMap<String, Identificador>();
        this.ids = new ArrayList<Token>();
        this.desviosSE = new Stack<String>();
        this.desviosLOOP = new Stack<String>();
        this.tipos = new Stack<TipoID>();
        this.operadorRelacional = null;
        this.operadorAtribuicao = null;
        this.tamMaxPilhaTipos = 0;
        this.qtdDesviosSE = 0;
        this.qtdDesviosELSE = 0;
        this.qtdDesviosLOOP = 0;
    }

    public void executeAction(int action, Token token) throws SemanticError {
        this.token = token; //guarda token  

        try {
            Method metodo = this.getClass().getDeclaredMethod("acao_" + action);
            metodo.invoke(this);
            System.out.println("Executado acao " + action);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(gals.Semantico.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro na acao " + action);
            System.out.println(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(gals.Semantico.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro na acao " + action);
            System.out.println(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(gals.Semantico.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro na acao " + action);
            System.out.println(ex.getMessage());
        } catch (SecurityException ex) {
            Logger.getLogger(gals.Semantico.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro na acao " + action);
            System.out.println(ex.getMessage());
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof SemanticError) {
                throw (SemanticError) ex.getTargetException();
            } else {
                Logger.getLogger(gals.Semantico.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erro na acao " + action);
                System.out.println(ex.getMessage());
            }
        }
    }

    private void acao_1() {

        codigoSaida.append(".assembly extern mscorlib{}");
        codigoSaida.append("\n.assembly vinicius{}");
        codigoSaida.append("\n.module vinicius.exe");
        codigoSaida.append("\n.class public vinicius");
        codigoSaida.append("\n{");
        codigoSaida.append("\n  .method public static void main ()");
        codigoSaida.append("\n  {");
        codigoSaida.append("\n     .entrypoint");

        System.out.println(codigoSaida);
    }

    private void acao_2() {

        codigoSaida.append("\n     ret");
        codigoSaida.append("\n  }");
        codigoSaida.append("\n}");

        System.out.println(codigoSaida);

    }

    private void acao_12() throws SemanticError {
        TipoID tipo = desempilhaTipo();//desempilha o tipo empilhado pela acao_23, mas nao sera utilizado

        String texto = "\n     call void [mscorlib]System.Console::Write(" + tipo.getTipo() + ")";
        codigoGerado.append(texto);
    }

    private void acao_19() throws SemanticError {
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();

        //verifica se a operacao pode ser atribuida 
        if (tipo1 != TipoID.tpLogical) {
            String msg = "operdador \"and\" é inválido para o tipo " + tipo1.getDescricao();
            throw new SemanticError(msg, token);
        }
        if (tipo2 != TipoID.tpLogical) {
            String msg = "operador \"and\" é inválido para o tipo " + tipo2.getDescricao();
            throw new SemanticError(msg, token);
        }

        codigoGerado.append("\n     and");
        tipos.push(TipoID.tpLogical);
    }

    private void acao_20() throws SemanticError {
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();

        //verifica se a operacao pode ser atribuida 
        if (tipo1 != TipoID.tpLogical) {
            String msg = "operdador \"or\" é inválido para o tipo " + tipo1.getDescricao();
            throw new SemanticError(msg, token);
        }
        if (tipo2 != TipoID.tpLogical) {
            String msg = "operador \"or\" é inválido para o tipo " + tipo2.getDescricao();
            throw new SemanticError(msg, token);
        }

        codigoGerado.append("\n     or");
        tipos.push(TipoID.tpLogical);
    }

    private void acao_21() {
        codigoGerado.append("\n     ldc.i4.1");
        tipos.push(TipoID.tpLogical);
    }

    private void acao_22() {
        codigoGerado.append("\n     ldc.i4.0");
        tipos.push(TipoID.tpLogical);
    }

    private void acao_23() throws SemanticError {
        TipoID tipo = desempilhaTipo();

        if (tipo != TipoID.tpLogical) {
            String msg = "operador de negação é inválido para o tipo " + tipo.getDescricao();
            throw new SemanticError(msg, token);
        }
        codigoGerado.append("\n     ldc.i4.0");
        codigoGerado.append("\n     ceq");
        tipos.push(tipo);
    }

    private void acao_24() {
        this.operadorRelacional = token;
    }

    private void acao_25() throws SemanticError {
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();

        //verifica se a operacao pode ser atribuida 
        boolean naoPodeAlterar = ((tipo1 == TipoID.tpCharacter) && (tipo2 != TipoID.tpCharacter))
                || ((tipo1 == TipoID.tpNumber) && (tipo2 != TipoID.tpNumber));
        if (naoPodeAlterar) {
            String msg = "operação de comparação inválida: " + tipo2.getDescricao() + " + " + tipo1.getDescricao();
            throw new SemanticError(msg, token);
        }

        if (operadorRelacional == null) {
            String msg = "operador relacional não definido";
            throw new SemanticError(msg, token);
        }

        String aux = this.operadorRelacional.getLexeme();
        if (aux.equals("=")) {
            codigoGerado.append("\n     ceq"); //verifica se valores sao iguais               
        } else if (aux.equals("!=")) {
            codigoGerado.append("\n     ceq"); //verifica se valores sao iguais                
            codigoGerado.append("\n     ldc.i4.0");
            codigoGerado.append("\n     ceq"); //inverte valor   
        } else if (aux.equals("<")) {
            codigoGerado.append("\n     clt");
        } else if (aux.equals("<=")) {
            codigoGerado.append("\n     clt");
            codigoGerado.append("\n     ldc.i4.0");
            codigoGerado.append("\n     ceq");
        } else if (aux.equals(">")) {
            codigoGerado.append("\n     cgt");
        } else if (aux.equals(">=")) {
            codigoGerado.append("\n     cgt");
            codigoGerado.append("\n     ldc.i4.0");
            codigoGerado.append("\n     ceq");
        } else {
            String msg = "operador relacional inválido";
            throw new SemanticError(msg, token);
        }
        this.operadorRelacional = null;

        tipos.push(TipoID.tpLogical); //empilha tipo do retorno da opreração
    }

    private void acao_26() throws SemanticError {
        soma();
    }

    private void acao_27() throws SemanticError {
        subtrai();
    }

    private void acao_28() throws SemanticError {
        multiplica();
    }

    private void acao_29() throws SemanticError {
        divide();
    }

    private void acao_30() throws SemanticError {
        divide(2);
    }

    private void acao_31() throws SemanticError {
        divide(3);
    }

    private TipoID desempilhaTipo() throws SemanticError {
        if (tipos.isEmpty()) {
            String msg = "ERRO NA ANALISE SEMANTICA: não foi encontrado o tipo do token";
            throw new SemanticError(msg, token);
        }
        int size = tipos.size();
        if (size > this.tamMaxPilhaTipos) {
            this.tamMaxPilhaTipos = size;
        }
        return tipos.pop();
    }

    private boolean estaContidoEm(TipoID tipo, TipoID... tipos) {
        if (tipos != null) {
            for (int i = 0; i < tipos.length; i++) {
                if (tipos[i] == tipo) {
                    return true;
                }
            }
        }
        return false;
    }

    private void soma() throws SemanticError { //soma valores da pilha
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();

        if (estaContidoEm(TipoID.tpNumber, tipo1, tipo2)) {
            codigoGerado.append("\n     add");
            tipos.push(TipoID.tpNumber);
        } else {
            String msg = "operação de soma inválida: " + tipo2.getDescricao() + " + " + tipo1.getDescricao();
            throw new SemanticError(msg, token);
        }
    }

    private void subtrai() throws SemanticError { //subtrai valores da pilha
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();

        if (estaContidoEm(TipoID.tpNumber, tipo1, tipo2)) {
            codigoGerado.append("\n     sub");
            tipos.push(TipoID.tpNumber);
        } else {
            String msg = "operação de subtração inválida: " + tipo2.getDescricao() + " + " + tipo1.getDescricao();
            throw new SemanticError(msg, token);
        }
    }

    private void multiplica() throws SemanticError {//mutiplica valores da pilha
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();

        if (estaContidoEm(TipoID.tpNumber, tipo1, tipo2)) {
            codigoGerado.append("\n     mul");
            tipos.push(TipoID.tpNumber);
        } else {
            String msg = "operação de multiplicação inválida: " + tipo2.getDescricao() + " + " + tipo1.getDescricao();
            throw new SemanticError(msg, token);
        }

    }

    private void divide(int... tipo) throws SemanticError {//divide valores da pilha
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();

        if (!estaContidoEm(TipoID.tpNumber, tipo1, tipo2)) {
            String msg = "operação de divisão inválida: " + tipo2.getDescricao() + " + " + tipo1.getDescricao();
            throw new SemanticError(msg, token);
        }

        int operacao = 1;
        if (tipo == null) {
            operacao = tipo[0];
        }

        switch (operacao) {
            case 1: //divisao normal 
                codigoGerado.append("\n     div");
                codigoGerado.append("\n     conv.r8");
                tipos.push(TipoID.tpNumber); //divisão normal sempre retorna float
                break;
            case 2: //retorna apenas parte indeira
                codigoGerado.append("\n     div");
                tipos.push(TipoID.tpNumber); //divisão normal sempre retorna float
                break;
            case 3: //retorna apenas o resto da divisao
                codigoGerado.append("\n     rem");
                tipos.push(TipoID.tpNumber); //divisão normal sempre retorna float
                break;
        }
    }
}
