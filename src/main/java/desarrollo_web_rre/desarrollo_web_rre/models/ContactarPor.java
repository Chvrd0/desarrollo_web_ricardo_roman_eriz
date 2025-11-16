package desarrollo_web_rre.desarrollo_web_rre.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;


@Entity
@Table
public class ContactarPor {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY 
    )
    private Integer id;

    @NotNull
    private String nombre;       // Ej: "WhatsApp", "Correo"

    @NotNull
    private String identificador; // número, mail, @usuario, etc.

    @NotNull
    private Integer avisoId;        // referencia a Aviso

    public ContactarPor() {
    }

    public ContactarPor(String nombre, String identificador, Integer avisoId) {
        this.nombre = nombre;
        this.identificador = identificador;
        this.avisoId = avisoId;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdentificador() {
        return identificador;
    }

    public Integer getAvisoId() {
        return avisoId;
    }
}
