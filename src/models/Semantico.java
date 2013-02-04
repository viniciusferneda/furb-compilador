package models;

import gals.Constants;
import gals.SemanticError;
import gals.Token;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Identificador idArmazemInt; //token auxiliar para a acao_29
    private Identificador idArmazemFloat; //token auxiliar para a acao_29
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
            Method metodo = this.getClass().getDeclaredMethod("acao_"+action);            
            metodo.invoke(this);
            System.out.println("Executado acao " + action);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Semantico.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro na acao " + action);
            System.out.println(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Semantico.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro na acao " + action);
            System.out.println(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Semantico.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro na acao " + action);
            System.out.println(ex.getMessage());
        } catch (SecurityException ex) {
            Logger.getLogger(Semantico.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Erro na acao " + action);
            System.out.println(ex.getMessage());
        } catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof SemanticError) {
                throw (SemanticError)ex.getTargetException();
            } else {
                Logger.getLogger(Semantico.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Erro na acao " + action);
                System.out.println(ex.getMessage());
            }
        }          
        
        //System.out.println(codigoGerado);
    }

    public void acao_1() {
               
    }

    private void acao_2() throws SemanticError {
        if (!this.id_module.getLexeme().equals(token.getLexeme())) {
            String msg = "identificador (\"" + token.getLexeme() + "\") diferente do utilizado para nomear o modulo (\"" + id_module.getLexeme() + "\")";
            throw new SemanticError(msg, token);
        } 
        
        
        codigoSaida.append(".assembly extern mscorlib{}");
        codigoSaida.append("\n.assembly trabalho{}"); 
        codigoSaida.append("\n.module trabalho.exe"); 
        codigoSaida.append("\n.class public ").append(id_module.getLexeme());
        codigoSaida.append("\n{");
        codigoSaida.append("\n  .method public static void main ()");
        codigoSaida.append("\n  {");
        codigoSaida.append("\n     .entrypoint");
        codigoSaida.append("\n     .maxstack ").append(this.tamMaxPilhaTipos);
        
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

    private void acao_3() throws SemanticError {        
        TipoID tipo = desempilhaTipo();//desempilha o tipo empilhado pela acao_23, mas nao sera utilizado
        String write;
        if (estaContidoEm(tipo, TipoID.tpDate, TipoID.tpTime, TipoID.tpBoolean)) {
            String msg = "tipo de saída inválido: encontrado " + tipo.getDescricao() + " mas era esperado int, float ou string" ;
            throw new SemanticError(msg, token);
        }         
         
        String texto = "\n     call void [mscorlib]System.Console::Write(" + tipo.getTipo() + ")";
        codigoGerado.append(texto);
   }
        
    
    private void acao_4() throws SemanticError {
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();
        
        //verifica se a operacao pode ser atribuida 
        if (tipo1 != TipoID.tpBoolean) {
            String msg = "operdador \"or\" é inválido para o tipo " + tipo1.getDescricao();
            throw new SemanticError(msg, token);
        }
        if (tipo2 != TipoID.tpBoolean) {
            String msg = "operador \"or\" é inválido para o tipo " + tipo2.getDescricao();
            throw new SemanticError(msg, token);
        }
         
        codigoGerado.append("\n     or");
        tipos.push(TipoID.tpBoolean);
    }
    
    private void acao_5() throws SemanticError {
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();
        
        //verifica se a operacao pode ser atribuida 
        if (tipo1 != TipoID.tpBoolean) {
            String msg = "operdador \"and\" é inválido para o tipo " + tipo1.getDescricao();
            throw new SemanticError(msg, token);
        }
        if (tipo2 != TipoID.tpBoolean) {
            String msg = "operador \"and\" é inválido para o tipo " + tipo2.getDescricao();
            throw new SemanticError(msg, token);
        }
        
        codigoGerado.append("\n     and");
        tipos.push(TipoID.tpBoolean);
    }
    
    private void acao_6() {//EMPILHA TRUE
        codigoGerado.append("\n     ldc.i4.1");  
        tipos.push(TipoID.tpBoolean);
    }
    
    private void acao_7() { //EMPILHA FALSE
        codigoGerado.append("\n     ldc.i4.0");       
        tipos.push(TipoID.tpBoolean);
    }
    
    private void acao_8() throws SemanticError { //NEGACAO LOGICA
        TipoID tipo = desempilhaTipo();
        
        if (tipo != TipoID.tpBoolean) {
            String msg = "operador de negação é inválido para o tipo " + tipo.getDescricao();
            throw new SemanticError(msg, token);
        }
        codigoGerado.append("\n     ldc.i4.0");
        codigoGerado.append("\n     ceq");
        tipos.push(tipo);
    }
    
    private void acao_9() {
        this.operadorRelacional = token;
    }
    
    private void acao_10() throws SemanticError {//COMPARA VALORES PELO OPERADOR UTILIZADO                
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();
        
        //verifica se a operacao pode ser atribuida 
        boolean naoPodeAlterar = ((tipo1 == TipoID.tpString) && (tipo2 != TipoID.tpString)) ||
                                 ((tipo1 == TipoID.tpBoolean) && (tipo2 != TipoID.tpBoolean));
        if (naoPodeAlterar) {
            String msg = "operação de comparação inválida: " + tipo2.getDescricao() + " + " + tipo1.getDescricao();
            throw new SemanticError(msg, token); 
        }    
        
        if (operadorRelacional == null) {
            String msg = "operador relacional não definido";
            throw new SemanticError(msg, token); 
        }
        
        String aux = this.operadorRelacional.getLexeme();
        if (aux.equals("==")) {
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
            //empilhaSE("ble"); //vai para o else caso seja menor ou igual 
            //codigoGerado.append("\n     ldc.i4.0");  //se nao cai no else, empilha false
            //empilhaELSE(); //incia else
            //codigoGerado.append("\n     ldc.i4.1"); //se veio pro else empilha true
            //empilhaFimSE();//define fim do se             
        } else if (aux.equals(">")) {
            codigoGerado.append("\n     cgt");    
        } else if (aux.equals(">=")) {
            codigoGerado.append("\n     cgt"); 
            codigoGerado.append("\n     ldc.i4.0");
            codigoGerado.append("\n     ceq"); 
            //empilhaSE("bge"); //vai para o else caso seja menor ou igual 
            //codigoGerado.append("\n     ldc.i4.0");  //se nao cai no else, empilha false
            //empilhaELSE(); //incia else
            //codigoGerado.append("\n     ldc.i4.1");  //se veio pro else empilha true
            //empilhaFimSE();//define fim do se    
        } else {
            String msg = "operador relacional inválido";
            throw new SemanticError(msg, token);
        } 
        this.operadorRelacional = null;
        
        tipos.push(TipoID.tpBoolean); //empilha tipo do retorno da opreração
    }
    
    private void acao_11() throws SemanticError { //soma valores da pilha
        soma();
    }
    
    private void acao_12() throws SemanticError { //subtrai valores da pilha
        subtrai();
    }
    
    private void acao_13() throws SemanticError {//mutiplica valores da pilha
        multiplica();
    }
    
    private void acao_14() throws SemanticError {//divide valores da pilha
        divide();
    }
    
    private void acao_15() throws SemanticError {
        divide(2);
    }
    
    private void acao_16() throws SemanticError {
        divide(3);
    }
    
    private void acao_17() { //empilha valor inteiro
        codigoGerado.append("\n     ldc.i8 ").append(token.getLexeme());
        tipos.push(TipoID.tpInt); //empilha tipo inteiro
    }
    
    private void acao_18() {//empilha valor real
        String texto = token.getLexeme();
        char[] array = texto.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (array[i] == ',') {
                array[i] = '.';
            }
        }
        texto = String.copyValueOf(array);
        codigoGerado.append("\n     ldc.r8 ").append(texto);
        tipos.push(TipoID.tpFloat); //empilha tipo real
    }
    
    private void acao_19() throws SemanticError {//empilha data
        int tam = token.getLexeme().length();
        String tipoFormat;
        if (tam == 8) {
            tipoFormat = "dd/MM/yy";
        } else {
            tipoFormat = "dd/MM/yyyy";
        }        
        DateFormat dataFormatEntrada = new SimpleDateFormat(tipoFormat);
        dataFormatEntrada.setLenient(false);
        
        Date date;
        try {
            date = dataFormatEntrada.parse(token.getLexeme());            
        } catch (ParseException ex) {
            String msg = "data inválida";
            throw new SemanticError(msg, token);            
        }
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0); //insere hora como 0
        cal.set(Calendar.MINUTE, 0); //insere minuto como 0
        cal.set(Calendar.SECOND, 0); //insere segundo como 0
        date = cal.getTime();
        
        Long data = date.getTime();
        //DateFormat dataFormatSaida = new SimpleDateFormat("yyyyMMdd");
        //String data = dataFormatSaida.format(date);
        
        codigoGerado.append("\n     ldc.i8 ").append(data.toString());
        tipos.push(TipoID.tpDate); //empilha tipo date
    }
    
    private void acao_20() throws SemanticError {//empilha hora
        boolean temSegundo;        
        String tipoFormat;
        int tam = token.getLexeme().length();
        if (tam == 5) {
            tipoFormat = "hh:mm";
            temSegundo = false;
        } else {
            tipoFormat = "hh:mm:ss";
            temSegundo = true;
        }        
        DateFormat dataFormatEntrada = new SimpleDateFormat(tipoFormat); 
        //dataFormatEntrada.setLenient(false);        
        Date date;
        try {
            date = dataFormatEntrada.parse(token.getLexeme());
        } catch (ParseException ex) {
            String msg = "hora inválida";
            throw new SemanticError(msg, token);
        }
        if (!temSegundo) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.SECOND, 0); //insere segundo como 0
            date = cal.getTime(); 
        }
        Long hora = date.getTime();
        //DateFormat dataFormatSaida = new SimpleDateFormat("hhmmss");
        //String hora = dataFormatSaida.format(date);
        
        codigoGerado.append("\n     ldc.i8 ").append(hora.toString());
        tipos.push(TipoID.tpTime); //empilha tipo time
    }
    
    private void acao_21() {//empilha literal
        codigoGerado.append("\n     ldstr ").append(token.getLexeme());
        tipos.push(TipoID.tpString); //empilha tipo string
    }
    
    private void acao_22() throws SemanticError {//muda sinal valor
        TipoID tipo = desempilhaTipo();
        //verifica se a operacao pode ser atribuida pro identificador
        boolean podeAlterar = estaContidoEm(tipo, TipoID.tpFloat, TipoID.tpInt); 
        if (!podeAlterar) {
            String msg = "inversão de sinal inválida para expressões do tipo " + tipo.getDescricao();
            throw new SemanticError(msg, token); 
        }  
        
        codigoGerado.append("\n     ldc.i8 -1 ");
        codigoGerado.append("\n     mul");
        tipos.push(tipo);
    }
    
    private void acao_23() { 
        ids.add(token);
    }    
    
    private void acao_24() throws SemanticError {
        Identificador id = getIdentificador(token);
        if (!id.isIncicializado()) {
            String msg = "identificador \"" + id.getNome() + "\" não foi inicializado";
            throw new SemanticError(msg, token);
        }
        empilha(id);
    }
    
    private void acao_25() throws SemanticError {
        if (ids.isEmpty()) {
            String msg = "não foi definido um identificador";
            throw new SemanticError(msg, token);
        }
        
        //procura o IdTipo do tipo descrido no token
        TipoID tipoIds = null;
        for (TipoID tipo: TipoID.values()) {
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
             //declara o identificador no comando
            if (codigoDeclaracao == null) {
                 codigoDeclaracao = new StringBuilder();                
            }
            String texto = "";
            if  (identificadores.size() > 1) {        
                texto = ",\n"; 
            }
            texto += "          " + id.getTipo().getTipo() + " " + id.getNome();
            codigoDeclaracao.append(texto);

            if (idsTemp.isEmpty()) {
                retirado = null;
            } else {
                retirado = idsTemp.remove(0);
            }
        }  
    }
    
    private void acao_26() throws SemanticError { 
        
        if (token.getLexeme().equals(";")) { //se for chamada obrigatoria com o token ";"
            //ACAO APOS ";" DA DECLARACAO DE VARIAVEIS
            
            ids.clear();
            desempilhaTipo(); //remove tipo do identificador 

        } else { //se for a chamada de atribuicao opcional desta acao
            //ACAO APOS EXPRESSAO DE ATRIBUICAO DA DECLARACAO DE VARIAVEIS            
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
    }
    
    private void acao_27() throws SemanticError {
        if (desviosLOOP.size() == 0) {
            String msg = "comando \"exit\" só pode ser utilizado dentro de um bloco de repetição";
            throw new SemanticError(msg, token);
        }
        String desvio = desviosLOOP.pop(); //pega o nome do desvio de loop    
        desviosLOOP.push(desvio); //guarda na pilha de novo para tirar no END do loop
        codigoGerado.append("\n     br ").append(desvio);         
    }
    
    private void acao_28() throws SemanticError { //operacao de atribuicao
        this.operadorAtribuicao = token;
        
        String aux = this.operadorAtribuicao.getLexeme();
        
        if ((aux.equals("-=")) || (aux.equals("+="))) {
            Token token = ids.get(0); //pega o identificador da lista
            Identificador id = getIdentificador(token);
            empilha(id);//empilha o valor original do identificador para usar na soma/subtracao da atribuicao composta
        } 
    }
    
    private void acao_29() throws SemanticError {        
        if (ids.size() != 1) {
            String msg = "identificador não definido";
            throw new SemanticError(msg, token);
        }        
        
        Token retirado = ids.remove(0);        
        Identificador id = getIdentificador(retirado); //identificador que esta recebendo a atribuicao
        
        atribuiValorId(id);        
    }    
    
    private void acao_30() throws SemanticError {
        for(Token retirado : ids) {
            Identificador id = getIdentificador(retirado); //identificador que irá receber a entrada

            if (estaContidoEm(id.getTipo(), TipoID.tpDate, TipoID.tpTime, TipoID.tpBoolean)) {
                String msg = "tipo de entrada inválido: encontrado " + id.getTipo().getDescricao() + " mas era esperado int, float ou string" ;
                throw new SemanticError(msg, token);
            }

            codigoGerado.append("\n     call string [mscorlib] System.Console::ReadLine()"); //le o dado da tela
            if (id.getTipo() != TipoID.tpString) { //a entrada da tela já é string
                //codigo par converter para o tipo do identificador
                String texto = "\n     call " + id.getTipo().getTipo() + " [mscorlib] " + id.getTipo().getClasse() + "::Parse(string)";
                codigoGerado.append(texto);
            }    
            tipos.push(id.getTipo());//seta o tipo de dado que foi inserido na pilha pela funcao acima
            
            setaValorId(id);    		
    	}
    	ids.clear();
    }
    
    private void acao_31() throws SemanticError {    
        TipoID tipo = desempilhaTipo();
        if (tipo != TipoID.tpBoolean) {
            String msg = "Expressão do comando condicional inválida: esperado boolean, encontrado " + tipo.getDescricao();
            throw new SemanticError(msg, token);   
        }
        empilhaSE("brfalse");
    }    
    
    private void empilhaSE(String cmdComparacao) throws SemanticError {        
        this.qtdDesviosSE++; //incrementa a quantidade de SE's no codigo  
        String desvioSe = "_DESVIOSE" + (qtdDesviosSE);
        desviosSE.push(desvioSe); //empilha desvio para o fim da parte true de SE
        String texto = "\n     " + cmdComparacao + " " + desvioSe;
        codigoGerado.append(texto);
    }
    
    private void acao_32() {
        empilhaFimSE();
    }
    
    private void empilhaFimSE() {
        codigoGerado.append("\n").append(desviosSE.pop()).append(":"); //fim do SE/ELSE
    }
    
    private void acao_33() {
        empilhaELSE();
    }
    
    private void empilhaELSE() {
        String desvioSE = desviosSE.pop(); //pega o nome do desvio do fim da parte true do SE
        
        //cria nome desvio ELSE
        this.qtdDesviosELSE++;
        String desvioELSE = "_DESVIOELSE" + (qtdDesviosELSE);
        desviosSE.push(desvioELSE); //empilha legenda para o fim do else
        
        
        codigoGerado.append("\n     br ").append(desvioELSE); //vai para o fim do ELSE        
        codigoGerado.append("\n").append(desvioSE).append(":"); //inicio do ELSE / fim da parte true do SE
    }
    
    private void acao_34() {
        this.qtdDesviosLOOP++; //incrementa a quantidade de SE's no codigo  
        String nomeIni = "_DESVIOINILOOP" + (qtdDesviosLOOP);
        desviosLOOP.push(nomeIni); //empilha desvio para o inicio do LOOP
        codigoGerado.append("\n").append(nomeIni).append(":");
        String nomeFim = "_DESVIOFIMLOOP" + (qtdDesviosLOOP);
        desviosLOOP.push(nomeFim); //empilha desvio para o fim do LOOP
    }
    
    private void acao_35() {
        String desvioFim = desviosLOOP.pop(); //pega o nome do desvio do fim do loop 
        String desvioIni = desviosLOOP.pop(); //pega o nome do desvio do inicio do loop
        codigoGerado.append("\n     br ").append(desvioIni);
        codigoGerado.append("\n").append(desvioFim).append(":"); 
    }

    private void acao_36() {
        this.id_module = token;
        
    }











    // FUNCOES AUXILIARES **************************************************************************************










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
        
        boolean podeAlterar = (id.getTipo() == tipo) || ((id.getTipo() == TipoID.tpFloat) && (tipo == TipoID.tpInt));
        if (!podeAlterar) {
            String msg = "operação de atribuição inválida: " + id.getTipo().getDescricao() + " <- " + tipo.getDescricao();
            throw new SemanticError(msg, token); 
        }  
        //String texto = "\n     stloc " + id.getNome();
        String texto = "\n     stloc " + id.getNome();
        
        codigoGerado.append(texto); 
        id.inicializou();
    }

    public void atribuiValorId(Identificador id) throws SemanticError {
        if (this.operadorAtribuicao == null) {
            String msg = "ERRO SEMANTICO: operador atribuicao não definido";
            throw new SemanticError(msg, token);
        }

        TipoID tipo = desempilhaTipo(); //tipo do valor  a ser aplicado
       
        tipos.push(tipo); //empilha de volta tipo do valor  a ser usado no tratamento abaixo

        boolean podeAlterar = (id.getTipo() == tipo) || ((id.getTipo() == TipoID.tpFloat) && (tipo == TipoID.tpInt));
        if (!podeAlterar) {
            String msg = "operação de atribuição inválida: " + id.getTipo().getDescricao() + " <- " + tipo.getDescricao();
            throw new SemanticError(msg, token);
        }

        String aux = this.operadorAtribuicao.getLexeme();
        
        if (aux.equals("-=")) {
            //verifica se a operacao pode ser atribuida pro identificador
            boolean naoPodeAlterar = estaContidoEm(id.getTipo(), TipoID.tpString, TipoID.tpBoolean);
            if (naoPodeAlterar) {
                String msg = "operador \"" + aux + "\" é inválido para operações com o tipo " + id.getTipo().getDescricao();
                throw new SemanticError(msg, token);
            }

            //ATRIBUICAO DE SUBTRACAO - o valor original do identificador ja foi empilhado na acao_28
            subtrai(); //subtrai 
        } else if (aux.equals("+=")) {
            //verifica se a operacao pode ser atribuida pro identificador
            boolean naoPodeAlterar = estaContidoEm(id.getTipo(), TipoID.tpBoolean);
            if (naoPodeAlterar) {
                String msg = "operador \"" + aux + "\" é inválido para operações com o tipo " + id.getTipo().getDescricao();
                throw new SemanticError(msg, token);
            }
            //ATRIBUICAO DE SOMA - o valor original do identificador ja foi empilhado na acao_28            
            soma(); //soma os dois valores no topo na pilha
        } else if (!aux.equals("=")) {
            String msg = "ERRO SEMANTICO: operador inválido";
            throw new SemanticError(msg, token);
        }
        setaValorId(id); //atribui valor empilhado no identificador        
        
        this.operadorAtribuicao = null;
    }
    
    private void soma() throws SemanticError { //soma valores da pilha
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();
        
        //verifica se a operacao pode ser atribuida 
        boolean podeAlterar = false;        
        switch (tipo1) {
            case tpInt:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpFloat, TipoID.tpInt);
                break;
            case tpFloat:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpFloat, TipoID.tpInt);
                break;
            case tpDate:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpDate);
                break;
            case tpTime:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpTime);
                break;
            case tpString:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpString);
                break;    
        }        
        if (!podeAlterar) {
            String msg = "operação de soma inválida: " + tipo2.getDescricao() + " + " + tipo1.getDescricao();
            throw new SemanticError(msg, token); 
        }  
        
        if (tipo1 == TipoID.tpString) {
            codigoGerado.append("\n     call string [mscorlib]System.String::Concat(string, string)");
        } else {
            codigoGerado.append("\n     add");
        }    
        
        if (tipo1 == tipo2) {
            tipos.push(tipo1);
        } else if (estaContidoEm(TipoID.tpFloat, tipo1, tipo2)) {
            tipos.push(TipoID.tpFloat);
        } else {
            tipos.push(TipoID.tpInt);
        }            
    }
    
    private void subtrai() throws SemanticError { //subtrai valores da pilha
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();
        
        //verifica se a operacao pode ser atribuida 
        boolean podeAlterar = false;        
        switch (tipo1) {
            case tpInt:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpFloat, TipoID.tpInt);
                break;
            case tpFloat:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpFloat, TipoID.tpInt);
                break;
            case tpDate:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpDate);
                break;
            case tpTime:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpTime);
                break;
        }        
        if (!podeAlterar) {
            String msg = "operação de subtração inválida: " + tipo2.getDescricao() + " + " + tipo1.getDescricao();
            throw new SemanticError(msg, token); 
        }  
        
        codigoGerado.append("\n     sub");
        
        if (tipo1 == tipo2) {
            tipos.push(tipo1);
        } else if (estaContidoEm(TipoID.tpFloat, tipo1, tipo2)) {
            tipos.push(TipoID.tpFloat);
        } else {
            tipos.push(TipoID.tpInt);
        }  
    }
    
    private void multiplica() throws SemanticError {//mutiplica valores da pilha
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();
        
        //verifica se a operacao pode ser atribuida 
        boolean podeAlterar = false;        
        switch (tipo1) {
            case tpInt:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpFloat, TipoID.tpInt);
                break;
            case tpFloat:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpFloat, TipoID.tpInt);
                break;
        }        
        if (!podeAlterar) {
            String msg = "operação de multiplicação inválida: " + tipo2.getDescricao() + " + " + tipo1.getDescricao();
            throw new SemanticError(msg, token); 
        }  
        
        codigoGerado.append("\n     mul");
        
        if (tipo1 == tipo2) {
            tipos.push(tipo1);
        } else { //apenas int * int retornna int
            tipos.push(TipoID.tpFloat);
        }  
    }
    
    private void divide(int... tipo) throws SemanticError {//divide valores da pilha
        TipoID tipo1 = desempilhaTipo();
        TipoID tipo2 = desempilhaTipo();
        
        //verifica se a operacao pode ser atribuida 
        boolean podeAlterar = false;        
        switch (tipo1) {
            case tpInt:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpFloat, TipoID.tpInt);
                break;
            case tpFloat:  
                podeAlterar = estaContidoEm(tipo2, TipoID.tpFloat, TipoID.tpInt);
                break;
        }        
        if (!podeAlterar) {
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
                tipos.push(TipoID.tpFloat); //divisão normal sempre retorna float
                break;
            case 2: //retorna apenas parte indeira
                codigoGerado.append("\n     div"); 
                tipos.push(TipoID.tpInt); //divisão normal sempre retorna float
                break;
            case 3: //retorna apenas o resto da divisao
                codigoGerado.append("\n     rem"); 
                tipos.push(TipoID.tpInt); //divisão normal sempre retorna float
                break;    
        }               
    }      
    
    
    
}