package desarrollo_web_rre.desarrollo_web_rre.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import desarrollo_web_rre.desarrollo_web_rre.models.Aviso;
import desarrollo_web_rre.desarrollo_web_rre.models.AvisoRepository;
import desarrollo_web_rre.desarrollo_web_rre.models.ComentarioRepository;
import desarrollo_web_rre.desarrollo_web_rre.models.Comuna;
import desarrollo_web_rre.desarrollo_web_rre.models.ComunaRepository;
import desarrollo_web_rre.desarrollo_web_rre.models.ContactarPor;
import desarrollo_web_rre.desarrollo_web_rre.models.ContactarPorRepository;
import desarrollo_web_rre.desarrollo_web_rre.models.Foto;
import desarrollo_web_rre.desarrollo_web_rre.models.FotoRepository;
import desarrollo_web_rre.desarrollo_web_rre.models.Nota;
import desarrollo_web_rre.desarrollo_web_rre.models.NotaRepository;
import desarrollo_web_rre.desarrollo_web_rre.models.Region;
import desarrollo_web_rre.desarrollo_web_rre.models.RegionRepository;

/**
 * Servicio principal de la aplicación.
 * Esta clase contiene la lógica de negocio central, manejando la interacción
 * entre los controladores y los repositorios de datos. Se encarga de la
 * manipulación de datos, el procesamiento de archivos y la preparación de
 * la información para las vistas.
 */
@Service
public class AppService {

    /**
     * Ruta absoluta al directorio 'static' de la aplicación.
     * Se utiliza como base para guardar y servir archivos subidos (ej. 'uploads').
     */
    private final String pathStatic;

    // --- Repositorios ---
    private final AvisoRepository avisoRepository;
    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    private final FotoRepository fotoRepository;
    private final ContactarPorRepository contactarPorRepository;
    private final ComentarioRepository comentarioRepository;
    private final NotaRepository notaRepository;

    /**
     * Constructor del servicio.
     * Spring inyecta automáticamente las dependencias de todos los repositorios.
     * También resuelve y almacena la ruta absoluta al directorio 'static'
     * al momento de la inicialización.
     *
     * @param avisoRepository        Repositorio para la entidad Aviso.
     * @param regionRepository       Repositorio para la entidad Region.
     * @param comunaRepository       Repositorio para la entidad Comuna.
     * @param fotoRepository         Repositorio para la entidad Foto.
     * @param contactarPorRepository Repositorio para la entidad ContactarPor.
     * @param comentarioRepository   Repositorio para la entidad Comentario.
     * @param notaRepository         Repositorio para la entidad Nota.
     * @throws IOException Si falla la resolución de la ruta 'classpath:static'.
     */
    public AppService(
        AvisoRepository avisoRepository,
        RegionRepository regionRepository,
        ComunaRepository comunaRepository,
        FotoRepository fotoRepository,
        ContactarPorRepository contactarPorRepository,
        ComentarioRepository comentarioRepository,
        NotaRepository notaRepository
    ) throws IOException {

        this.avisoRepository = avisoRepository;
        this.regionRepository = regionRepository;
        this.comunaRepository = comunaRepository;
        this.fotoRepository = fotoRepository;
        this.contactarPorRepository = contactarPorRepository;
        this.comentarioRepository = comentarioRepository;
        this.notaRepository = notaRepository;

        // Resolver ruta absoluta a /static
        Path staticDir = Paths.get(ResourceUtils.getFile("classpath:static").getAbsolutePath());
        this.pathStatic = staticDir.toString();
        System.out.println("Static path resolved to: " + this.pathStatic);
    }

    // ================================
    // PORTADA: últimos 5 avisos
    // ================================

    /**
     * Obtiene los datos necesarios para la página de portada.
     * Busca los avisos más recientes y formatea la información necesaria,
     * incluyendo la comuna y la foto principal.
     *
     * @param pageSize El número de avisos a obtener (aunque la implementación actual lo fija en 5).
     * @return Una lista de Mapas, donde cada mapa representa un aviso con datos formateados como String.
     */
    public List<Map<String, String>> getPortadaData(Integer pageSize) {
        // Nota: La consulta está fijada a 5, ignorando el parámetro pageSize.
        Page<Aviso> page = avisoRepository.findAllByOrderByFechaIngresoDesc(
            PageRequest.of(0, 5)
        );
        List<Aviso> avisos = page.getContent();

        List<Map<String, String>> data = new ArrayList<>();

        for (Aviso aviso : avisos) {
            Map<String, String> avisoData = new HashMap<>();

            // Comuna
            Comuna comuna = null;
            if (aviso.getComunaId() != null) {
                comuna = comunaRepository.findById(aviso.getComunaId()).orElse(null);
            }

            // Foto principal (la primera subida)
            Foto img = fotoRepository.findFirstByAvisoIdOrderByIdAsc(aviso.getId());
            String pathImage = null;
            if (img != null) {
                // archivo guardado en /static/uploads/<filename>
                pathImage = img.getRutaArchivo() + img.getNombreArchivo();
            }

            // Formatear unidad de medida
            String um = "mes(es)";
            if ("a".equalsIgnoreCase(aviso.getUnidadMedida())) {
                um = "año(s)";
            }

            // Manejo de nulos
            String sec = (aviso.getSector() == null) ? "" : aviso.getSector();
            String des = (aviso.getDescripcion() == null) ? "" : aviso.getDescripcion();

            // Poblar el mapa
            avisoData.put("fecha_ingreso", aviso.getFechaIngreso() == null ? "" : aviso.getFechaIngreso().toString());
            avisoData.put("comuna", (comuna == null) ? "" : comuna.getNombre());
            avisoData.put("sector", sec);
            avisoData.put("tipo", aviso.getTipo());
            avisoData.put("cantidad", aviso.getCantidad());
            avisoData.put("edad", aviso.getEdad());
            avisoData.put("unidad_medida", um);
            avisoData.put("descripcion", des);
            avisoData.put("path_image", pathImage);

            data.add(avisoData);
        }

        return data;
    }

    // ================================
    // POST AVISO (equivalente a enviar_formulario en Flask)
    // ================================

    /**
     * Procesa y guarda un nuevo aviso enviado desde el formulario.
     * Esto incluye:
     * 1. Procesar y guardar archivos de imagen en el disco con nombres únicos (hash SHA-256).
     * 2. Guardar la entidad Aviso principal.
     * 3. Guardar las entidades Foto asociadas al aviso.
     * 4. Guardar las entidades ContactarPor asociadas al aviso.
     *
     * @param foto1 Archivo de imagen 1 (obligatorio).
     * @param foto2 Archivo de imagen 2 (opcional).
     * @param foto3 Archivo de imagen 3 (opcional).
     * @param foto4 Archivo de imagen 4 (opcional).
     * @param foto5 Archivo de imagen 5 (opcional).
     * @param comunaNombre Nombre de la comuna seleccionada.
     * @param sector Sector (opcional).
     * @param nombre Nombre del publicador.
     * @param email Email del publicador.
     * @param celular Celular del publicador (opcional).
     * @param tipo Tipo de aviso (ej. "perdido").
     * @param cantidad Cantidad de animales.
     * @param edad Edad del animal.
     * @param unidadMedida Unidad de medida de la edad ("Mes(es)" o "Año(s)").
     * @param fechaEntrega Fecha del evento (avistamiento, etc.).
     * @param descripcion Descripción adicional (opcional).
     * @param contactos Lista de vías de contacto seleccionadas (ej. ["email", "celular"]).
     * @param allRequestParams Mapa de todos los parámetros para extraer valores de contacto (ej. "contacto-email").
     * @throws Exception Si ocurre un error durante el hash, la E/S de archivos o el guardado en BBDD.
     */
    public void handlePostAviso(
        MultipartFile foto1,
        MultipartFile foto2,
        MultipartFile foto3,
        MultipartFile foto4,
        MultipartFile foto5,
        String comunaNombre,
        String sector,
        String nombre,
        String email,
        String celular,
        String tipo,
        String cantidad,
        String edad,
        String unidadMedida,
        String fechaEntrega,
        String descripcion,
        List<String> contactos,
        Map<String, String> allRequestParams
    ) throws Exception {

        // --- 1. Construir lista de fotos ---
        List<MultipartFile> fotos = new ArrayList<>();
        if (foto1 != null && !foto1.isEmpty()) fotos.add(foto1);
        if (foto2 != null && !foto2.isEmpty()) fotos.add(foto2);
        if (foto3 != null && !foto3.isEmpty()) fotos.add(foto3);
        if (foto4 != null && !foto4.isEmpty()) fotos.add(foto4);
        if (foto5 != null && !foto5.isEmpty()) fotos.add(foto5);

        // --- 2. Extraer valores de contacto ---
        List<String> contactosValues = new ArrayList<>();
        if (contactos != null) {
            for (String c : contactos) {
                String key = "contacto-" + c;
                String value = allRequestParams.get(key);
                contactosValues.add(value);
            }
        }


        // --- 3. Procesar y guardar archivos ---
        List<String> savedFilenames = new ArrayList<>();
        Path uploadsDir = Paths.get(this.pathStatic, "uploads");
        if (!Files.exists(uploadsDir)) {
            Files.createDirectories(uploadsDir);
        }

        for (MultipartFile file : fotos) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new IllegalArgumentException("File name is empty.");
            }

            // Generar nombre único (hash SHA-256)
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(originalFilename.getBytes("UTF-8"));
            byte[] hash = md.digest();
            String filename;
            try (Formatter formatter = new Formatter()) {
                for (byte b : hash) {
                    formatter.format("%02x", b);
                }
                filename = formatter.toString();
            }

            // Validar extensión
            String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if (!extension.matches("jpg|jpeg|png|gif")) {
                throw new IllegalArgumentException("Invalid file extension: " + extension);
            }

            String finalName = filename + "." + extension;
            Path targetPath = uploadsDir.resolve(finalName);

            // Guardar el archivo en el disco
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            savedFilenames.add(finalName);
        }

        // --- 4. Buscar Comuna ID ---
        Comuna comuna = comunaRepository.findByNombre(comunaNombre);
        Integer comunaId = (comuna == null) ? null : comuna.getId();

        // --- 5. Normalizar datos ---
        String tipoNorm = (tipo == null) ? null : tipo.toLowerCase();
        String unidadCode = "m"; // 'm' por defecto (meses)
        if ("Año(s)".equalsIgnoreCase(unidadMedida)) {
            unidadCode = "a"; // 'a' (años)
        }

        // --- 6. Crear y guardar Aviso ---
        LocalDateTime fechaIngreso = LocalDateTime.now();
        Aviso aviso = new Aviso(
            comunaId, sector, nombre, email, celular,
            tipoNorm, cantidad, edad, unidadCode,
            fechaEntrega, descripcion, fechaIngreso
        );
        aviso = avisoRepository.save(aviso); // Guardar y obtener la entidad con el ID generado

        // --- 7. Crear y guardar Fotos asociadas ---
        for (String filename : savedFilenames) {
            Foto foto = new Foto(
                "uploads/", // Ruta relativa al directorio 'static'
                filename,
                aviso.getId()
            );
            fotoRepository.save(foto);
        }

        // --- 8. Crear y guardar Contactos asociados ---
        if (contactos != null) {
            for (int i = 0; i < contactos.size(); i++) {
                String via = contactos.get(i);
                String value = contactosValues.get(i);
                ContactarPor c = new ContactarPor(
                    via,
                    value,
                    aviso.getId()
                );
                contactarPorRepository.save(c);
            }
        }
    }

    // ================================
    // LISTADO paginado
    // ================================

    /**
     * Obtiene los datos para la página de "Listado", de forma paginada.
     * Recupera una página de avisos y, para cada uno, calcula la nota promedio
     * y obtiene la foto principal y la comuna.
     *
     * @param page El número de página solicitado (1-indexado).
     * @return Un Mapa que contiene la lista de datos ("data"), el número de página ("page")
     * y el total de páginas ("total_pages").
     */
    public Map<String, Object> getListadoData(Integer page) {
        int pageSize = 5;
        // Convertir página 1-indexada (del usuario) a 0-indexada (de Spring)
        int pageIndex = (page == null || page < 1) ? 0 : (page - 1);

        Page<Aviso> pageResult = avisoRepository.findAllByOrderByFechaIngresoDesc(
            PageRequest.of(pageIndex, pageSize)
        );

        List<Aviso> avisos = pageResult.getContent();
        int totalPages = pageResult.getTotalPages();

        List<Map<String, String>> data = new ArrayList<>();

        for (Aviso aviso : avisos) {
            Map<String, String> avisoData = new HashMap<>();

            // Comuna
            Comuna comuna = null;
            if (aviso.getComunaId() != null) {
                comuna = comunaRepository.findById(aviso.getComunaId()).orElse(null);
            }

            // Foto principal
            Foto img = fotoRepository.findFirstByAvisoIdOrderByIdAsc(aviso.getId());
            String pathImage = null;
            if (img != null) {
                pathImage = img.getRutaArchivo() + img.getNombreArchivo();
            }

            
            // Calcular nota promedio
            List<Nota> notas = notaRepository.findAllByAvisoId(aviso.getId());
            double notaPromedio = 0.0;
            if (notas != null && !notas.isEmpty()) {
                notaPromedio = notas.stream()
                                .mapToInt(Nota::getValor)
                                .average()
                                .orElse(0.0);
            }
            
            avisoData.put("nota_promedio", String.format("%.1f", notaPromedio));

            // Formatear unidad de medida
            String um = "mes(es)";
            if ("a".equalsIgnoreCase(aviso.getUnidadMedida())) {
                um = "año(s)";
            }

            // Manejo de nulos
            String sec = (aviso.getSector() == null) ? "" : aviso.getSector();
            String des = (aviso.getDescripcion() == null) ? "" : aviso.getDescripcion();

            // Poblar el mapa
            avisoData.put("id", aviso.getId() == null ? "" : aviso.getId().toString());
            avisoData.put("fecha_ingreso", aviso.getFechaIngreso() == null ? "" : aviso.getFechaIngreso().toString());
            avisoData.put("fecha_entrega", aviso.getFechaEntrega());
            avisoData.put("comuna", (comuna == null) ? "" : comuna.getNombre());
            avisoData.put("sector", sec);
            avisoData.put("tipo", aviso.getTipo());
            avisoData.put("cantidad", aviso.getCantidad());
            avisoData.put("edad", aviso.getEdad());
            avisoData.put("unidad_medida", um);
            avisoData.put("descripcion", des);
            avisoData.put("nombre", aviso.getNombre());
            avisoData.put("email", aviso.getEmail());
            avisoData.put("path_image", pathImage);

            data.add(avisoData);
        }

        // Ensamblar resultado final
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("page", page); // Devuelve la página 1-indexada original
        result.put("total_pages", totalPages);

        return result;
    }

    // ================================
    // DETALLES de un aviso
    // ================================

    /**
     * Obtiene todos los datos necesarios para mostrar la página de detalles de un aviso.
     * Esto incluye el aviso principal, todas sus fotos, sus métodos de contacto,
     * y la información de comuna y región.
     *
     * @param id El ID del aviso a consultar.
     * @return Un Mapa que contiene las entidades "aviso", "fotos", "contactos", "comuna" y "region".
     * Retorna un mapa vacío si el aviso no se encuentra.
     */
    public Map<String, Object> getDetallesAviso(Integer id) {
        Map<String, Object> result = new HashMap<>();

        // Buscar el aviso. Si no existe, retornar mapa vacío.
        Aviso aviso = avisoRepository.findById(id).orElse(null);
        if (aviso == null) {
            return result;
        }

        // Buscar entidades relacionadas
        List<Foto> fotos = fotoRepository.findAllByAvisoIdOrderByIdAsc(aviso.getId());
        List<ContactarPor> contactos = contactarPorRepository.findAllByAvisoId(aviso.getId());

        Comuna comuna = null;
        Region region = null;

        // Obtener Comuna y luego Región (si existen)
        if (aviso.getComunaId() != null) {
            comuna = comunaRepository.findById(aviso.getComunaId()).orElse(null);
            if (comuna != null && comuna.getRegionId() != null) {
                region = regionRepository.findById(comuna.getRegionId()).orElse(null);
            }
        }

        // Poblar el mapa de resultado
        result.put("aviso", aviso);
        result.put("fotos", fotos);
        result.put("contactos", contactos);
        result.put("comuna", comuna);
        result.put("region", region);

        return result;
    }
}