package desarrollo_web_rre.desarrollo_web_rre.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactarPorRepository extends JpaRepository<ContactarPor, Integer> {

    // Todas las formas de contacto asociadas a un aviso
    List<ContactarPor> findAllByAvisoId(Integer avisoId);
}
