function submitNota(selectElement) {
    const notaValue = selectElement.value;
    if (!notaValue) return;

    const form = selectElement.closest('form');
    const avisoId = form.dataset.avisoId;
    
    const data = { nota: notaValue };

    fetch(`/aviso/${avisoId}/nueva_nota`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error al enviar nota');
        }
        return response.json(); 
    })
    .then(data => {
        // data es {"notaPromedio": 5.5, "totalVotos": 1.0}

        const fila = selectElement.closest('tr');
        const celdaPromedio = fila.querySelector('.nota-promedio-cell');

        // ¡AQUÍ ESTÁ LA LÓGICA SIMPLIFICADA!
        if (celdaPromedio) {
            // Comprobamos el promedio que viene del API
            if (data.notaPromedio == 0.0) {
                celdaPromedio.innerText = '-';
            } else {
                // Si hay votos, formateamos y mostramos el promedio
                const nuevoPromedio = data.notaPromedio.toFixed(1);
                celdaPromedio.innerText = `${nuevoPromedio}`;
            }
        }
    })
    .catch(err => {
        console.error("Error al publicar nota:", err);
        alert("Error al guardar tu nota. Inténtalo de nuevo.");
        selectElement.value = "";
    });
}