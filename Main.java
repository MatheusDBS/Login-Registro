import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Main {
    private static final String USUARIOS_FILE = "usuarios.json";
    private static final String BLOQUEADOS_FILE = "bloqueados.json";
    private static final Gson gson = new Gson();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n1. Registrar");
            System.out.println("2. Login");
            System.out.println("3. Sair");
            System.out.print("Escolha uma opção: ");
            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    registrarUsuario();
                    break;
                case "2":
                    fazerLogin();
                    break;
                case "3":
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void registrarUsuario() {
        System.out.print("Nome de usuário: ");
        String nome = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        List<Usuario> usuarios = carregarUsuarios();
        usuarios.add(new Usuario(nome, senha));
        salvarUsuarios(usuarios);
        System.out.println("Usuário registrado com sucesso!");
    }

    private static void fazerLogin() {
        System.out.print("Nome de usuário: ");
        String nome = scanner.nextLine();

        // Verifica se está bloqueado
        if (estaBloqueado(nome)) {
            System.out.println("⚠️ Usuário bloqueado por múltiplas tentativas de login.");
            return;
        }

        List<Usuario> usuarios = carregarUsuarios();
        Optional<Usuario> usuarioOptional = usuarios.stream()
                .filter(u -> u.getNome().equals(nome))
                .findFirst();

        if (usuarioOptional.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        int tentativas = 0;
        while (tentativas < 5) {
            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            if (usuarioOptional.get().getSenha().equals(senha)) {
                System.out.println("✅ Login bem-sucedido!");
                return;
            } else {
                tentativas++;
                System.out.println("❌ Senha incorreta. Tentativas restantes: " + (5 - tentativas));
            }
        }

        // Após 5 tentativas:
        System.out.println("🚫 Usuário bloqueado por excesso de tentativas.");
        bloquearUsuario(nome);
    }

    private static List<Usuario> carregarUsuarios() {
        try (Reader reader = new FileReader(USUARIOS_FILE)) {
            Type listType = new TypeToken<List<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(reader, listType);
            return usuarios != null ? usuarios : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>(); // Retorna uma lista vazia se houver erro ao carregar
        }
    }
    

    private static void salvarUsuarios(List<Usuario> usuarios) {
        try (Writer writer = new FileWriter(USUARIOS_FILE)) {
            gson.toJson(usuarios, writer);
        } catch (IOException e) {
            System.out.println("Erro ao salvar usuários.");
        }
    }

    private static void bloquearUsuario(String nome) {
        Set<String> bloqueados = carregarBloqueados();
        bloqueados.add(nome);
        try (Writer writer = new FileWriter(BLOQUEADOS_FILE)) {
            gson.toJson(bloqueados, writer);
        } catch (IOException e) {
            System.out.println("Erro ao bloquear usuário.");
        }
    }

    private static boolean estaBloqueado(String nome) {
        Set<String> bloqueados = carregarBloqueados();
        return bloqueados.contains(nome);
    }

    private static Set<String> carregarBloqueados() {
        try (Reader reader = new FileReader(BLOQUEADOS_FILE)) {
            Type setType = new TypeToken<Set<String>>() {}.getType();
            Set<String> bloqueados = gson.fromJson(reader, setType);
            return bloqueados != null ? bloqueados : new HashSet<>();
        } catch (IOException e) {
            return new HashSet<>(); // retorna lista vazia se arquivo não existir
        }
    }
    
}
