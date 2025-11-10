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
                System.out.print(output);
            }else{
                System.out.println(command+": command not found");
            }
           
        }
        
    }
}
