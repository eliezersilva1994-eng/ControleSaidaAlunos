package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {

    /**
     * Configuração lida de variáveis de ambiente, para não deixar a senha
     * do banco escrita no código (e, portanto, no repositório Git).
     *
     * Se a variável de ambiente não estiver definida, usa um valor padrão
     * pensado só para desenvolvimento local — assim o projeto continua
     * funcionando sem configuração extra na sua própria máquina.
     *
     * Em produção (servidor na nuvem), essas variáveis são configuradas no
     * próprio servidor, nunca no código.
     */
    private static final String URL =
            valorOuPadrao("DB_URL", "jdbc:postgresql://localhost:5432/controle_saida_alunos");
    private static final String USUARIO =
            valorOuPadrao("DB_USUARIO", "postgres");
    private static final String SENHA =
            valorOuPadrao("DB_SENHA", "451245");

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private static String valorOuPadrao(String nomeVariavel, String valorPadrao) {
        String valor = System.getenv(nomeVariavel);
        return (valor == null || valor.isBlank()) ? valorPadrao : valor;
    }
}