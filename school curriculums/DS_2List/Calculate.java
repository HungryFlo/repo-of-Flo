import 准备工作.LStack;
import java.util.Scanner;

public class Calculate {
    public static void main(String[] args){
        //提示用户输入
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Please input an arithmetic expression, split every character with space:");

        //读取用户输入
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();

        //进行计算操作
        Calculate cal = new Calculate();
        try {
            System.out.println("= " + cal.calculate(input));
        } catch (InputException e) {
            System.out.println(e.message);
        }
        System.out.println("------------------------------------------------------------------------");
    }

    public double calculate(String input) throws InputException {
        //创建两个栈
        LStack<Operator> opStack = new LStack<>();
        LStack<Double> numStack = new LStack<>();

        //分割字符串
        String[] sList = input.split(" ");

        //创建一些公用的变量
        Operator temp;
        char curr;

        //进行计算操作
        for (String s :
                sList) {
            try {
                numStack.push(Double.parseDouble(s));
            } catch (Exception e){
                if(s.length() == 1 && isValidOperator(s.charAt(0))){
                    curr = s.charAt(0);
                    if(curr == ')'){
                        while((temp = opStack.pop()).operator != '('){
                            operate(temp,numStack);
                            //检查括号不匹配现象
                            if(opStack.isEmpty()){
                                throw new InputException("There is a redundant ')'!");
                            }
                        }
                    } else {
                        //创建一个操作符对象
                        temp = new Operator(curr);
                        if (!opStack.isEmpty() && (temp.precedence <= opStack.topValue().precedence) && ((temp.precedence != opStack.topValue().precedence) || !temp.associativity)) {
                            while (temp.precedence < opStack.topValue().precedence || (temp.precedence == opStack.topValue().precedence && !temp.associativity)) {
                                operate(opStack.pop(), numStack);
                                if (opStack.isEmpty()) break;
                            }
                        }
                        //入栈时调用bracketInStack()方法来修改左括号的优先级
                        temp.bracketInStack();
                        opStack.push(temp);
                    }
                } else {
                    throw new InputException("Input Error!");
                }
            }
        }

        //进行最终的计算操作
        while(!opStack.isEmpty()){
            temp = opStack.pop();
            //判断左括号是否冗余
            if(temp.operator == '('){
                throw new InputException("There is a redundant '('!");
            }
            operate(temp, numStack);
        }

        return numStack.topValue();
    }

    //将具体的计算单独写成一个方法
    private void operate(Operator curr, LStack<Double> numStack){
        double x2 = numStack.pop();
        double x1 = numStack.pop();
        switch (curr.operator) {
            case '+' -> numStack.push(x1 + x2);
            case '-' -> numStack.push(x1 - x2);
            case '*' -> numStack.push(x1 * x2);
            case '/' -> numStack.push(x1 / x2);
            case '^' -> numStack.push(Math.pow(x1, x2));
        }
    }

    //判断是否是有效的操作符
    public boolean isValidOperator(char operator){
        return operator == '+' || operator == '-'
                || operator == '^' || operator == '*'
                || operator == '/' ||operator == '('
                || operator == ')' ;
    }
}
