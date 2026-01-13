package desarrollo_web_rre.desarrollo_web_rre.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Entidad JPA que representa un comentario asociado a un aviso.
 * Esta clase se mapea a la tabla "comentario" en la base de datos.
 */
@Entity
@Table // Mapea a la tabla "comentario" por defecto
public class Comentario {

    /**
     * Identificador único del comentario (Clave Primaria).
     * Se genera automáticamente usando la estrategia de identidad de la base de datos.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Integer id;

    /**
     * Nombre de la persona que publica el comentario.
     * No puede ser nulo.
     */
    @NotNull
    private String nombre;

    /**
     * El contenido textual del comentario.
     * No puede ser nulo.
     */
    @NotNull
    private String texto;

    /**
     * La fecha y hora en que se creó el comentario.
     * No puede ser nulo.
     */
    @NotNull
    private LocalDateTime fecha;

    /**
     * El ID del 'Aviso' al que está asociado este comentario.
     * Actúa como clave foránea (FK) para la entidad Aviso.
     * No puede ser nulo.
     */
    @NotNull
    private Integer avisoId; // referencia a Aviso

    /**
     * Constructor por defecto.
     * Requerido por el framework JPA.
     */
    public Comentario() {
    }

    /**
     * Constructor para crear una nueva instancia de Comentario.
     *
     * @param nombre El nombre del autor del comentario.
     * @param texto El contenido del comentario.
     * @param fecha La fecha y hora de creación.
     * @param avisoId El ID del aviso al que se asocia.
     */
    public Comentario(String nombre, String texto, LocalDateTime fecha, Integer avisoId) {
        this.nombre = nombre;
        this.texto = texto;
        this.fecha = fecha;
        this.avisoId = avisoId;
    }

    /**
     * Obtiene el ID del comentario.
     *
     * @return El ID único del comentario.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Obtiene el nombre del autor del comentario.
     *
     * @return El nombre del autor.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el texto del comentario.
     *
     * @return El contenido del comentario.
     */
    public String getTexto() {
        return texto;
    }

    /**
     * Obtiene la fecha y hora de creación del comentario.
     *
     * @return La fecha y hora.
     */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /**
     * Obtiene el ID del aviso al que pertenece el comentario.
     *
     * @return El ID del aviso asociado.
     */
    public Integer getAvisoId() {
        return avisoId;
    }
}