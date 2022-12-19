package curso.api.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import curso.api.rest.service.ImplementacaoUserDetailsService;

/*Mapeia url, endereços, autoriza ou bloqueia acesso a url*/
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	// Configura as solicitacoes de acessos por http
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Activando a proteção contra usuarios que estão validados por token
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())

				// Ativar a permição a url da pagina inicial do sistama ex:
				// sistema.com/index.html
				.disable().authorizeRequests().antMatchers("/").permitAll().antMatchers("/index", "/recuperar/**").permitAll()
				.antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
				// URL de logout - redireciona o user ao deslogar
				.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")

				// Mapea url de logout e invalida o usuario
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

				// Filtra as requisiçoes de login para autenticação
				.and()
				.addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
						UsernamePasswordAuthenticationFilter.class)

				// Filtra demais requisiçoes para verificar a presença do token JWT no Header
				// HTTP
				.addFilterBefore(new JWTApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Service que irá consultar o usuario no banco
		auth.userDetailsService(implementacaoUserDetailsService)
				// padrao de codificação de senha
				.passwordEncoder(new BCryptPasswordEncoder());
	}
}
