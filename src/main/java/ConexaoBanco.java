import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    private static final String URL = "jdbc:postgresql://localhost:5432/controle_saida_alunos";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "451245"; // troca pela senha que você definiu na instalação

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}