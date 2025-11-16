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

@Service
public class ApiService {

    private final AvisoRepository avisoRepository;
    private final ComentarioRepository comentarioRepository;

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
    public List<Map<String, String>> getComentarios(Long avisoId) {
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
    public List<Map<String, String>> getLineStats() {
        List<Aviso> avisos = avisoRepository.findAll();
        Map<String, Integer> counts = new HashMap<>();

        for (Aviso aviso : avisos) {
            if (aviso.getFechaIngreso() == null) {
                continue;
            }
            String dia = aviso.getFechaIngreso().toLocalDate().toString();
            Integer current = counts.get(dia);
            if (current == null) {
                counts.put(dia, 1);
            } else {
                counts.put(dia, current + 1);
            }
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
    public List<Map<String, Object>> getPieStats() {
        List<Aviso> avisos = avisoRepository.findAll();
        Map<String, Integer> counts = new HashMap<>();

        for (Aviso aviso : avisos) {
            String tipo = aviso.getTipo();
            if (tipo == null) {
                continue;
            }
            String key = tipo.toLowerCase();
            Integer current = counts.get(key);
            if (current == null) {
                counts.put(key, 1);
            } else {
                counts.put(key, current + 1);
            }
        }

        List<Map<String, Object>> data = new ArrayList<>();
        for (String tipo : counts.keySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", capitalize(tipo));
            item.put("y", counts.get(tipo));
            data.add(item);
        }

        return data;
    }

    // ================================
    // STATS: bar (avisos por mes y tipo)
    // ================================
    public Map<String, Object> getBarStats() {
        List<Aviso> avisos = avisoRepository.findAll();

        // dataProcesada[mes][tipo] = cantidad
        Map<String, Map<String, Integer>> dataProcesada = new HashMap<>();
        List<String> tiposAnimales = new ArrayList<>();

        for (Aviso aviso : avisos) {
            if (aviso.getFechaIngreso() == null || aviso.getTipo() == null) {
                continue;
            }

            String mes = aviso.getFechaIngreso().getYear()
                + "-" + String.format("%02d", aviso.getFechaIngreso().getMonthValue());
            String tipo = aviso.getTipo().toLowerCase();

            if (!dataProcesada.containsKey(mes)) {
                dataProcesada.put(mes, new HashMap<String, Integer>());
            }
            Map<String, Integer> inner = dataProcesada.get(mes);

            Integer current = inner.get(tipo);
            if (current == null) {
                inner.put(tipo, 1);
            } else {
                inner.put(tipo, current + 1);
            }

            if (!tiposAnimales.contains(tipo)) {
                tiposAnimales.add(tipo);
            }
        }

        // labels = meses ordenados
        List<String> labels = new ArrayList<>(dataProcesada.keySet());
        labels.sort(String::compareTo);

        List<Map<String, Object>> series = new ArrayList<>();

        for (String tipo : tiposAnimales) {
            List<Integer> datosTipo = new ArrayList<>();
            for (String mes : labels) {
                Map<String, Integer> inner = dataProcesada.get(mes);
                Integer value = inner.get(tipo);
                if (value == null) {
                    value = 0;
                }
                datosTipo.add(value);
            }

            Map<String, Object> serie = new HashMap<>();
            serie.put("name", capitalize(tipo));
            serie.put("data", datosTipo);
            series.add(serie);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("series", series);

        return result;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
