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
    private StringBuilder codigoDeclaracao; //codigo auxailiar para a delcaracao de variaveis 
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

    //inicio do programa
    private void acao_1() throws SemanticError {

        this.id_module = token;
        
         if (!this.id_module.getLexeme().equals(token.getLexeme())) {
            String msg = "identificador (\"" + token.getLexeme() + "\") diferente do utilizado para nomear o modulo (\"" + id_module.getLexeme() + "\")";
            throw new SemanticError(msg, token);
        } 
        
        codigoSaida.append(".assembly extern mscorlib{}");
        codigoSaida.append("\n.assembly ").append(id_module.getLexeme()).append("{}");
        codigoSaida.append("\n.module ").append(id_module.getLexeme()).append(".exe");
        codigoSaida.append("\n.class public ").append(id_module.getLexeme());
        codigoSaida.append("\n{");
        codigoSaida.append("\n  .method public static void main ()");
        codigoSaida.append("\n  {");
        codigoSaida.append("\n     .entrypoint");

        System.out.println(codigoSaida);
    }

    //finalização do programa
    private void acao_2() {

        //declara variaveis
        codigoSaida.append("\n     .locals ("); 
        
        if (codigoDeclaracao != null) {
            String texto = codigoDeclaracao.toString();
            codigoSaida.append(texto.trim());
        }
        
        codigoSaida.append(")");
        
        codigoSaida.append(codigoGerado);

        codigoSaida.append("\n     ret");
        codigoSaida.append("\n  }");
        codigoSaida.append("\n}");

        System.out.println(codigoSaida);

    }

    //declaração de variaveis
    private void acao_3() throws SemanticError {
        if (ids.isEmpty()) {
            String msg = "não foi definido um identificador";
            throw new SemanticError(msg, token);
        }

        List<Token> idsTemp = new ArrayList<Token>();
        for (Token retirado : ids) {
            idsTemp.add(retirado);
        }

        //adiciona na lista de identificadores os ids com o seu tipo
        Token retirado = idsTemp.remove(0);
        TipoID tipo = desempilhaTipo();
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
            Identificador id = new Identificador(retirado, tipo, identificadores.size());
            identificadores.put(retirado.getLexeme(), id);
            
            String texto = "";
            if (identificadores.size() > 1) {
                texto = ",\n";
            }
            texto += "          " + id.getTipo().getTipo() + " " + id.getNome();

            if (codigoDeclaracao == null) {
                 codigoDeclaracao = new StringBuilder();                
            }
            codigoDeclaracao.append(texto);

            if (idsTemp.isEmpty()) {
                retirado = null;
            } else {
                retirado = idsTemp.remove(0);
            }
        }
        
        ids.clear();
        
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

    //empilha uma constante numérica truncada para determinar o tamanho do array
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

    //adiciona o identificadors a lista de identificadores
    private void acao_6() {
        ids.add(token);
    }

    //atribui um valor ao identificador
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

    private void acao_8(){
    }
    
    //geração de código para leitura das variaveis
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

    //escreve o tipo das variáveis
    private void acao_12() throws SemanticError {
        TipoID tipo = desempilhaTipo();//desempilha o tipo empilhado pela acao_23, mas nao sera utilizado

        String texto = "\n     call void [mscorlib]System.Console::Write(" + tipo.getTipo() + ")";
        codigoGerado.append(texto);
    }

    //Inicio da seleção IfTrueDo
    private void acao_13() throws SemanticError {
        TipoID tipo = desempilhaTipo();
        if (tipo != TipoID.tpLogical) {
            String msg = "Expressão do comando condicional inválida: esperado boolean, encontrado " + tipo.getDescricao();
            throw new SemanticError(msg, token);
        }
        empilhaSE("brfalse");
    }

    //Fim da seleção
    private void acao_14() {
        empilhaFimSE();
    }

    //Determina o proximo da seleção
    private void acao_15() {
        empilhaELSE();
    }

    private void empilhaSE(String cmdComparacao) throws SemanticError {
        this.qtdDesviosSE++; //incrementa a quantidade de SE's no codigo
        String desvioSe = "";
        if(qtdDesviosSE < 10){
            desvioSe = "r0" + (qtdDesviosSE);
        }else{
            desvioSe = "r" + (qtdDesviosSE);
        }
        desviosSE.push(desvioSe); //empilha desvio para o fim da parte true de SE
        String texto = "\n     " + cmdComparacao + " " + desvioSe;
        codigoGerado.append(texto);
    }

    private void empilhaFimSE() {
        codigoGerado.append("\n").append(desviosSE.pop()).append(":"); //fim do SE/ELSE
    }
    
    private void empilhaELSE() {
        String desvioSE = desviosSE.pop(); //pega o nome do desvio do fim da parte true do SE
        
        //cria nome desvio ELSE
        this.qtdDesviosELSE++;
        String desvioELSE = "";
        if(qtdDesviosELSE < 10){
            desvioELSE = "r0" + (qtdDesviosELSE);
        }else{
            desvioELSE = "r" + (qtdDesviosELSE);            
        }
        desviosSE.push(desvioELSE); //empilha legenda para o fim do else
                
        codigoGerado.append("\n     br ").append(desvioELSE); //vai para o fim do ELSE        
        codigoGerado.append("\n").append(desvioSE).append(":"); //inicio do ELSE / fim da parte true do SE
    }
    
    //Inicio da repetição
    private void acao_16() {
        this.qtdDesviosLOOP++; //incrementa a quantidade de SE's no codigo  
        
        String nomeIni = "";
        if(qtdDesviosLOOP < 10){
            nomeIni = "r0" + (qtdDesviosLOOP);
        }else{
            nomeIni = "r" + (qtdDesviosLOOP);
        }
        desviosLOOP.push(nomeIni); //empilha desvio para o inicio do LOOP
        
        codigoGerado.append("\n").append(nomeIni).append(":");
        
        String nomeFim = "";
        if(qtdDesviosLOOP < 10){
            nomeFim = "r0" + (qtdDesviosLOOP);
        }else{
            nomeFim = "r" + (qtdDesviosLOOP);
        }
        desviosLOOP.push(nomeFim); //empilha desvio para o fim do LOOP
        
    }

    //Verificação se repetição deve continuar
    private void acao_17() throws SemanticError {
        if (desviosLOOP.size() == 0) {
            String msg = "comando \"exit\" só pode ser utilizado dentro de um bloco de repetição";
            throw new SemanticError(msg, token);
        }
        String desvio = desviosLOOP.pop(); //pega o nome do desvio de loop    
        desviosLOOP.push(desvio); //guarda na pilha de novo para tirar no END do loop
        codigoGerado.append("\n     br ").append(desvio);
    }

    //Saida da repetição
    private void acao_18() {
        String desvioFim = desviosLOOP.pop(); //pega o nome do desvio do fim do loop 
        String desvioIni = desviosLOOP.pop(); //pega o nome do desvio do inicio do loop
        codigoGerado.append("\n     br ").append(desvioIni);
        codigoGerado.append("\n").append(desvioFim).append(":"); 
    }

    //expressão lógica 'and' 
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

    //expressão lógica 'or'
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

    //expressao lógica 'true'
    private void acao_21() {
        codigoGerado.append("\n     ldc.i4.1");
        tipos.push(TipoID.tpLogical);
    }

    //expressão logica 'false'
    private void acao_22() {
        codigoGerado.append("\n     ldc.i4.0");
        tipos.push(TipoID.tpLogical);
    }

    //expressão lógica de negação 'not'
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

    //determina o operador relacional da comparação
    private void acao_24() {
        this.operadorRelacional = token;
    }

    //empilha o resultado da operação lógica
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

    //soma de valores numéricos
    private void acao_26() throws SemanticError {
        soma();
    }

    //subtração de valores numéricos
    private void acao_27() throws SemanticError {
        subtrai();
    }

    //multiplicação de valores numéricos
    private void acao_28() throws SemanticError {
        multiplica();
    }

    //divisão de valores numéricos
    private void acao_29() throws SemanticError {
        divide(1);
    }

    //retorna o quociente (inteiro) da divisão
    private void acao_30() throws SemanticError {
        divide(2);
    }

    //retorna a parte inteira do parâmetro
    private void acao_31() throws SemanticError {
        divide(3);
    }

    //empilha o identificador
    private void acao_32() throws SemanticError {
        Identificador id = getIdentificador(token);
        empilha(id);
    }

    //determina a expressão de um array
    private void acao_33() {
    }

    //empilha uma constante numerica
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

    //empilha uma constante literal
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

    //trunk (retorna a parte inteira do parâmetro)
    private void acao_36() throws SemanticError {
        TipoID tipo = desempilhaTipo();

        //verifica se a operacao pode ser atribuida pro identificador
        boolean podeAlterar = estaContidoEm(tipo, TipoID.tpNumber);
        if (!podeAlterar) {
            String msg = "constante numérica inválida para expressões do tipo " + tipo.getDescricao();
            throw new SemanticError(msg, token);
        }

        codigoGerado.append("\n     conv.i8 ");
        tipos.push(TipoID.tpInt); //empilha tipo number
    }

    //round (retorna a parte arredondada do parametro)
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

    //troca de sinal para positiva do elemento
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

    //troca de sinal para o negativo
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

    private void divide(int operacao) throws SemanticError {//divide valores da pilha
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();

        if (!estaContidoEm(TipoID.tpNumber, tipo1, tipo2)) {
            String msg = "operação de divisão inválida: " + tipo2.getDescricao() + " + " + tipo1.getDescricao();
            throw new SemanticError(msg, token);
        }

        switch (operacao) {
            case 1: //divisao normal 
                codigoGerado.append("\n     div");
                codigoGerado.append("\n     conv.r8");
                tipos.push(TipoID.tpNumber); //divisão normal sempre retorna float
                break;
            case 2: //retorna apenas parte indeira
                codigoGerado.append("\n     div");
                codigoGerado.append("\n     conv.i8 ");
                tipos.push(TipoID.tpInt); //divisão normal sempre retorna float
                break;
            case 3: //retorna apenas o resto da divisao
                codigoGerado.append("\n     rem");
                codigoGerado.append("\n     conv.i8 ");
                tipos.push(TipoID.tpInt); //divisão normal sempre retorna float
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
    }
}
