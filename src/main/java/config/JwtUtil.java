package config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import model.Usuario;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Gera e valida tokens JWT — o "crachá digital" que comprova que alguém já
 * fez login, sem precisar consultar o banco de dados a cada requisição.
 *
 * Como funciona, em resumo: no login, geramos um token que carrega quem é o
 * usuário (id, perfil, turma) e é assinado com uma chave secreta só nossa.
 * A cada requisição protegida, o cliente reenvia esse token, e nós
 * verificamos a assinatura — se bater, confiamos no que está escrito nele,
 * sem precisar ir ao banco de novo.
 *
 * A chave secreta (JWT_SECRET) deve ser configurada como variável de
 * ambiente em produção — nunca deixar o valor padrão abaixo rodando na
 * internet, ele é só para desenvolvimento local.
 */
public class JwtUtil {

    private static final long DURACAO_TOKEN_MS = 8 * 60 * 60 * 1000L; // 8 horas — cobre um turno escolar

    private static final SecretKey CHAVE = Keys.hmacShaKeyFor(
            valorOuPadrao("JWT_SECRET",
                    "chave-de-desenvolvimento-local-troque-isso-em-producao-0123456789")
                    .getBytes());

    public static String gerarToken(Usuario usuario) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + DURACAO_TOKEN_MS);

        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("email", usuario.getEmail())
                .claim("perfil", usuario.getPerfil())
                .claim("turmaId", usuario.getTurmaId())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(CHAVE)
                .compact();
    }

    /**
     * Valida o token e devolve os dados nele contidos.
     * Lança io.jsonwebtoken.JwtException (ou subclasses, como
     * ExpiredJwtException) se o token for inválido, adulterado ou expirado.
     */
    public static Claims validarToken(String token) {
        return Jwts.parser()
                .verifyWith(CHAVE)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static String valorOuPadrao(String nomeVariavel, String valorPadrao) {
        String valor = System.getenv(nomeVariavel);
        return (valor == null || valor.isBlank()) ? valorPadrao : valor;
    }
}