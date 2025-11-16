package desarrollo_web_rre.desarrollo_web_rre.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import desarrollo_web_rre.desarrollo_web_rre.services.AppService;

@Controller
public class AppController {

    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    // ================================
    // PORTADA: GET "/"
    // ================================
    @GetMapping("/")
    public String indexRoute(Model model) {

        // Equivalente a: db.get_avisos(5, 0) + armado de data en Flask
        List<Map<String, String>> modelData = appService.getPortadaData(5);

        model.addAttribute("data", modelData);

        // Equivalente a render_template("portada/_portada.html", data=data)
        // Ajusta el nombre del template según como lo tengas en Thymeleaf
        return "portada/_portada";
    }

    // ================================
    // FORMULARIO: GET "/nuevo-aviso"
    // ================================
    @GetMapping("/nuevo-aviso")
    public String nuevoAvisoRoute() {
        // Equivalente a render_template("formulario/_formulario.html")
        return "formulario/_formulario";
    }

    // ================================
    // FORMULARIO: POST "/post-aviso"
    // ================================
    @PostMapping("/post-aviso")
    public String postAvisoRoute(
        // Fotos
        @RequestParam("foto1") MultipartFile foto1,
        @RequestParam(value = "foto2", required = false) MultipartFile foto2,
        @RequestParam(value = "foto3", required = false) MultipartFile foto3,
        @RequestParam(value = "foto4", required = false) MultipartFile foto4,
        @RequestParam(value = "foto5", required = false) MultipartFile foto5,

        // Datos del formulario
        @RequestParam("select-comuna") String comuna,
        @RequestParam(value = "sector", required = false) String sector,
        @RequestParam("nombre") String nombre,
        @RequestParam("email") String email,
        @RequestParam(value = "phone", required = false) String celular,
        @RequestParam("select-tipo") String tipo,
        @RequestParam("cantidad") String cantidad,
        @RequestParam("edad") String edad,
        @RequestParam("select-edad") String unidadMedida,
        @RequestParam("entrega") String fechaEntrega,
        @RequestParam(value = "descripcion", required = false) String descripcion,

        // Contactos (checkbox + inputs) – se procesan en el servicio
        @RequestParam(value = "select-contacto", required = false) List<String> contactos,
        @RequestParam Map<String, String> allRequestParams  // para leer "contacto-xxx"
    ) throws Exception {

        appService.handlePostAviso(
            foto1, foto2, foto3, foto4, foto5,
            comuna, sector, nombre, email, celular,
            tipo, cantidad, edad, unidadMedida,
            fechaEntrega, descripcion,
            contactos, allRequestParams
        );

        // En Flask devolvías un template de mensaje final.
        // Como base, dejamos un redirect a la portada (estilo confesiones).
        return "redirect:/";
    }

    // ================================
    // LISTADO: GET "/listado?page=N"
    // ================================
    @GetMapping("/listado")
    public String listadoRoute(
        @RequestParam(value = "page", required = false) Integer page,
        Model model
    ) {

        if (page == null || page < 1) {
            page = 1;
        }

        // El servicio se encarga de hacer count, offset, etc.
        Map<String, Object> listadoData = appService.getListadoData(page);

        model.addAttribute("data", listadoData.get("data"));
        model.addAttribute("page", listadoData.get("page"));
        model.addAttribute("total_pages", listadoData.get("total_pages"));

        return "listado/_listado";
    }

    // ================================
    // DETALLES: GET "/aviso/{id}"
    // ================================
    @GetMapping("/aviso/{id}")
    public String detallesRoute(
        @PathVariable("id") Long id,
        Model model
    ) {

        Map<String, Object> detalles = appService.getDetallesAviso(id);

        model.addAttribute("aviso", detalles.get("aviso"));
        model.addAttribute("fotos", detalles.get("fotos"));
        model.addAttribute("contactos", detalles.get("contactos"));
        model.addAttribute("comuna", detalles.get("comuna"));
        model.addAttribute("region", detalles.get("region"));

        return "listado/detalles_listado";
    }

    // ================================
    // ESTADÍSTICAS: GET "/estadisticas"
    // ================================
    @GetMapping("/estadisticas")
    public String estadisticasRoute() {
        // Igual que en Flask: render_template("estadisticas/_estadisticas.html")
        return "estadisticas/_estadisticas";
    }
}
