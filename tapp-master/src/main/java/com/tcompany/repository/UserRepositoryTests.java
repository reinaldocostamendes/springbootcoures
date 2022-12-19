package com.tcompany.repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tcompany.model.Users;

@Repository
@Transactional
public interface UserRepositoryTests extends CrudRepository<Users, Integer> {
	@Query("select u from Users u where trim(upper(u.firs_name)) like %?1%")
	List<Users> findUserByFirsName(String name);
}
