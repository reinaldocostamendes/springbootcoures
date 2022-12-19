package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import curso.api.rest.model.UserChart;
import curso.api.rest.model.UserReport;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.TelefoneRepsoitory;
import curso.api.rest.repository.UsuarioRepository;
import curso.api.rest.service.ImplementacaoUserDetailsService;
import curso.api.rest.service.ServiceRelatorio;

//@CrossOrigin(origins = {"*","https://www.google.pt"})//Liberar acesso a dominios no array
//@CrossOrigin libera acesso a todos os dominios
@RestController
@RequestMapping(value = "/usuario")
public class IndexController {
	@Autowired /* Se fosse CDI seria @Inject */
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TelefoneRepsoitory telefoneRepsoitory;

	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;

	@Autowired
	private ServiceRelatorio serviceRelatorio;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/*
	 * @GetMapping(value = "/", produces = "application/json") public ResponseEntity
	 * init(@RequestParam(value = "nome", required = true, defaultValue =
	 * "Nome não foi informado!") String nome, @RequestParam(value = "salario") Long
	 * salario) { System.out.println("parametro recebido: "+nome); return new
	 * ResponseEntity("Olá REST SpringBoot o seu nome é: "+nome+", Salário: "
	 * +salario, HttpStatus.OK); }
	 */
	/*
	 * @GetMapping(value = "/", produces = "application/json") public
	 * ResponseEntity<Usuario> init() { Usuario usuario =new Usuario();
	 * usuario.setId(20L); usuario.setLogin("admin@reinaldo.com");
	 * usuario.setNome("Reinaldo Mendes"); usuario.setSenha("admin");
	 * 
	 * return ResponseEntity.ok(usuario); }
	 */
	/*
	 * @GetMapping(value = "/", produces = "application/json") public
	 * ResponseEntity<List<Usuario>> init() { List<Usuario> usuarios = new
	 * ArrayList<Usuario>(); Usuario usuario1 = new Usuario(); usuario1.setId(20L);
	 * usuario1.setLogin("admin@reinaldo.com"); usuario1.setNome("Reinaldo Mendes");
	 * usuario1.setSenha("admin");
	 * 
	 * Usuario usuario2 = new Usuario(); usuario2.setId(23L);
	 * usuario2.setLogin("admin@reinaldocostamendes.com");
	 * usuario2.setNome("Reinaldo Costa Mendes"); usuario2.setSenha("admin");
	 * usuarios.add(usuario1); usuarios.add(usuario2); return
	 * ResponseEntity.ok(usuarios); }
	 */
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws IOException {
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		// **Consumindo API publica Externa
		URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		String cep = "";
		StringBuilder jsonCep = new StringBuilder();

		while ((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		System.out.println(jsonCep.toString());
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);

		usuario.setCep(userAux.getCep());
		usuario.setLogradouro(userAux.getLogradouro());
		usuario.setComplemento(userAux.getComplemento());
		usuario.setBairro(userAux.getBairro());
		usuario.setLocalidade(userAux.getLocalidade());
		usuario.setUf(userAux.getUf());
		// **Consumindo API publica Externa
		String senhaCriptografado = new BCryptPasswordEncoder().encode(usuario.getSenha());
		usuario.setSenha(senhaCriptografado);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		implementacaoUserDetailsService.insereAcessoPadrao(usuarioSalvo.getId());
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PostMapping(value = "/vendausuario", produces = "application/json")
	public ResponseEntity<Usuario> cadastrarvenda(@RequestBody Usuario usuario) throws Exception {
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		Usuario usuarioTemp = usuarioRepository.findUserByLogin(usuario.getLogin());
		if (!usuarioTemp.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografado = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografado);
		}
		// **Consumindo API publica Externa
		URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
		URLConnection connection = url.openConnection();
		InputStream is = connection.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		String cep = "";
		StringBuilder jsonCep = new StringBuilder();

		while ((cep = br.readLine()) != null) {
			jsonCep.append(cep);
		}
		System.out.println(jsonCep.toString());
		Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);

		usuario.setCep(userAux.getCep());
		usuario.setLogradouro(userAux.getLogradouro());
		usuario.setComplemento(userAux.getComplemento());
		usuario.setBairro(userAux.getBairro());
		usuario.setLocalidade(userAux.getLocalidade());
		usuario.setUf(userAux.getUf());
		// **Consumindo API publica Externa
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@PostMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity cadastrarvenda(@PathVariable Long iduser, @PathVariable Long idvenda) {
		return new ResponseEntity("iduser: " + iduser + " ivenda: " + idvenda, HttpStatus.OK);
	}

	@PutMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity atualizarvenda(@PathVariable Long iduser, @PathVariable Long idvenda) {
		return new ResponseEntity("atualizar venda iduser: " + iduser + " ivenda: " + idvenda, HttpStatus.OK);
	}

	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) throws Exception {
		for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
			usuario.getTelefones().get(pos).setUsuario(usuario);
		}
		Usuario usuarioTemp = usuarioRepository.findById(usuario.getId()).get();

		if (!usuarioTemp.getSenha().equals(usuario.getSenha())) {
			String senhaCriptografado = new BCryptPasswordEncoder().encode(usuario.getSenha());
			usuario.setSenha(senhaCriptografado);
		}
		if (!usuario.getCep().equalsIgnoreCase(usuarioTemp.getCep())) {
			// **Consumindo API publica Externa
			URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

			String cep = "";
			StringBuilder jsonCep = new StringBuilder();

			while ((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			System.out.println(jsonCep.toString());
			Usuario userAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);

			usuario.setCep(userAux.getCep());
			usuario.setLogradouro(userAux.getLogradouro());
			usuario.setComplemento(userAux.getComplemento());
			usuario.setBairro(userAux.getBairro());
			usuario.setLocalidade(userAux.getLocalidade());
			usuario.setUf(userAux.getUf());
			// **Consumindo API publica Externa
		}
		System.out.println("Data>>>>>>>> " + usuario.getDataNascimento());
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		return new ResponseEntity<Usuario>(usuarioSalvo, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}/codigovenda/{venda}", produces = "application/json")
	public ResponseEntity<Usuario> relatorio(@PathVariable("id") Long id, @PathVariable("venda") Long venda) {
		Usuario u = usuarioRepository.findById(id).get();
		return new ResponseEntity(u, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = "application/json")
	@Cacheable("cacheuser")
	@CacheEvict(value = "cacheuser", allEntries = true)
	@CachePut("cacheuser")
	public ResponseEntity<Usuario> init(@PathVariable("id") Long id) {
		Usuario u = usuarioRepository.findById(id).get();
		System.out.println("Data peguei: " + u.getDataNascimento());
		return new ResponseEntity<Usuario>(u, HttpStatus.OK);
	}

	/*
	 * @GetMapping(value = "v2/{id}", produces = "application/json") public
	 * ResponseEntity<Usuario> initV2(@PathVariable("id") Long id) { Usuario u =
	 * usuarioRepository.findById(id).get(); return new ResponseEntity(u,
	 * HttpStatus.OK); }
	 */

	// @CrossOrigin(origins = {"https://localhost:8080"})
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable("id") Long id) {
		usuarioRepository.deleteById(id);
		return "Eliminado com sucesso!";
	}

	// @CrossOrigin(origins = {"https://localhost:8080"})
	/*
	 * @DeleteMapping(value = "/{id}", produces = "application/text", headers =
	 * "X-API-Version=v2") public ResponseEntity<Usuario>
	 * deleteV2(@PathVariable("id") Long id) { usuarioRepository.deleteById(id);
	 * return new ResponseEntity("Eliminado com sucesso!",HttpStatus.OK); }
	 */
	@GetMapping(value = "/", produces = "application/json")
	@Cacheable("cacheusuarios")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuarios() throws InterruptedException {

		PageRequest page = PageRequest.of(0, 5, Sort.by("nome"));
		Page<Usuario> list = usuarioRepository.findAll(page);
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/page/{pagina}", produces = "application/json")
	@Cacheable("cacheusuarios")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuariosPagina(@PathVariable("pagina") int pagina)
			throws InterruptedException {
		if (pagina > 0) {
			pagina = pagina - 1;
		}
		PageRequest page = PageRequest.of(pagina, 5, Sort.by("nome"));
		Page<Usuario> list = usuarioRepository.findAll(page);
		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@Cacheable("cacheusuarios")
	@CacheEvict(value = "cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuariosPorNome(@PathVariable("nome") String nome) {
		PageRequest page = null;
		Page<Usuario> list = null;
		if (nome == null || (nome != null && nome.trim().isEmpty() || nome.equalsIgnoreCase("undefined"))) {
			page = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(page);
			// System.out.println(nome + ">todos a procurar...");
		} else {
			// System.out.println(nome + " a procurar...");
			page = PageRequest.of(0, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, page);
			// System.out.println(list.toString());

		}

		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@GetMapping(value = "/usuarioPorNome/{nome}/page/{page}", produces = "application/json")
	@CachePut("cacheusuarios")
	public ResponseEntity<Page<Usuario>> usuariosPorNomePage(@PathVariable("nome") String nome,
			@PathVariable("page") int page) {
		PageRequest pageReq = null;
		Page<Usuario> list = null;
		if (page > 0) {
			page = page - 1;
		}
		if (nome == null || (nome != null && nome.trim().isEmpty() || nome.equalsIgnoreCase("undefined"))) {
			pageReq = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findAll(pageReq);
			// System.out.println(nome + ">todos a procurar...");
		} else {
			// System.out.println(nome + " a procurar...");
			pageReq = PageRequest.of(page, 5, Sort.by("nome"));
			list = usuarioRepository.findUserByNamePage(nome, pageReq);
			// System.out.println(list.toString());

		}

		return new ResponseEntity<Page<Usuario>>(list, HttpStatus.OK);
	}

	@DeleteMapping(value = "/removerTelefone/{id}", produces = "application/text")
	public String deleteTelefone(@PathVariable("id") Long id) {
		telefoneRepsoitory.deleteById(id);
		return "Telefone removido com sucesso";
	}

	@GetMapping(value = "/relatorio", produces = "application/text")
	public ResponseEntity<String> downloadRelatorio(HttpServletRequest request) throws Exception {
		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario", new HashMap(), request.getServletContext());
		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		return new ResponseEntity<String>(base64Pdf, HttpStatus.OK);
	}

	@PostMapping(value = "/relatorio/", produces = "application/text")
	public ResponseEntity<String> downloadRelatorioParam(HttpServletRequest request, @RequestBody UserReport userReport)
			throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dateFormatParam = new SimpleDateFormat("yyyy-MM-dd");

		String dataInicio = dateFormatParam.format(dateFormat.parse(userReport.getDataInicio()));
		String dataFim = dateFormatParam.format(dateFormat.parse(userReport.getDataFim()));
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("DATA_INICIO", dataInicio);
		params.put("DATA_FIM", dataFim);
		byte[] pdf = serviceRelatorio.gerarRelatorio("relatorio-usuario-param", params, request.getServletContext());
		String base64Pdf = "data:application/pdf;base64," + Base64.encodeBase64String(pdf);
		return new ResponseEntity<String>(base64Pdf, HttpStatus.OK);
	}

	@GetMapping(value = "/grafico", produces = "application/json")
	public ResponseEntity<UserChart> grafico() {
		UserChart userChart = new UserChart();
	List<String> resultado =	jdbcTemplate.queryForList(
				"select array_agg(nome) from usuario where salario >0 and nome <>'' union all select cast(array_agg(salario) as character varying[]) from usuario where salario >0 and nome <>''",
				String.class);
	if(!resultado.isEmpty()) {
		String nomes = resultado.get(0).replaceAll("\\{", "").replaceAll("\\}", "");
		String salarios = resultado.get(1).replaceAll("\\{", "").replaceAll("\\}", "");
		userChart.setNome(nomes);
		userChart.setSalario(salarios);
	}
		return new ResponseEntity<UserChart>(userChart, HttpStatus.OK);
	}
}
