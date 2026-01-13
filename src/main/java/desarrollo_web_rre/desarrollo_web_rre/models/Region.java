package desarrollo_web_rre.desarrollo_web_rre.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Entidad JPA que representa una Región (división administrativa territorial).
 * Esta entidad es referenciada por las Comunas ({@link Comuna}).
 * Esta clase se mapea a la tabla "region" en la base de datos.
 */
@Entity
@Table // Mapea a la tabla "region" por nombre de clase
public class Region {

    /**
     * Identificador único de la región (Clave Primaria).
     * Se genera automáticamente usando la estrategia de identidad de la base de datos.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY 
    )
    private Integer id;

    /**
     * Nombre de la región (ej. "Región Metropolitana", "Región de Valparaíso").
     * No puede ser nulo.
     */
    @NotNull
    private String nombre;

    /**
     * Constructor por defecto.
     * Requerido por el framework JPA para la creación de instancias.
     */
    public Region() {
    }

    /**
     * Constructor para crear una nueva instancia de Region.
     *
     * @param nombre El nombre de la región.
     */
    public Region(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el ID de la región.
     *
     * @return El ID único de la región.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Obtiene el nombre de la región.
     *
     * @return El nombre de la región.
     */
    public String getNombre() {
        return nombre;
    }
}