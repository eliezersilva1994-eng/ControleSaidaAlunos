package app;

import config.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Set;

/**
 * Ponto único de checagem de autenticação/autorização para os controllers.
 *
 * Uso típico, no início de um método de endpoint protegido:
 *   ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN);
 * ou, quando mais de um perfil pode acessar:
 *   ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN, Usuario.PERFIL_SECRETARIA);
 *
 * Se o token estiver ausente/inválido/expirado, ou o perfil não estiver na
 * lista permitida, lança NaoAutorizadoException — tratada globalmente por
 * TratadorErrosGlobais, então o controller não precisa de try/catch para isso.
 */
public class ContextoAutenticacao {

    private ContextoAutenticacao() {
    }

    public static UsuarioAutenticado autenticar(HttpServletRequest request) {
        String cabecalho = request.getHeader("Authorization");
        if (cabecalho == null || !cabecalho.startsWith("Bearer ")) {
            throw new NaoAutorizadoException("Faça login para acessar este recurso.", 401);
        }

        String token = cabecalho.substring("Bearer ".length());
        try {
            Claims claims = JwtUtil.validarToken(token);
            int id = Integer.parseInt(claims.getSubject());
            String email = claims.get("email", String.class);
            String perfil = claims.get("perfil", String.class);
            Integer turmaId = claims.get("turmaId", Integer.class);
            return new UsuarioAutenticado(id, email, perfil, turmaId);

        } catch (JwtException | IllegalArgumentException e) {
            throw new NaoAutorizadoException("Sessão inválida ou expirada. Faça login novamente.", 401);
        }
    }

    public static UsuarioAutenticado exigirPerfil(HttpServletRequest request, String... perfisPermitidos) {
        UsuarioAutenticado usuario = autenticar(request);

        Set<String> permitidos = Set.of(perfisPermitidos);
        if (!permitidos.contains(usuario.perfil())) {
            throw new NaoAutorizadoException(
                    "Seu perfil (" + usuario.perfil() + ") não tem permissão para esta ação.", 403);
        }
        return usuario;
    }
}