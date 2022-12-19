package curso.api.rest.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import curso.api.rest.model.Usuario;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	@Query("select u from Usuario u where u.login = ?1")
	Usuario findUserByLogin(String longin);

	@Query("select u from Usuario u where trim(upper(u.nome)) like %?1%")
	List<Usuario> findUserByNome(String nome);

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update usuario set token=?1 where login=?2")
	void atualizaTokenUser(String token, String login);

	@Query(value = "SELECT constraint_name from information_schema.constraint_column_usage where "
			+ "table_name='usuarios_role' and column_name='role_id'"
			+ "and constraint_name <> 'unique_role_user';", nativeQuery = true)
	String consultaConstraintRole();

	/*
	 * @Modifying
	 * 
	 * @Query(value = "alter table usuarios_role DROP CONSTRAINT ?1;", nativeQuery =
	 * true) void removerConstraintRole(String constraint);
	 */
	@Modifying
	@Query(nativeQuery = true, value = "insert into usuarios_role(usuario_id,role_id) values(?1,(select id from role where nome_role = 'ROLE_USER'));")
	void insereAcessoRolePadrao(Long idUser);
	
	@Modifying
	@Query(value="update usuario set senha=?1 where id=?2", nativeQuery = true)
	 void updateSenha(String senha, Long codUser);

	default Page<Usuario> findUserByNamePage(String nome, PageRequest page) {
		Usuario usuario = new Usuario();
		usuario.setNome(nome);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		ExampleMatcher ignoringExampleMatcher = ExampleMatcher.matching().withMatcher("nome",
				ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		Example<Usuario> example = Example.of(usuario, ignoringExampleMatcher);

		Page<Usuario> retorno = findAll(example, page);
		// System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>e" +
		// retorno.toList().toString());
		return retorno;
	}

}
