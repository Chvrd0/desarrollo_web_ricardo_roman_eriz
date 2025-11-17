function submitNota(selectElement) {
    const notaValue = selectElement.value;
    if (!notaValue) return; // No hacer nada si selecciona la opción vacía "-"

    // Encontrar el formulario padre para obtener el ID del aviso
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
    .then(nuevaNota => {
        alert('¡Gracias por tu nota!');
        // Recargamos la página para que se actualice el promedio
        window.location.reload(); 
    })
    .catch(err => {
        console.error("Error al publicar nota:", err);
        alert("Error al guardar tu nota. Inténtalo de nuevo.");
        // Resetea el select por si falla
        selectElement.value = "";
    });
}