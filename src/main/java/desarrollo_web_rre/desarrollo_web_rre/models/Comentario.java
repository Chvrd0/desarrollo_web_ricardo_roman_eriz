package desarrollo_web_rre.desarrollo_web_rre.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;


@Entity
@Table
public class Comentario {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Integer id;

    @NotNull
    private String nombre;

    @NotNull
    private String texto;

    @NotNull
    private LocalDateTime fecha;

    @NotNull
    private Integer avisoId; // referencia a Aviso

    public Comentario() {
    }

    public Comentario(String nombre, String texto, LocalDateTime fecha, Integer avisoId) {
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

    public Integer getAvisoId() {
        return avisoId;
    }
}
