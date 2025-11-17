/**
 * Envía la nota seleccionada por el usuario al servidor y actualiza el promedio mostrado en la tabla.
 *
 * @param {HTMLSelectElement} selectElement - El elemento <select> desde donde se obtiene la nota.
 */
function submitNota(selectElement) {

    // Obtiene el valor seleccionado en el <select>.
    // Si el usuario no selecciona nada, se aborta la ejecución.
    const notaValue = selectElement.value;
    if (!notaValue) return;

    // Busca el formulario más cercano (padre) del <select>.
    const form = selectElement.closest('form');

    // Extrae el ID del aviso desde el atributo data-aviso-id del formulario.
    const avisoId = form.dataset.avisoId;
    
    // Prepara el cuerpo de la solicitud a enviar al servidor.
    const data = { nota: notaValue };

    // Envía la nota al backend usando fetch con método POST y JSON.
    fetch(`/aviso/${avisoId}/nueva_nota`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data),
    })
    .then(response => {
        // Si la respuesta no es exitosa, dispara un error.
        if (!response.ok) {
            throw new Error('Error al enviar nota');
        }
        return response.json(); 
    })
    .then(data => {
        // Obtiene el nuevo promedio devuelto por el backend y lo formatea a 1 decimal.
        const nuevoPromedio = data.notaPromedio.toFixed(1);

        // Ubica la fila de la tabla donde se hizo la acción.
        const fila = selectElement.closest('tr');

        // Busca la celda donde se muestra el promedio.
        const celdaPromedio = fila.querySelector('.nota-promedio-cell');
        
        // Si existe la celda, actualiza su contenido con el nuevo promedio.
        if (celdaPromedio) {
            celdaPromedio.innerText = `${nuevoPromedio}`;
        }

    })
    .catch(err => {
        // En caso de error, lo muestra en consola y notifica al usuario.
        console.error("Error al publicar nota:", err);
        alert("Error al guardar tu nota. Inténtalo de nuevo.");

        // Reinicia el select para evitar confusión.
        selectElement.value = "";
    });
}