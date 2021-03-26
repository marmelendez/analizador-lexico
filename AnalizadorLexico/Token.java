package AnalizadorLexico;

public class Token {
    
    //ATRIBUTOS
    Tipo tipo;
    String valor;

    //METODOS

    /* Constructor */
    Token (){
        this.tipo = Tipo.ERROR; 
        this.valor = " ";
    }
    /* Getters y setters */
    public String getValor() {
        return this.valor;
    }

    public Tipo getTipo() {
        return this.tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    /* Obtiene índice en la tabla de transición que representa un símbolo */
    public int indiceAlfabeto(char simbolo){
        int indice = -1;
        if (simbolo == 'E' || simbolo == 'e'){
            indice = 11;
        } else if (Character.isLetter(simbolo)){
            indice = 0;
        } else if (Character.isDigit(simbolo)){
            indice = 1;
        } else if (simbolo == '_'){
            indice = 2;
        } else if (simbolo == '='){
            indice = 3;
        } else if (simbolo == '+'){
            indice = 4;
        } else if (simbolo == '-'){
            indice = 5;
        } else if (simbolo == '*'){
            indice = 6;
        } else if (simbolo == '/'){
            indice = 7;
        } else if (simbolo == '^'){
            indice = 8;
        } else if (simbolo == '('){
            indice = 9;
        } else if (simbolo == ')'){
            indice = 10;
        } else if (simbolo == '.'){
            indice = 12;
        } 
        return indice;
    }

    /* Obtiene el tipo de token que es de acuerdo a su estado actual */
    public Tipo getTipo(int estado){
        switch (estado){
            case 1: 
                return Tipo.VARIABLE;
            case 2:
                return Tipo.ASIGNACION;
            case 3: 
                return Tipo.SUMA;
            case 4:
                return Tipo.RESTA;
            case 5:
                return Tipo.MULTIPLICACION;
            case 6:
                return Tipo.DIVISION;
            case 13:
                return Tipo.COMENTARIO;
            case 7:
                return Tipo.POTENCIA;
            case 8:
                return Tipo.PARENTESIS_QUE_ABRE;
            case 9:
                return Tipo.PARENTESIS_QUE_CIERRA;
            case 10:
                return Tipo.ENTERO;
            case 11:
                return Tipo.REAL;
            case 12:
                return Tipo.REAL;
            case 14:
                return Tipo.REALEXP;
            case 15:
                return Tipo.REALEXP;
            default:
                return Tipo.ERROR;
        }
    }
}
