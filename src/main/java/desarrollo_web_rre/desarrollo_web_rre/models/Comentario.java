package desarrollo_web_rre.desarrollo_web_rre.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table
public class Comentario {

    @Id
    @SequenceGenerator(
        name = "comentario_sequence",
        sequenceName = "comentario_sequence",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "comentario_sequence"
    )
    private Integer id;

    @NotNull
    private String nombre;

    @NotNull
    private String texto;

    @NotNull
    private LocalDateTime fecha;

    @NotNull
    private Long avisoId; // referencia a Aviso

    public Comentario() {
    }

    public Comentario(String nombre, String texto, LocalDateTime fecha, Long avisoId) {
        this.nombre = nombre;
        this.texto = texto;
        this.fecha = fecha;
        this.avisoId = avisoId;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTexto() {
        return texto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Long getAvisoId() {
        return avisoId;
    }
}
