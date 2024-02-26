import java.io.*;

public class ListTest_lkn {
    public static void main(String[] args) {
        List<Character> list = new AList<>(); // 在这里修改实现类，例如 ArrayList
        //List<Character> list = new SinglyLinkedList<>();
        //List<Character> list = new DoublyLinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("list_testcase.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                executeCommands(line, list);
                showStructure(list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ListException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeCommands(String commands, List<Character> list) throws ListException {
        String[] commandArray = commands.split("\\s+");

        for (String command : commandArray) {
            executeCommand(command, list);
        }
    }

    private static void executeCommand(String command, List<Character> list) throws ListException {
        char operation = command.charAt(0);
        char value = command.length() > 1 ? command.charAt(1) : 0; // Assuming commands like "+a"

        switch (operation) {
            case '+':
                list.insert(value);
                break;
            case '-':
                list.remove();
                break;
            case '=':
                list.replace(value);
                break;
            case '#':
                list.gotoBeginning();
                break;
            case '*':
                list.gotoEnd();
                break;
            case '>':
                list.gotoNext();
                break;
            case '<':
                list.gotoPrev();
                break;
            case '~':
                list.clear();
                break;
            default:
                // Handle unknown command or ignore
                break;
        }
    }

    private static void showStructure(List<Character> list) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(System.out);
        list.showStructure(pw);
        pw.flush();
    }
}
