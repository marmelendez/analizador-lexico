package AnalizadorLexico;

import java.util.Vector;
import java.io.File; 
import java.io.FileNotFoundException;  
import java.util.Scanner; 

public class Analizador {

    //ATRIBUTOS
    int alfabeto; //Columnas de la tabla: representa simbolos
    int estado; //Filas de la tabla: representa estado actual
    boolean flag; //Control de impresiones de tokens y evalución con estado inicial
    char[] charArr; //Arreglo de chars de cada línea de entrada
    Token token = new Token(); //Token: contiene tipo y valor del token
    Vector<String> texto = new Vector<>(); //Vector que almacena cada línea del archivo de entrada .txt
    int[][] tabla = { //Tabla de transición 
        {1, 10, -1, 2, 3, 4, 5, 6, 7, 8, 9, 1, -1},
        {1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, 13, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {-1, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11},
        {-1, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, 14, -1},
        {-1, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
        {13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13},
        {-1, 12, -1, -1, -1, 15, -1, -1, -1, -1, -1, -1, -1},
        {-1, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
    };   


    //METODOS

    /* Lee un nombreArchivo que representa la ruta donde se encuentra el archivo
    Guarda contenido del archivo en un vector<String>
    Si no puede abrirlo manda una excepción */
    private void leerArchivo(String nombreArchivo){
        try {
            File archivo = new File(nombreArchivo);
            Scanner scan = new Scanner(archivo);
            while (scan.hasNextLine()) { //Mientras exista una línea siguiente
                this.texto.add(scan.nextLine()); //Guarda la línea como string en el vector
            }
            scan.close();
        }

        catch (FileNotFoundException e) { //Si recibe la excepción FileNotFoundException le informa al usuario
            System.out.println("Error, no se encontro el archivo con ese nombre");
            e.printStackTrace();
        }
    }

    /* Muestra en pantalla los el valor y tipo del token identificado */
    private void imprimirTokenTipo(){
        System.out.printf("%-25s %-20s %n", this.token.getValor(), this.token.getTipo());
    }

    /* Evalua si el siguiente símbolo de la linea de chars será válido */
    private boolean simboloEsAceptable(int j){
        if (j+1 <this.charArr.length && this.token.indiceAlfabeto(this.charArr[j+1]) >= 0){ //Si el símbolo que sigue de leer no es el último y si es aceptable
            this.alfabeto = this.token.indiceAlfabeto(this.charArr[j+1]); //Guarda nuevo valor de columna (simbolos) de la tabla
            return true;
        }
        return false;
    }
    /* Si el tipo del actual símbolo es VARIABLE guarda su valor hasta que reciba un símbolo no aceptable */
    private void evaluarVariable(int j){
        this.token.setValor(this.token.getValor().concat(String.valueOf(this.charArr[j]))); //Agrega símbolo al valor actual del token
        if (simboloEsAceptable(j)){ 
            this.estado = this.tabla[this.estado][this.alfabeto];
            if (this.token.getTipo(this.estado) == Tipo.VARIABLE){ //Si el estado del siguiente símbolo es de tipo VARIABLE
                this.flag = false; //No realiza evaluacion desde estado inicial con el siguiente símbolo(se mantiene en estado actual) y no imprime token
            }else{
                this.flag = true; //Vuelve al estado inicial
            }
        } else{ //Si es el fin de línea o sigue un símbolo que no es variable (que no sea letras, digitos o guion bajo)
            this.flag = true; //Vuelve al estado inicial
        }
    }

    /* Si el tipo del actual símbolo es DIVISIÓN evalua si es división o comentario */
    private void evaluarDivision(int j) { 
        this.token.setValor(String.valueOf(this.charArr[j])); 
        if (simboloEsAceptable(j)){ 
            this.estado = this.tabla[this.estado][this.alfabeto];
            if (this.token.getTipo(this.estado) == Tipo.COMENTARIO){ //Si el estado del siguiente símbolo es de tipo de COMENTARIO
                this.token.setTipo(token.getTipo(this.estado)); //Actualiza tipo de token 
                this.flag = false; //No realiza evaluacion desde estado inicial con el siguiente símbolo(se mantiene en estado actual) y no imprime token
            }
        }
    }

    /* Si el tipo del actual símbolo es COMENTARIO lo guarda hasta llegar al final de línea */
    private void evaluarComentario(int j){
        this.token.setValor(this.token.getValor().concat(String.valueOf(this.charArr[j]))); //Agrega símbolo al valor actual del token
        if (j==this.charArr.length-1){ //Si llego al final de línea
            this.flag = true; //Vuelve al estado actual al inicial
        }
    }

    /* Si el tipo del actual símbolo es ENTERO evalua si es entero o real */
    private void evaluarEntero(int j){
        this.token.setValor(this.token.getValor().concat(String.valueOf(this.charArr[j]))); //Agrega símbolo al valor actual del token
        if (simboloEsAceptable(j)){
            this.estado = this.tabla[this.estado][this.alfabeto];
            if (this.token.getTipo(this.estado) == Tipo.REAL  || this.token.getTipo(this.estado) == Tipo.ENTERO || this.token.getTipo(this.tabla[0][this.alfabeto]) == Tipo.VARIABLE || this.token.getTipo(this.tabla[0][this.alfabeto])== Tipo.ERROR){ //Evalua el estado del siguiente símbolo 
                this.token.setTipo(this.token.getTipo(this.estado)); //Actualiza tipo de token 
                this.flag = false; //No realiza evaluacion desde estado inicial con el siguiente símbolo(se mantiene en estado actual) ni imprime token
            }else{
                this.flag = true; //Vuelve al estado inicia
            }
        } else { //Si es llego al final de linea o recibe otro tipo de simbolo
            this.flag = true; //Vuelve al estado inicial
        }
    }

    /* Si el tipo del actual símbolo es REAL con E/e (exponente) evalua si aceptable o no */
    private void evaluarRealExp(int j){
        this.token.setValor(this.token.getValor().concat(String.valueOf(this.charArr[j]))); //Agrega símbolo al valor actual del token
        if (simboloEsAceptable(j)){
            this.estado = this.tabla[this.estado][this.alfabeto];
            if (token.getTipo(this.estado) == Tipo.REAL || token.getTipo(this.estado) == Tipo.REALEXP){ //Si el siguiente símbolo NO ES de tipo REAL
                this.token.setTipo(this.token.getTipo(this.estado));  //Actualiza tipo de token
                this.flag = false; //Vuelve al estado inicial
            }else if (token.getTipo(this.estado) == Tipo.ERROR) {//Si el siguiente símbolo de tipo ERROR
                this.token.setTipo(this.token.getTipo(this.estado));  //Actualiza tipo de token
                this.flag = true; //Vuelve al estado inicial
            } else {
                this.flag = true; //Vuelve al estado inicial
            }
        } else { //Si llego al final de la línea o recibe otro tipo de símbolo
            this.token.setTipo(this.token.getTipo(this.estado)); //Actualiza tipo de token
        }
    }

    /* Si el tipo del actual símbolo es REAL guarda símbolos hasta que reciba símbolo no aceptable */
    private void evaluarReal(int j){
        this.token.setValor(this.token.getValor().concat(String.valueOf(this.charArr[j]))); //Agrega símbolo al valor actual del token
        if (simboloEsAceptable(j)){
            this.estado = this.tabla[this.estado][this.alfabeto];
            if (token.getTipo(this.estado) == Tipo.REAL){ //Si el siguiente símbolo es un E/e (exponente) o numero, de tipo REAL O REALEXP
                this.flag = false; //Se mantiene en ese estado y no imprime token
            } else if (token.getTipo(this.estado) == Tipo.REALEXP){
                this.token.setTipo(this.token.getTipo(this.estado)); //Actualiza estado 
            } else if (this.token.getTipo(this.tabla[0][this.alfabeto]) != Tipo.ERROR && this.token.getTipo(this.tabla[0][this.alfabeto]) != Tipo.VARIABLE){
                this.flag = true; //Vuelve al estado inicial
            } else if (token.getTipo(this.estado) == Tipo.ERROR || this.token.getTipo(this.tabla[0][this.alfabeto])== Tipo.VARIABLE){
                this.token.setTipo(this.token.getTipo(this.estado));  //Actualiza estado 
            }else{
                this.flag = true; //Vuelve al estado inicial
            }
        } else { //Si llego al final de la línea o recibe otro tipo de símbolo
            this.flag = true; //Vuelve al estado inicial
        }
    }

    /* Si el tipo del actual símbolo es ERROR guarda símbolos hasta que reciba símbolo aceptable */
    private void evaluarError(int j){
        this.token.setValor(this.token.getValor().concat(String.valueOf(this.charArr[j])));
        if (simboloEsAceptable(j) && this.charArr[j] !=' '){
            this.estado = this.tabla[0][this.alfabeto];
            if (token.getTipo(this.estado) == Tipo.VARIABLE || token.getTipo(this.estado) == Tipo.ENTERO){ //Si el siguiente símbolo es un E/e (exponente) o numero, de tipo REAL O REALEXP
                this.flag = false; //Se mantiene en ese estado y no imprime token
            }else{
                this.flag = true; //Vuelve al estado inicial
            }
        } else { //Si llego al final de la línea o recibe otro tipo de símbolo
            this.flag = true; //Vuelve al estado inicial
        }
    }

    /* Identifica el tipo de token que es el token actual y manda a llamar a funciones según el tipo */
    private void tipoDeToken(int j){
        switch(this.token.getTipo()){ //Obtiene tipo del token actual
            case VARIABLE:  
                evaluarVariable(j);   
                break; 
            case DIVISION: 
                evaluarDivision(j);
                break;
            case ENTERO:
                evaluarEntero(j);
                break;
            case REALEXP:
                evaluarRealExp(j);
                break;
            case REAL: 
                evaluarReal(j);
                break;
            case COMENTARIO: 
                evaluarComentario(j);   
                break;
            case ERROR:
                evaluarError(j);
                break;
            default:
                this.token.setValor(String.valueOf(this.charArr[j]));
        }
    }

    /* Evalua símbolo que se encuentra en estado inicial*/
    private void evaluarInicio(int j){
        this.estado = 0;
        this.token.setValor("");
        this.alfabeto = this.token.indiceAlfabeto(charArr[j]); //Obtiene indice del simbolo en la tabla de transición
        if (this.alfabeto >= 0){ //Si es un símbolo aceptado 
            this.estado = this.tabla[this.estado][this.alfabeto]; 
            this.token.setTipo(this.token.getTipo(this.estado)); //Actualiza tipo de token
        }else{
            this.token.setTipo(this.token.getTipo(-1));
        }
    }

    /* Controla la evaluación de todos los símbolos de cada línea de entrada */
    private void identificarToken(){
        this.flag = true; // Inicializa flag que controla impresion de token y evaluación inicial 
        for (int j=0; j<this.charArr.length;j++){ //Recorre todos los símbolos de una línea
            if (this.flag){ 
                evaluarInicio(j); //En algunos casos al ser una variable, comentario o numero no vuelve a realizar la evaluación inicial hasta cambiar de estado
            }
            tipoDeToken(j); // Identifica el tipo de token de los siguientes simbolos
            if (this.flag && this.charArr[j]!= ' ') { //Si la flag permanece en true y simbolo no es un espacio en blanco
                imprimirTokenTipo(); //Imprime el valor y tipo de token identificado
            }
        }
    }

    /* Manda cada línea del archivo de entrada a función de identificarToken */
    public void lexerAritmetico(String archivo){
        leerArchivo(archivo);
        String linea;
        System.out.printf("%-25s %-20s %n", "Token", "Tipo");
        for (int i=0; i< this.texto.size();i++){ //Por cada línea el archivo
            linea = this.texto.get(i);
            this.charArr= linea.toCharArray(); //Transforma línea a arreglo de chars
            identificarToken(); //Llama a funcion para identificar token y su tipo
        }
    }

    public static void main(String[] args) {
        Analizador a = new Analizador(); //Se crea objeto de la clase Analizador
        a.lexerAritmetico("expresiones.txt"); //Se manda a llamar a su función lexerAritmetico y se pasa como parametro el nombre de archivo a leer
    }
} 