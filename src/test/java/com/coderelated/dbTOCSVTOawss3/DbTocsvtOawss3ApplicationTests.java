package com.coderelated.dbTOCSVTOawss3;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import com.coderelated.dbTOCSVTOawss3.model.Users;
import com.coderelated.dbTOCSVTOawss3.repository.UsersRepository;

@SpringBootTest
@DataJpaTest
class DbTocsvtOawss3ApplicationTests {
	
	@Autowired
	private UsersRepository usersRepository;

	@Test
	void contextLoads() {
	}
	
	@Test
	public void calltest() {
		Users users = new Users();
		users.setName("prem");
		users.setDob(new Date(System.currentTimeMillis()));
		users.setCreatedDate(new Date(System.currentTimeMillis()));
		usersRepository.save(users);
		 List<Users> userslist = (List<Users>) usersRepository.findAll();
	        System.out.println(userslist.size());
	        assertThat(userslist.size()).isEqualTo(1);
	}

}
