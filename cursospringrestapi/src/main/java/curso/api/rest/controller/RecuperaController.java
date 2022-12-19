package curso.api.rest.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import curso.api.rest.ObjetoError;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ServiceEnviaEmail;

@RestController
@RequestMapping(value = "/recuperar")
public class RecuperaController {
	@Autowired /* Se fosse CDI seria @Inject */
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private ServiceEnviaEmail enviaEmail;

	@ResponseBody
	@PostMapping(value = "/")
	public ResponseEntity<ObjetoError> recuperar(@RequestBody Usuario login) throws Exception {
		ObjetoError objetoError = new ObjetoError();
		System.out.println(login.getLogin());
		Usuario user = usuarioRepository.findUserByLogin(login.getLogin());
		if (user == null) {
			objetoError.setCode("404");
			objetoError.setError("Usuario não encontrado!");
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String senhaNova=dateFormat.format(Calendar.getInstance().getTime());
			usuarioRepository.updateSenha(new BCryptPasswordEncoder().encode(senhaNova), user.getId());
			
			enviaEmail.enviarEmail("Recuperação de Senha",user.getLogin(),"Sua senha nova: "+ senhaNova);
			objetoError.setCode("200");
			objetoError.setError("Acesso enviado para o seu email!");
		}
		return new ResponseEntity<ObjetoError>(objetoError, HttpStatus.OK);
	}
}
