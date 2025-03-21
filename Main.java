import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    private static final String ARQUIVO_JSON = "usuarios.json";
    private static List<Usuario> usuarios = new ArrayList<>();
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        carregarUsuarios();

        Scanner scanner = new Scanner(System.in);
        System.out.println("1 - Registrar");
        System.out.println("2 - Login");
        System.out.print("Escolha uma opção: ");
        int opcao = scanner.nextInt();
        scanner.nextLine(); // consumir quebra de linha

        if (opcao == 1) {
            registrar(scanner);
        } else if (opcao == 2) {
            logar(scanner);
        } else {
            System.out.println("Opção inválida.");
        }
    }

    private static void registrar(Scanner scanner) {
        System.out.print("Digite o nome de usuário: ");
        String username = scanner.nextLine();
        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        // Verifica se já existe
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username)) {
                System.out.println("Usuário já existe!");
                return;
            }
        }

        Usuario novo = new Usuario(username, senha);
        usuarios.add(novo);
        salvarUsuarios();
        System.out.println("Usuário registrado com sucesso!");
    }

    private static void logar(Scanner scanner) {
        System.out.print("Digite o nome de usuário: ");
        String username = scanner.nextLine();
        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username) && u.getSenha().equals(senha)) {
                System.out.println("Login bem-sucedido! Bem-vindo, " + username + "!");
                return;
            }
        }

        System.out.println("Login falhou. Usuário ou senha incorretos.");
    }

    private static void carregarUsuarios() {
        try (Reader reader = new FileReader(ARQUIVO_JSON)) {
            Type listType = new TypeToken<List<Usuario>>() {}.getType();
            usuarios = gson.fromJson(reader, listType);
            if (usuarios == null) {
                usuarios = new ArrayList<>();
            }
        } catch (FileNotFoundException e) {
            // Primeiro uso, arquivo ainda não existe
            usuarios = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void salvarUsuarios() {
        try (Writer writer = new FileWriter(ARQUIVO_JSON)) {
            gson.toJson(usuarios, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
