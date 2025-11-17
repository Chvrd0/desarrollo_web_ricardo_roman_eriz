package desarrollo_web_rre.desarrollo_web_rre.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import desarrollo_web_rre.desarrollo_web_rre.models.Aviso;
import desarrollo_web_rre.desarrollo_web_rre.models.AvisoRepository;
import desarrollo_web_rre.desarrollo_web_rre.models.Comentario;
import desarrollo_web_rre.desarrollo_web_rre.models.ComentarioRepository;

/**
 * Servicio de Spring (@Service) diseñado para exponer datos a la API.
 * Se especializa en agregar y formatear datos para endpoints de API,
 * particularmente para obtener comentarios y generar estadísticas
 * para gráficos (líneas, circular, barras).
 */
@Service
public class ApiService {

    private final AvisoRepository avisoRepository;
    private final ComentarioRepository comentarioRepository;

    /**
     * Constructor para la inyección de dependencias de los repositorios.
     *
     * @param avisoRepository Repositorio para acceder a los datos de Avisos.
     * @param comentarioRepository Repositorio para acceder a los datos de Comentarios.
     */
    public ApiService(
        AvisoRepository avisoRepository,
        ComentarioRepository comentarioRepository
    ) {
        this.avisoRepository = avisoRepository;
        this.comentarioRepository = comentarioRepository;
    }

    // ================================
    // COMENTARIOS de un aviso
    // ================================

    /**
     * Obtiene todos los comentarios para un aviso específico, ordenados por fecha ascendente.
     *
     * @param avisoId El ID del aviso del cual se quieren obtener los comentarios.
     * @return Una lista de Mapas, donde cada mapa representa un comentario formateado (id, nombre, texto, fecha).
     */
    public List<Map<String, String>> getComentarios(Integer avisoId) {
        List<Comentario> comentarios = comentarioRepository.findAllByAvisoIdOrderByFechaAsc(avisoId);
        List<Map<String, String>> data = new ArrayList<>();

        for (Comentario c : comentarios) {
            Map<String, String> item = new HashMap<>();
            item.put("id", c.getId().toString());
            item.put("nombre", c.getNombre());
            item.put("texto", c.getTexto());
            item.put("fecha", c.getFecha() == null ? "" : c.getFecha().toString());
            data.add(item);
        }

        return data;
    }

    // ================================
    // STATS: línea (avisos por día)
    // ================================

    /**
     * Genera estadísticas para un gráfico de líneas: Avisos por día.
     * Cuenta cuántos avisos se ingresaron en cada día único.
     *
     * @return Una lista de Mapas con el formato [{"dia": "YYYY-MM-DD", "cantidad": "N"}, ...].
     */
    public List<Map<String, String>> getLineStats() {
        List<Aviso> avisos = avisoRepository.findAll();
        Map<String, Integer> counts = new HashMap<>();

        // Contar avisos por día
        for (Aviso aviso : avisos) {
            if (aviso.getFechaIngreso() == null) {
                continue;
            }
            String dia = aviso.getFechaIngreso().toLocalDate().toString(); // "YYYY-MM-DD"
            counts.put(dia, counts.getOrDefault(dia, 0) + 1);
        }

        // Transformar a lista de mapas
        List<Map<String, String>> data = new ArrayList<>();
        for (String dia : counts.keySet()) {
            Map<String, String> item = new HashMap<>();
            item.put("dia", dia);
            item.put("cantidad", counts.get(dia).toString());
            data.add(item);
        }

        return data;
    }

    // ================================
    // STATS: pie (avisos por tipo)
    // ================================

    /**
     * Genera estadísticas para un gráfico circular (pie chart): Avisos por tipo.
     * Cuenta el total de avisos para cada 'tipo' (ej. "perro", "gato").
     *
     * @return Una lista de Mapas con el formato [{"name": "TipoCapitalizado", "y": N}, ...],
     * listo para ser consumido por librerías de gráficos como Highcharts.
     */
    public List<Map<String, Object>> getPieStats() {
        List<Aviso> avisos = avisoRepository.findAll();
        Map<String, Integer> counts = new HashMap<>();

        // Contar avisos por tipo
        for (Aviso aviso : avisos) {
            String tipo = aviso.getTipo();
            if (tipo == null) {
                continue;
            }
            String key = tipo.toLowerCase();
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }

        // Transformar al formato {name, y}
        List<Map<String, Object>> data = new ArrayList<>();
        for (String tipo : counts.keySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", capitalize(tipo)); // Capitaliza "perro" -> "Perro"
            item.put("y", counts.get(tipo));
            data.add(item);
        }

        return data;
    }

    // ================================
    // STATS: bar (avisos por mes y tipo)
    // ================================

    /**
     * Genera estadísticas para un gráfico de barras: Avisos por mes y tipo.
     * Agrupa los avisos por mes (YYYY-MM) y luego por tipo, creando series de datos.
     *
     * @return Un Mapa que contiene dos claves:
     * "labels": una lista de meses ordenados (ej. ["2023-10", "2023-11"]).
     * "series": una lista de series, donde cada serie es un tipo de animal
     * (ej. [{"name": "Perro", "data": [5, 8]}, {"name": "Gato", "data": [2, 3]}]).
     */
    public Map<String, Object> getBarStats() {
        List<Aviso> avisos = avisoRepository.findAll();

        // dataProcesada[mes][tipo] = cantidad
        Map<String, Map<String, Integer>> dataProcesada = new HashMap<>();
        List<String> tiposAnimales = new ArrayList<>(); // Lista de tipos únicos encontrados

        // 1. Agrupar datos por mes y tipo
        for (Aviso aviso : avisos) {
            if (aviso.getFechaIngreso() == null || aviso.getTipo() == null) {
                continue;
            }

            String mes = aviso.getFechaIngreso().getYear()
                + "-" + String.format("%02d", aviso.getFechaIngreso().getMonthValue()); // "YYYY-MM"
            String tipo = aviso.getTipo().toLowerCase();

            // Inicializar mapa interno si el mes es nuevo
            dataProcesada.putIfAbsent(mes, new HashMap<String, Integer>());
            Map<String, Integer> inner = dataProcesada.get(mes);

            // Incrementar contador para ese tipo en ese mes
            inner.put(tipo, inner.getOrDefault(tipo, 0) + 1);

            // Registrar tipo si es la primera vez que se ve
            if (!tiposAnimales.contains(tipo)) {
                tiposAnimales.add(tipo);
            }
        }

        // 2. Preparar "labels" (meses ordenados)
        List<String> labels = new ArrayList<>(dataProcesada.keySet());
        labels.sort(String::compareTo); // Orden cronológico/alfabético

        // 3. Preparar "series" (una serie por cada tipo)
        List<Map<String, Object>> series = new ArrayList<>();

        for (String tipo : tiposAnimales) {
            List<Integer> datosTipo = new ArrayList<>();
            // Recorrer los meses en orden para construir el array de datos
            for (String mes : labels) {
                Map<String, Integer> inner = dataProcesada.get(mes);
                Integer value = inner.getOrDefault(tipo, 0); // Si no hubo avisos de ese tipo ese mes, es 0
                datosTipo.add(value);
            }

            Map<String, Object> serie = new HashMap<>();
            serie.put("name", capitalize(tipo));
            serie.put("data", datosTipo);
            series.add(serie);
        }

        // 4. Ensamblar resultado final
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("series", series);

        return result;
    }

    /**
     * Método de utilidad privado para capitalizar la primera letra de un string.
     * (ej. "perro" -> "Perro").
     *
     * @param s El string a capitalizar.
     * @return El string capitalizado, o el string original si es nulo o vacío.
     */
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}