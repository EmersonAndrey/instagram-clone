package br.edu.ifpb.instagram.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.edu.ifpb.instagram.model.entity.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository userRepository;

	@PersistenceContext
    private EntityManager entityManager;
	
	UserEntity user1 = new UserEntity();
	
	void setUser(){
       	user1.setFullName("andrey feia");
        user1.setEmail("ana@email.com.br");
        user1.setUsername("anaFeia");
        user1.setEncryptedPassword("senha1235");

        user1 = userRepository.save(user1); 
		entityManager.flush(); 

		
	}

	@Test
	void buscarUsuarioPorNomeTest() {
		setUser();
		
		Optional<UserEntity> usuarioEncontrado = userRepository.findByUsername("anaFeia");
		
        if(usuarioEncontrado.isPresent()){
            assertEquals("anaFeia", usuarioEncontrado.get().getUsername());
		    assertEquals("ana@email.com.br",usuarioEncontrado.get().getEmail());
        }	
			
		
	}

    @Test
    void buscarUsuarioComNomeNullTest(){
        Optional<UserEntity> user = userRepository.findByUsername(null);
        assertTrue(user.isEmpty());
    }


	@Test
	void retornarListaDeUsuariosExistenteTest(){
		setUser();

		List<UserEntity> listaUsuarios = userRepository.findAll();

		assertNotNull(listaUsuarios);
		assertThat(listaUsuarios).extracting(UserEntity::getUsername)
    	.containsExactlyInAnyOrder("anaFeia");
		
	}
	@Test
	void retornaListaVaziaTest(){
		List<UserEntity> listaUsuarios = userRepository.findAll();
		assertThat(listaUsuarios).isNotNull();
		assertThat(listaUsuarios).isEmpty();
	}


	@Test
    public void updatePartialUserTest() {
        setUser();
		System.out.println(user1.getFullName());
		System.out.println(user1.getEmail());
		System.out.println(user1.getUsername());
		
        Optional<UserEntity> existeUser = userRepository.findById(user1.getId());
        assertTrue(existeUser.isPresent(), "Usuário não foi salvo corretamente!");

        int updatedRows = userRepository.updatePartialUser(
                "ismael", 
                "ismael@gmail.com", 
                "andreyFeio", 
                null, 
                user1.getId()
        );

        entityManager.flush();
        entityManager.clear(); 

        Optional<UserEntity> updatedUserO = userRepository.findById(user1.getId());
        assertTrue(updatedUserO.isPresent(), "Usuário atualizado não encontrado!");


        UserEntity updatedUser = updatedUserO.get();

		System.out.println(updatedUser.getFullName());
		System.out.println(updatedUser.getEmail());
		System.out.println(updatedUser.getUsername());

      
        assertEquals("ismael", updatedUser.getFullName());
        assertEquals("ismael@gmail.com", updatedUser.getEmail());
        assertEquals("andreyFeio", updatedUser.getUsername()); 
        assertEquals("senha1235", updatedUser.getEncryptedPassword());

        assertEquals(1, updatedRows);
    }

}
