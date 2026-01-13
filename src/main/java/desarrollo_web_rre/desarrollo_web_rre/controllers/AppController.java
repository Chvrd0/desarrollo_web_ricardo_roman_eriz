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

/**
 * Controlador principal de la aplicación web.
 * Maneja las solicitudes HTTP para las rutas principales, como la portada,
 * el formulario de avisos, el listado y los detalles.
 */
@Controller
public class AppController {

    private final AppService appService;

    /**
     * Constructor para la inyección de dependencias de AppService.
     *
     * @param appService El servicio que contiene la lógica de negocio.
     */
    public AppController(AppService appService) {
        this.appService = appService;
    }

    // ================================
    // PORTADA: GET "/"
    // ================================

    /**
     * Maneja la solicitud GET para la ruta raíz ("/").
     * Muestra la página principal (portada) de la aplicación.
     *
     * @param model Objeto Model para pasar datos a la vista.
     * @return El nombre de la plantilla de vista para la portada.
     */
    @GetMapping("/")
    public String indexRoute(Model model) {

        // Obtiene los datos de la portada (ej. los 5 últimos avisos)
        List<Map<String, String>> modelData = appService.getPortadaData(5);

        model.addAttribute("data", modelData);

        return "portada/_portada";
    }

    // ================================
    // FORMULARIO: GET "/nuevo-aviso"
    // ================================

    /**
     * Maneja la solicitud GET para "/nuevo-aviso".
     * Muestra el formulario para crear un nuevo aviso.
     *
     * @return El nombre de la plantilla de vista del formulario.
     */
    @GetMapping("/nuevo-aviso")
    public String nuevoAvisoRoute() {
        return "formulario/_formulario";
    }

    /**
     * Maneja la solicitud GET para "/formulario-completo".
     * Muestra la página de confirmación después de enviar un formulario.
     *
     * @return El nombre de la plantilla de vista del mensaje de éxito.
     */
    @GetMapping("/formulario-completo")
    public String formularioCompletoRoute() {
        return "formulario/msj_final_formulario";
    }

    // ================================
    // FORMULARIO: POST "/post-aviso"
    // ================================

    /**
     * Maneja la solicitud POST para "/post-aviso".
     * Procesa los datos del formulario de nuevo aviso, incluyendo la subida de archivos.
     *
     * @param foto1 Archivo de imagen (obligatorio).
     * @param foto2 Archivo de imagen (opcional).
     * @param foto3 Archivo de imagen (opcional).
     * @param foto4 Archivo de imagen (opcional).
     * @param foto5 Archivo de imagen (opcional).
     * @param comuna Comuna seleccionada en el formulario.
     * @param sector Sector (opcional).
     * @param nombre Nombre del contacto.
     * @param email Email del contacto.
     * @param celular Teléfono celular del contacto (opcional).
     * @param tipo Tipo de aviso (ej. "Perdido", "Encontrado").
     * @param cantidad Cantidad de animales.
     * @param edad Edad del animal.
     * @param unidadMedida Unidad de medida para la edad (ej. "días", "meses", "años").
     * @param fechaEntrega Fecha de entrega o avistamiento.
     * @param descripcion Descripción adicional (opcional).
     * @param contactos Lista de métodos de contacto preferidos (opcional).
     * @param allRequestParams Un mapa que captura todos los parámetros de la solicitud (útil para depuración o campos dinámicos).
     * @return Una redirección a la página de "formulario completo".
     * @throws Exception Si ocurre un error durante el procesamiento o guardado.
     */
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

        @RequestParam(value = "select-contacto", required = false) List<String> contactos,
        @RequestParam Map<String, String> allRequestParams
    ) throws Exception {

        // Delega la lógica de negocio (guardar en BBDD, procesar imágenes) al servicio
        appService.handlePostAviso(
            foto1, foto2, foto3, foto4, foto5,
            comuna, sector, nombre, email, celular,
            tipo, cantidad, edad, unidadMedida,
            fechaEntrega, descripcion,
            contactos, allRequestParams
        );

        // Redirige para evitar reenvío del formulario (Patrón Post-Redirect-Get)
        return "redirect:/formulario-completo";
    }

    // ================================
    // LISTADO: GET "/listado?page=N"
    // ================================

    /**
     * Maneja la solicitud GET para "/listado".
     * Muestra una lista paginada de todos los avisos.
     *
     * @param page El número de página a mostrar (opcional, por defecto es 1).
     * @param model Objeto Model para pasar datos a la vista.
     * @return El nombre de la plantilla de vista del listado.
     */
    @GetMapping("/listado")
    public String listadoRoute(
        @RequestParam(value = "page", required = false) Integer page,
        Model model
    ) {

        // Asegura que la página sea válida
        if (page == null || page < 1) {
            page = 1;
        }

        // Obtiene los datos paginados del servicio
        Map<String, Object> listadoData = appService.getListadoData(page);

        // Agrega los datos de paginación al modelo
        model.addAttribute("data", listadoData.get("data"));
        model.addAttribute("page", listadoData.get("page"));
        model.addAttribute("total_pages", listadoData.get("total_pages"));

        return "listado/_listado";
    }

    // ================================
    // DETALLES: GET "/aviso/{id}"
    // ================================

    /**
     * Maneja la solicitud GET para "/aviso/{id}".
     * Muestra la página de detalles de un aviso específico.
     *
     * @param id El ID del aviso a mostrar, extraído de la URL.
     * @param model Objeto Model para pasar datos a la vista.
     * @return El nombre de la plantilla de vista de detalles.
     */
    @GetMapping("/aviso/{id}")
    public String detallesRoute(
        @PathVariable("id") Integer id,
        Model model
    ) {

        // Obtiene los detalles completos del aviso desde el servicio
        Map<String, Object> detalles = appService.getDetallesAviso(id);

        // Agrega los datos del aviso al modelo
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

    /**
     * Maneja la solicitud GET para "/estadisticas".
     * Muestra la página de estadísticas de la aplicación.
     *
     * @return El nombre de la plantilla de vista de estadísticas.
     */
    @GetMapping("/estadisticas")
    public String estadisticasRoute() {
        return "estadisticas/_estadisticas";
    }
}