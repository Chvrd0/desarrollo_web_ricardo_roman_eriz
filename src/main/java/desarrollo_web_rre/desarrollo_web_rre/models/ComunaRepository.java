package desarrollo_web_rre.desarrollo_web_rre.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad {@link Comuna}.
 *
 * <p>Permite realizar operaciones CRUD sobre la tabla de comunas y define
 * consultas personalizadas utilizadas para poblar selectores dinámicos de
 * región–comuna u obtener comunas específicas por nombre.</p>
 */
@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Integer> {

    /**
     * Obtiene todas las comunas que pertenecen a una región específica.
     *
     * <p>Este método se utiliza normalmente para cargar el listado de comunas
     * según la región seleccionada por el usuario en los formularios.</p>
     *
     * @param regionId ID de la región.
     * @return Lista de comunas asociadas a la región.
     */
    List<Comuna> findAllByRegionId(Integer regionId);

    /**
     * Busca una comuna según su nombre exacto.
     *
     * <p>Útil para validaciones o búsquedas directas cuando se conoce el nombre
     * textual de la comuna.</p>
     *
     * @param nombre Nombre exacto de la comuna.
     * @return La comuna correspondiente, o null si no existe.
     */
    Comuna findByNombre(String nombre);
}