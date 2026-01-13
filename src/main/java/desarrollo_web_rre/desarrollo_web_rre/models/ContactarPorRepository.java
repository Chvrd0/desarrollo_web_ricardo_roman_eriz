package desarrollo_web_rre.desarrollo_web_rre.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link ContactarPor}.
 *
 * <p>Permite realizar operaciones CRUD sobre las formas de contacto asociadas a los avisos.
 * Cada entrada en esta entidad representa un tipo de contacto disponible
 * (por ejemplo: WhatsApp, Instagram, correo alternativo, etc.).</p>
 */
@Repository
public interface ContactarPorRepository extends JpaRepository<ContactarPor, Integer> {

    /**
     * Obtiene todas las formas de contacto asociadas a un aviso específico.
     *
     * <p>Este método se utiliza, por ejemplo, cuando se despliega el detalle del aviso
     * y se muestran los distintos medios mediante los cuales el usuario puede ser contactado.</p>
     *
     * @param avisoId ID del aviso.
     * @return Lista de objetos {@link ContactarPor} asociados al aviso.
     */
    List<ContactarPor> findAllByAvisoId(Integer avisoId);
}