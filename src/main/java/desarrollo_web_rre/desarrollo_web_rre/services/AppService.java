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

@Service
public class AppService {

    private final String pathStatic;

    private final AvisoRepository avisoRepository;
    private final RegionRepository regionRepository;
    private final ComunaRepository comunaRepository;
    private final FotoRepository fotoRepository;
    private final ContactarPorRepository contactarPorRepository;
    private final ComentarioRepository comentarioRepository;
    private final NotaRepository notaRepository;

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

        // Igual que en Confessions: resolver ruta absoluta a /static
        Path staticDir = Paths.get(ResourceUtils.getFile("classpath:static").getAbsolutePath());
        this.pathStatic = staticDir.toString();
        System.out.println("Static path resolved to: " + this.pathStatic);
    }

    // ================================
    // PORTADA: últimos 5 avisos
    // ================================
    public List<Map<String, String>> getPortadaData(Integer pageSize) {
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

            // Foto principal
            Foto img = fotoRepository.findFirstByAvisoIdOrderByIdAsc(aviso.getId());
            String pathImage = null;
            if (img != null) {
                // archivo guardado en /static/uploads/<filename>
                pathImage = img.getRutaArchivo() + img.getNombreArchivo();
            }

            String um = "mes(es)";
            if ("a".equalsIgnoreCase(aviso.getUnidadMedida())) {
                um = "año(s)";
            }

            String sec = (aviso.getSector() == null) ? "" : aviso.getSector();
            String des = (aviso.getDescripcion() == null) ? "" : aviso.getDescripcion();

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

        // --- Construir lista de fotos (al menos foto1) ---
        List<MultipartFile> fotos = new ArrayList<>();
        if (foto1 != null && !foto1.isEmpty()) {
            fotos.add(foto1);
        }
        if (foto2 != null && !foto2.isEmpty()) {
            fotos.add(foto2);
        }
        if (foto3 != null && !foto3.isEmpty()) {
            fotos.add(foto3);
        }
        if (foto4 != null && !foto4.isEmpty()) {
            fotos.add(foto4);
        }
        if (foto5 != null && !foto5.isEmpty()) {
            fotos.add(foto5);
        }

        // --- Valores de contactos (contacto-<via>) ---
        List<String> contactosValues = new ArrayList<>();
        if (contactos != null) {
            for (String c : contactos) {
                String key = "contacto-" + c;
                String value = allRequestParams.get(key);
                contactosValues.add(value);
            }
        }


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

            // Generar nombre único (hash)
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

            String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if (!extension.matches("jpg|jpeg|png|gif")) {
                throw new IllegalArgumentException("Invalid file extension: " + extension);
            }

            String finalName = filename + "." + extension;
            Path targetPath = uploadsDir.resolve(finalName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            savedFilenames.add(finalName);
        }

        // --- Buscar comuna por nombre ---
        Comuna comuna = comunaRepository.findByNombre(comunaNombre);
        Integer comunaId = (comuna == null) ? null : comuna.getId();

        // Normalizar tipo y unidad de medida
        String tipoNorm = (tipo == null) ? null : tipo.toLowerCase();
        String unidadCode = "m";
        if ("Año(s)".equalsIgnoreCase(unidadMedida)) {
            unidadCode = "a";
        }

        // Crear aviso
        LocalDateTime fechaIngreso = LocalDateTime.now();
        Aviso aviso = new Aviso(
            comunaId,
            sector,
            nombre,
            email,
            celular,
            tipoNorm,
            cantidad,
            edad,
            unidadCode,
            fechaEntrega,
            descripcion,
            fechaIngreso
        );
        aviso = avisoRepository.save(aviso);

        // Crear fotos asociadas
        for (String filename : savedFilenames) {
            Foto foto = new Foto(
                "uploads/",
                filename,
                aviso.getId()
            );
            fotoRepository.save(foto);
        }

        // Crear contactos asociados
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
    public Map<String, Object> getListadoData(Integer page) {
        int pageSize = 5;
        int pageIndex = (page == null || page < 1) ? 0 : (page - 1);

        Page<Aviso> pageResult = avisoRepository.findAllByOrderByFechaIngresoDesc(
            PageRequest.of(pageIndex, pageSize)
        );

        List<Aviso> avisos = pageResult.getContent();
        int totalPages = pageResult.getTotalPages();

        List<Map<String, String>> data = new ArrayList<>();

        for (Aviso aviso : avisos) {
            Map<String, String> avisoData = new HashMap<>();



            Comuna comuna = null;
            if (aviso.getComunaId() != null) {
                comuna = comunaRepository.findById(aviso.getComunaId()).orElse(null);
            }

            Foto img = fotoRepository.findFirstByAvisoIdOrderByIdAsc(aviso.getId());
            String pathImage = null;
            if (img != null) {
                pathImage = img.getRutaArchivo() + img.getNombreArchivo();
            }

            // --- AÑADIR CÁLCULO DE NOTA PROMEDIO ---
            List<Nota> notas = notaRepository.findAllByAvisoId(aviso.getId());
            double notaPromedio = 0.0;
            if (notas != null && !notas.isEmpty()) {
                notaPromedio = notas.stream()
                                    .mapToInt(Nota::getValor)
                                    .average()
                                    .orElse(0.0);
            }
            // Guardamos el promedio formateado a 1 decimal
            avisoData.put("nota_promedio", String.format("%.1f", notaPromedio));
            // --- FIN DE CÁLCULO ---

            String um = "mes(es)";
            if ("a".equalsIgnoreCase(aviso.getUnidadMedida())) {
                um = "año(s)";
            }

            String sec = (aviso.getSector() == null) ? "" : aviso.getSector();
            String des = (aviso.getDescripcion() == null) ? "" : aviso.getDescripcion();

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

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("page", page);
        result.put("total_pages", totalPages);

        return result;
    }

    // ================================
    // DETALLES de un aviso
    // ================================
    public Map<String, Object> getDetallesAviso(Integer id) {
        Map<String, Object> result = new HashMap<>();

        Aviso aviso = avisoRepository.findById(id).orElse(null);
        if (aviso == null) {
            return result;
        }

        List<Foto> fotos = fotoRepository.findAllByAvisoIdOrderByIdAsc(aviso.getId());
        List<ContactarPor> contactos = contactarPorRepository.findAllByAvisoId(aviso.getId());

        Comuna comuna = null;
        Region region = null;

        if (aviso.getComunaId() != null) {
            comuna = comunaRepository.findById(aviso.getComunaId()).orElse(null);
            if (comuna != null && comuna.getRegionId() != null) {
                region = regionRepository.findById(comuna.getRegionId()).orElse(null);
            }
        }

        result.put("aviso", aviso);
        result.put("fotos", fotos);
        result.put("contactos", contactos);
        result.put("comuna", comuna);
        result.put("region", region);

        return result;
    }
}
