package curso.api.rest.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import curso.api.rest.ApplicationContextLoad;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
@Component
public class JWTTokenAutenticacaoService {

	// Tempo de validade do token 172800000 é igual a 2 dias
	private static final long EXPIRATION_TIME = 172800000;
	//private static final long EXPIRATION_TIME = 1;

	// Uma senha unica para compor autenticação
	private static final String SECRET = "SenhaExtremamenteSecreta";
	/* Prefixo padrão de token */
	private static final String TOKEN_PREFIX = "Bearer";

	/**/
	private static final String HEADER_STRING = "Authorization";

	/* Gerando token de autenticação e adicionando ao cabeçalho e resposta Http/ */
	public void addAuthentication(HttpServletResponse response, String username) throws IOException {
		String JWT = Jwts.builder().setSubject(username)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();

		String token = TOKEN_PREFIX + " " + JWT;// Bearer 242343tettfefefef23r232

		response.addHeader(HEADER_STRING, token); // Authorization: Bearer 242343tettfefefef23r232

		ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).atualizaTokenUser(JWT,
				username);

		// Liberando resposta para portas diferentes que usam api
		liberacaoCors(response);

		// Escreve token com resposta no corpo http
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");

	}

	// Retorn o usuario validado com token ou caso não seja valido retorna null
	public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
		/* Pega o token enviado no cabeçalho http */
		String token = request.getHeader(HEADER_STRING);
		try {
			if (token != null) {
				String tokenLimpo = token.replaceFirst(TOKEN_PREFIX, "").trim();
				// Faz a validação do token do usuario na requisição
				String user = Jwts.parser().setSigningKey(SECRET)// Bearer 242343tettfefefef23r232
						.parseClaimsJws(tokenLimpo)// 242343tettfefefef23r232
						.getBody().getSubject();// Reinaldo Mendes
				if (user != null) {
					Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class)
							.findUserByLogin(user);
					if (usuario != null) {
						if (tokenLimpo.equalsIgnoreCase(usuario.getToken())) {

							// Retornar o usuario logado
							return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(),
									usuario.getAuthorities());
						}
					}

				}
			}
		} catch (ExpiredJwtException e) {
			try {
				response.getOutputStream()
						.println("Seu TOKEN está expirado faça login ou informe novo TOKEN para autenticação");
			} catch (IOException e1) {
				// e1.printStackTrace();
			}
		}
		liberacaoCors(response);
		return null;
	}

	private void liberacaoCors(HttpServletResponse response) {
		/*
		 * if (response.getHeader("Access-Control-Allow-Origin") == null) {
		 * response.addHeader("Access-Control-Allow-Origin", "*"); }
		 */
		// Libera a resposta para a porta diferente

		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
	}
}
