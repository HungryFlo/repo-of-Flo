public class Operator {
    char operator;
    int precedence;
    boolean associativity;

    public Operator(){}

    public Operator(char operator){
        this.operator = operator;
        this.precedence = precedence(operator);
        this.associativity = associativity(operator);
    }

    private int precedence(char operator){
        if(operator == '+' || operator == '-'){
            return 10;
        } else if (operator == '^') {
            return 1000;
        } else if(operator == '*' || operator == '/'){
            return 100;
        } else if (operator == '(') {
            return 10000;
        } else {
            System.out.println("Not a valid operator!");
            return 0;
        }
    }

    private boolean associativity(char operator){
        if(operator == '^'){
            return true;
        }
        return false;
    }

    public void bracketInStack(){
        if(this.operator == '('){
            this.precedence = 1;
        }
    }
}
