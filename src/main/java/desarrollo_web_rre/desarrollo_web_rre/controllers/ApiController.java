package desarrollo_web_rre.desarrollo_web_rre.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import desarrollo_web_rre.desarrollo_web_rre.models.Comentario;
import desarrollo_web_rre.desarrollo_web_rre.models.Nota;
import desarrollo_web_rre.desarrollo_web_rre.models.NotaRepository;
import desarrollo_web_rre.desarrollo_web_rre.services.ApiService;

/**
 * Controlador REST que expone los endpoints de la API utilizada por el frontend.
 * 
 * Incluye:
 * <ul>
 *   <li>Obtención de comentarios de un aviso</li>
 *   <li>Obtención de datos estadísticos (línea, pie, barra)</li>
 *   <li>Creación de nuevos comentarios</li>
 *   <li>Registro de nuevas notas (calificaciones) asociadas a un aviso</li>
 * </ul>
 */
@RestController
public class ApiController {

    private final ApiService apiService;
    private final NotaRepository notaRepository;

    /**
     * Constructor que inyecta las dependencias necesarias.
     *
     * @param apiService     Servicio con la lógica de negocio para estadísticas y comentarios.
     * @param notaRepository Repositorio JPA para gestionar las entidades {@link Nota}.
     */
    public ApiController(ApiService apiService, NotaRepository notaRepository) {
        this.apiService = apiService;
        this.notaRepository = notaRepository;
    }

    // ======================================
    // COMENTARIOS: GET "/aviso/{id}/comentarios"
    // ======================================

    /**
     * Obtiene la lista de comentarios asociados a un aviso específico.
     *
     * @param avisoId ID del aviso del cual se desean obtener los comentarios.
     * @return Lista de mapas con la información de cada comentario
     *         (por ejemplo: nombre, texto, fecha formateada, etc.).
     */
    @GetMapping("/aviso/{id}/comentarios")
    public List<Map<String, String>> getComentariosEndpoint(
        @PathVariable("id") Integer avisoId
    ) {
        // Delegamos la obtención de comentarios en el servicio.
        return apiService.getComentarios(avisoId);
    }

    // ======================================
    // ESTADÍSTICAS: GET "/get-line-data"
    // ======================================

    /**
     * Entrega los datos necesarios para graficar una serie de tiempo (línea) en el frontend.
     *
     * @return Lista de mapas donde cada mapa representa un punto de la serie
     *         (por ejemplo: fecha, cantidad, etc.).
     */
    @GetMapping("/get-line-data")
    public List<Map<String, String>> getLineDataEndpoint() {
        return apiService.getLineStats();
    }

    // ======================================
    // ESTADÍSTICAS: GET "/get-pie-data"
    // ======================================

    /**
     * Entrega los datos necesarios para graficar un gráfico tipo pie (torta).
     *
     * @return Lista de mapas con categorías y sus valores asociados.
     */
    @GetMapping("/get-pie-data")
    public List<Map<String, Object>> getPieDataEndpoint() {
        return apiService.getPieStats();
    }

    // ======================================
    // ESTADÍSTICAS: GET "/get-bar-data"
    // ======================================

    /**
     * Entrega los datos necesarios para graficar un gráfico de barras.
     *
     * @return Mapa con la estructura de datos requerida por el frontend
     *         para construir el gráfico de barras.
     */
    @GetMapping("/get-bar-data")
    public Map<String, Object> getBarDataEndpoint() {
        return apiService.getBarStats();
    }

    // ======================================
    // COMENTARIOS: POST "/aviso/{id}/nuevo_comentario"
    // ======================================

    /**
     * Crea un nuevo comentario asociado a un aviso.
     *
     * <p>Se espera que el cuerpo de la petición (JSON) contenga las claves
     * <code>c_nombre</code> y <code>c_texto</code>, correspondientes al nombre
     * del autor del comentario y al texto del comentario, respectivamente.</p>
     *
     * @param avisoId ID del aviso al que se asocia el comentario.
     * @param payload Mapa con los datos enviados desde el frontend.
     * @return El comentario recién creado. Idealmente, debe ser el objeto persistido.
     */
    @PostMapping("/aviso/{id}/nuevo_comentario")
    public Comentario postComentarioEndpoint(
        @PathVariable("id") Integer avisoId,
        @RequestBody Map<String, String> payload
    ) {
        // Extraemos los campos que viene desde el JSON del frontend.
        String nombre = payload.get("c_nombre");
        String texto = payload.get("c_texto");

        // Creamos el nuevo comentario, asociándolo al aviso y con fecha actual.
        Comentario nuevoComentario = new Comentario(
            nombre,
            texto,
            LocalDateTime.now(),
            avisoId
        );

        // TODO: Guardar el comentario en la base de datos (ejemplo):
        // return comentarioRepository.save(nuevoComentario);
        // Por ahora, solo se imprime por consola y se retorna el objeto en memoria.
        System.out.println("Guardando nuevo comentario: " + nombre + ": " + texto);

        return nuevoComentario;
    }

    // ======================================
    // NOTAS: POST "/aviso/{id}/nueva_nota"
    // ======================================

    /**
     * Registra una nueva nota (calificación) para un aviso y retorna el nuevo promedio.
     *
     * <p>Se espera que el cuerpo de la petición (JSON) contenga la clave
     * <code>nota</code> con el valor numérico en formato string, por ejemplo:
     * <code>{"nota": "5"}</code>.</p>
     *
     * @param avisoId ID del aviso al que se asocia la nota.
     * @param payload Mapa con los datos enviados desde el frontend, al menos la clave "nota".
     * @return Mapa que contiene el nuevo promedio de notas bajo la clave "notaPromedio".
     */
    @PostMapping("/aviso/{id}/nueva_nota")
    public Map<String, Double> postNotaEndpoint(
        @PathVariable("id") Integer avisoId,
        @RequestBody Map<String, String> payload
    ) {
        // Extraemos y parseamos la nota enviada desde el frontend.
        // Asumimos que viene algo como {"nota": "5"}.
        Integer valorNota = Integer.parseInt(payload.get("nota"));

        // Validación defensiva del rango permitido para la nota.
        if (valorNota < 1 || valorNota > 7) {
            throw new IllegalArgumentException("La nota debe estar entre 1 y 7");
        }

        // Creamos y guardamos la nueva nota asociada al aviso.
        Nota nuevaNota = new Nota(valorNota, avisoId);
        notaRepository.save(nuevaNota);

        // Obtenemos todas las notas de ese aviso para calcular el nuevo promedio.
        List<Nota> notas = notaRepository.findAllByAvisoId(avisoId);

        double notaPromedio = 0.0;
        if (notas != null && !notas.isEmpty()) {
            notaPromedio = notas.stream()
                                .mapToInt(Nota::getValor)
                                .average()
                                .orElse(0.0);
        }

        // Armamos la respuesta que consumirá el frontend (JS),
        // donde la clave "notaPromedio" coincide con lo que espera el código JS.
        Map<String, Double> resultado = new HashMap<>();
        resultado.put("notaPromedio", notaPromedio);

        return resultado;
    }
}