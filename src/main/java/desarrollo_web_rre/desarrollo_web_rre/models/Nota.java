package desarrollo_web_rre.desarrollo_web_rre.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table
public class Nota {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Integer id;

    @NotNull
    private Integer avisoId; // Referencia a Aviso

    @NotNull
    @Min(1) // Validación para asegurar que la nota sea al menos 1
    @Max(7) // Validación para asegurar que la nota sea máximo 7
    private Integer nota;


    public Nota() {
    }

    public Nota(Integer valor, Integer avisoId) {
        this.nota = valor;
        this.avisoId = avisoId;
    }

    // --- Getters ---
    
    public Integer getId() {
        return id;
    }

    public Integer getValor() {
        return nota;
    }

    public Integer getAvisoId() {
        return avisoId;
    }
}