package desarrollo_web_rre.desarrollo_web_rre.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import desarrollo_web_rre.desarrollo_web_rre.models.Comentario;
import desarrollo_web_rre.desarrollo_web_rre.services.ApiService;

@RestController
public class ApiController {

    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    // ======================================
    // COMENTARIOS: GET "/aviso/{id}/comentarios"
    // ======================================
    @GetMapping("/aviso/{id}/comentarios")
    public List<Map<String, String>> getComentariosEndpoint(
        @PathVariable("id") Integer avisoId
    ) {
        return apiService.getComentarios(avisoId);
    }

    // ======================================
    // ESTADÍSTICAS: GET "/get-line-data"
    // ======================================
    @GetMapping("/get-line-data")
    public List<Map<String, String>> getLineDataEndpoint() {
        return apiService.getLineStats();
    }

    // ======================================
    // ESTADÍSTICAS: GET "/get-pie-data"
    // ======================================
    @GetMapping("/get-pie-data")
    public List<Map<String, Object>> getPieDataEndpoint() {
        return apiService.getPieStats();
    }

    // ======================================
    // ESTADÍSTICAS: GET "/get-bar-data"
    // ======================================
    @GetMapping("/get-bar-data")
    public Map<String, Object> getBarDataEndpoint() {
        return apiService.getBarStats();
    }

    // ======================================
    // COMENTARIOS: POST "/aviso/{id}/nuevo_comentario"
    // ======================================
    @PostMapping("/aviso/{id}/nuevo_comentario")
    public Comentario postComentarioEndpoint(
        @PathVariable("id") Integer avisoId,
        @RequestBody Map<String, String> payload
    ) {
        // apiService necesita un nuevo método para manejar esto
        String nombre = payload.get("c_nombre");
        String texto = payload.get("c_texto");

        // Asumo que tienes un método en ApiService para crear el comentario
        // (Probablemente necesites inyectar ComentarioRepository aquí o en ApiService)
        Comentario nuevoComentario = new Comentario(
            nombre,
            texto,
            LocalDateTime.now(),
            avisoId
        );

        // Llama al servicio para guardarlo
        // Este es un ejemplo, deberás implementar la lógica de guardado.
        // return apiService.saveComentario(nuevoComentario); 

        // O si inyectas el repo aquí:
        // return comentarioRepository.save(nuevoComentario);

        // Por ahora, solo como ejemplo (implementa el guardado):
        System.out.println("Guardando nuevo comentario: " + nombre + ": " + texto);
        // DEBES IMPLEMENTAR EL GUARDADO Y DEVOLVER EL OBJETO GUARDADO
        return nuevoComentario; 
    }
}
