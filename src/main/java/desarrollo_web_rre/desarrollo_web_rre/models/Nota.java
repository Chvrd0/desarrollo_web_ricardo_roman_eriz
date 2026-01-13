package desarrollo_web_rre.desarrollo_web_rre.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Entidad JPA que representa una calificación (nota) para un aviso.
 * Permite a los usuarios asignar una puntuación (ej. de 1 a 7) a un aviso.
 * Esta clase se mapea a la tabla "nota" en la base de datos.
 */
@Entity
@Table // Mapea a la tabla "nota" por nombre de clase
public class Nota {

    /**
     * Identificador único de la nota (Clave Primaria).
     * Se genera automáticamente usando la estrategia de identidad de la base de datos.
     */
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Integer id;

    /**
     * El ID del 'Aviso' al que está asociada esta nota.
     * Actúa como clave foránea (FK) para la entidad Aviso.
     * No puede ser nulo.
     */
    @NotNull
    private Integer avisoId; // Referencia a Aviso

    /**
     * El valor numérico de la calificación.
     * No puede ser nulo.
     * La validación asegura que el valor está entre 1 y 7.
     */
    @NotNull
    @Min(1) // Validación para asegurar que la nota sea al menos 1
    @Max(7) // Validación para asegurar que la nota sea máximo 7
    private Integer nota;


    /**
     * Constructor por defecto.
     * Requerido por el framework JPA para la creación (hidratación) de instancias.
     */
    public Nota() {
    }

    /**
     * Constructor para crear una nueva instancia de Nota.
     *
     * @param valor El valor de la calificación (debe estar entre 1 y 7).
     * @param avisoId El ID del aviso al que se asocia esta nota.
     */
    public Nota(Integer valor, Integer avisoId) {
        this.nota = valor;
        this.avisoId = avisoId;
    }

    // --- Getters ---
    
    /**
     * Obtiene el ID de la nota.
     *
     * @return El ID único de la nota.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Obtiene el valor numérico de la calificación.
     *
     * @return El valor de la nota (ej. 1-7).
     */
    public Integer getValor() {
        return nota;
    }

    /**
     * Obtiene el ID del aviso al que pertenece esta nota.
     *
     * @return El ID del aviso asociado.
     */
    public Integer getAvisoId() {
        return avisoId;
    }
}