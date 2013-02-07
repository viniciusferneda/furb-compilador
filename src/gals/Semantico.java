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

    //inicio do programa
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

    //finalização do programa
    private void acao_2() {

        codigoSaida.append(codigoGerado);

        codigoSaida.append("\n     ret");
        codigoSaida.append("\n  }");
        codigoSaida.append("\n}");

        System.out.println(codigoSaida);

    }

    //declaração de variaveis
    private void acao_3() throws SemanticError {
        codigoSaida.append("\n     .locals (");

        if (ids.isEmpty()) {
            String msg = "não foi definido um identificador";
            throw new SemanticError(msg, token);
        }

        //procura o TipoID do tipo descrido no token
        TipoID tipoIds = null;
        for (TipoID tipo : TipoID.values()) {
            if (token.getLexeme().equals(tipo.getDescricao())) {
                tipoIds = tipo;
                break;
            }
        }
        if (tipoIds == null) {
            String msg = "tipo " + token.getLexeme() + " é inválido";
            throw new SemanticError(msg, token);
        }
        tipos.push(tipoIds);

        List<Token> idsTemp = new ArrayList<Token>();
        for (Token retirado : ids) {
            idsTemp.add(retirado);
        }

        //adiciona na lista de identificadores os ids com o seu tipo
        Token retirado = idsTemp.remove(0);
        while (retirado != null) {
            //verifica se identificador já foi declarado
            if (id_module.getLexeme().equals(retirado.getLexeme())) {
                String msg = "identificador \"" + retirado.getLexeme() + "\" declarado com o mesmo nome do módulo";
                throw new SemanticError(msg, retirado);
            }
            boolean jaDeclarado = (identificadores.get(retirado.getLexeme()) != null);
            if (!jaDeclarado) {
                for (Token t1 : idsTemp) {
                    if (retirado.getLexeme().equals(t1.getLexeme())) {
                        jaDeclarado = true;
                        break;
                    }
                }
            }
            if (jaDeclarado) {
                String msg = "identificador \"" + retirado.getLexeme() + "\" já foi declarado";
                throw new SemanticError(msg, retirado);
            }

            //cria novo identificador
            Identificador id = new Identificador(retirado, tipoIds, identificadores.size());
            identificadores.put(retirado.getLexeme(), id);

            String texto = "";
            if (identificadores.size() > 1) {
                texto = ",\n";
            }
            texto += "          " + id.getTipo().getTipo() + " " + id.getNome();
            codigoSaida.append(texto);

            if (idsTemp.isEmpty()) {
                retirado = null;
            } else {
                retirado = idsTemp.remove(0);
            }
        }
    }

    //empilha o tipo da variavel
    private void acao_4() throws SemanticError {
        //procura o TipoID do tipo descrido no token
        TipoID tipoIds = null;
        for (TipoID tipo : TipoID.values()) {
            if (token.getLexeme().equals(tipo.getDescricao())) {
                tipoIds = tipo;
                break;
            }
        }
        if (tipoIds == null) {
            String msg = "tipo " + token.getLexeme() + " é inválido";
            throw new SemanticError(msg, token);
        }
        tipos.push(tipoIds);
    }

    private void acao_5() {
        String texto = token.getLexeme();
        char[] array = texto.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == ',') {
                array[i] = '.';
            }
        }
        texto = String.copyValueOf(array);
        codigoGerado.append("\n     conv.i8 ").append(texto);
        tipos.push(TipoID.tpNumber); //empilha tipo number
    }

    private void acao_6() {
        ids.add(token);
    }

    private void acao_7() throws SemanticError {
        TipoID tipo1 = desempilhaTipo(); //guarda tipo do valor na pilha   
        tipos.push(tipo1); //empilha o tipo do valor na pilha para poder validar na funcao setaValorId

        if (ids.size() > 1) {
            for (int i = 1; i < ids.size(); i++) { //duplica o valor da pilha para cada identificador da pilha
                codigoGerado.append("\n     dup"); //duplica valor existente na lista
                tipos.push(tipo1);
            }
        }

        for (Token retirado : ids) {
            Identificador id = getIdentificador(retirado);
            setaValorId(id); //atribui o valor na pilha para o identificador
        }

        tipos.push(tipo1); //empilha o tipo na pilha para poder remover na chamada obrigatoria desta acao com ";"
    }

    private void acao_10() throws SemanticError {
        for (Token retirado : ids) {
            Identificador id = getIdentificador(retirado); //identificador que irá receber a entrada

            if (estaContidoEm(id.getTipo(), TipoID.tpLogical)) {
                String msg = "tipo de entrada inválido: encontrado " + id.getTipo().getDescricao() + " mas era esperado int, float ou string";
                throw new SemanticError(msg, token);
            }

            codigoGerado.append("\n     call string [mscorlib] System.Console::ReadLine()"); //le o dado da tela
            if (id.getTipo() != TipoID.tpCharacter) { //a entrada da tela já é string
                //codigo par converter para o tipo do identificador
                String texto = "\n     call " + id.getTipo().getTipo() + " [mscorlib] " + id.getTipo().getClasse() + "::Parse(string)";
                codigoGerado.append(texto);
            }
            tipos.push(id.getTipo());//seta o tipo de dado que foi inserido na pilha pela funcao acima

            setaValorId(id);
        }
        ids.clear();
    }

    private void acao_12() throws SemanticError {
        TipoID tipo = desempilhaTipo();//desempilha o tipo empilhado pela acao_23, mas nao sera utilizado

        String texto = "\n     call void [mscorlib]System.Console::Write(" + tipo.getTipo() + ")";
        codigoGerado.append(texto);
    }

    private void acao_13() {
    }

    private void acao_14() {
    }

    private void acao_15() {
    }

    private void acao_16() {
    }

    private void acao_17() {
    }

    private void acao_18() {
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

    //retorna o resto (inteiro) da divisão
    private void acao_31() throws SemanticError {
        divide(3);
    }

    private void acao_32() throws SemanticError {
        Identificador id = getIdentificador(token);
        if (!id.isIncicializado()) {
            String msg = "identificador \"" + id.getNome() + "\" não foi inicializado";
            throw new SemanticError(msg, token);
        }
        empilha(id);
    }

    //numerico
    private void acao_34() throws SemanticError {
        String texto = token.getLexeme();
        char[] array = texto.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == ',') {
                array[i] = '.';
            }
        }
        texto = String.copyValueOf(array);
        codigoGerado.append("\n     ldc.r8 ").append(texto);
        tipos.push(TipoID.tpNumber); //empilha tipo number
    }

    //literal
    private void acao_35() {
        String texto = token.getLexeme();
        char[] array = texto.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '\'') {
                array[i] = '"';
            }
        }
        texto = String.copyValueOf(array);
        codigoGerado.append("\n     ldstr ").append(texto);
        tipos.push(TipoID.tpCharacter); //empilha tipo literal
    }

    //trunk
    private void acao_36() throws SemanticError {
        TipoID tipo = desempilhaTipo();

        //verifica se a operacao pode ser atribuida pro identificador
        boolean podeAlterar = estaContidoEm(tipo, TipoID.tpNumber);
        if (!podeAlterar) {
            String msg = "constante numérica inválida para expressões do tipo " + tipo.getDescricao();
            throw new SemanticError(msg, token);
        }

        codigoGerado.append("\n     conv.i8 ");
        tipos.push(TipoID.tpNumber); //empilha tipo number
    }

    //round
    private void acao_37() throws SemanticError {
        TipoID tipo = desempilhaTipo();

        //verifica se a operacao pode ser atribuida pro identificador
        boolean podeAlterar = estaContidoEm(tipo, TipoID.tpNumber);
        if (!podeAlterar) {
            String msg = "constante numérica inválida para expressões do tipo " + tipo.getDescricao();
            throw new SemanticError(msg, token);
        }

        codigoGerado.append("\n     ldc.r8 ");
        tipos.push(TipoID.tpNumber); //empilha tipo number
    }

    private void acao_38() throws SemanticError {
        TipoID tipo = desempilhaTipo();

        //verifica se a operacao pode ser atribuida pro identificador
        boolean podeAlterar = estaContidoEm(tipo, TipoID.tpNumber);
        if (!podeAlterar) {
            String msg = "inversão de sinal inválida para expressões do tipo " + tipo.getDescricao();
            throw new SemanticError(msg, token);
        }

        tipos.push(tipo);
    }

    private void acao_39() throws SemanticError {//muda sinal valor
        TipoID tipo = desempilhaTipo();

        //verifica se a operacao pode ser atribuida pro identificador
        boolean podeAlterar = estaContidoEm(tipo, TipoID.tpNumber);
        if (!podeAlterar) {
            String msg = "inversão de sinal inválida para expressões do tipo " + tipo.getDescricao();
            throw new SemanticError(msg, token);
        }

        codigoGerado.append("\n     ldc.i8 -1 ");
        codigoGerado.append("\n     mul");
        tipos.push(tipo);
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

    private Identificador getIdentificador(Token token) throws SemanticError {
        Identificador id = identificadores.get(token.getLexeme());
        if (id == null) {
            String msg = "identificador \"" + token.getLexeme() + "\" não foi declarado";
            throw new SemanticError(msg, this.token);
        }
        return id;
    }

    private void empilha(Identificador id) {
        codigoGerado.append("\n     ldloc ").append(id.getNome());
        tipos.push(id.getTipo()); //adiciona tipo do identificador 
    }

    private void setaValorId(Identificador id) throws SemanticError {
        TipoID tipo = desempilhaTipo();

        boolean podeAlterar = (id.getTipo() == tipo) || (id.getTipo() == TipoID.tpNumber);
        if (!podeAlterar) {
            String msg = "operação de atribuição inválida: " + id.getTipo().getDescricao() + " <- " + tipo.getDescricao();
            throw new SemanticError(msg, token);
        }
        String texto = "\n     stloc " + id.getNome();

        codigoGerado.append(texto);
        id.inicializou();
    }
}
