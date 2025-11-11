import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
       
        Scanner scanner = new Scanner(System.in);
        
        while(true){
            System.out.print("$ ");
            String command = scanner.nextLine();
            if(command.trim().substring(0, 4).equals("exit")){
                break;
            }else if(command.trim().substring(0,4).equals("echo")){
                String output = command.substring(4).trim();
                System.out.println(output);
            }else if(command.trim().substring(0,4).equals("type")){
                String check = command.substring(5).trim();
                if(check.equals("echo")||check.equals("exit")||check.equals("type")) {
                    System.out.println(check+" is a shell builtin");
                }else{
                    System.out.println(check+": not found");
                }
            }
            else{
                System.out.println(command+": command not found");
            }
           
        }
        
    }
}
