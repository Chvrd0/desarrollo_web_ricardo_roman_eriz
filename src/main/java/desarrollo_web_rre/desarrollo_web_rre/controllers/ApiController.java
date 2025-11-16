package desarrollo_web_rre.desarrollo_web_rre.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        @PathVariable("id") Long avisoId
    ) {
        // Equivalente a: db.get_aviso_id(id, Comentario) + [i.to_dict() for i in comentarios]
        return apiService.getComentarios(avisoId);
    }

    // ======================================
    // ESTADÍSTICAS: GET "/get-line-data"
    // ======================================
    @GetMapping("/get-line-data")
    public List<Map<String, String>> getLineDataEndpoint() {
        // Equivalente a get_line_stats() en Flask
        return apiService.getLineStats();
    }

    // ======================================
    // ESTADÍSTICAS: GET "/get-pie-data"
    // ======================================
    @GetMapping("/get-pie-data")
    public List<Map<String, Object>> getPieDataEndpoint() {
        // Equivalente a get_pie_stats() en Flask
        return apiService.getPieStats();
    }

    // ======================================
    // ESTADÍSTICAS: GET "/get-bar-data"
    // ======================================
    @GetMapping("/get-bar-data")
    public Map<String, Object> getBarDataEndpoint() {
        // Equivalente a get_bar_stats() en Flask
        return apiService.getBarStats();
    }
}
