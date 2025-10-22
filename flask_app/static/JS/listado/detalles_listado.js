// =====================================================
// Función de validación de texto
// =====================================================

const validateText = (text, min, max) => {
    // Si no hay texto, retorna falso
    if (!text) return false;

    // Verifica que la longitud del texto (sin espacios extras)
    // esté entre los límites establecidos (min y max)
    let lengthValid = text.trim().length <= max && text.trim().length >= min;
    return lengthValid;
};


// =====================================================
// Bloque principal: se ejecuta al cargar completamente la página
// =====================================================

document.addEventListener("DOMContentLoaded", () => {
    
    // --- Obtención de elementos del DOM ---
    const form = document.getElementById("form-comentario");  // Formulario de comentarios
    const commentListDiv = document.getElementById("lista-comentarios"); // Contenedor de comentarios
    const avisoId = form.dataset.avisoId; // Se obtiene el ID del aviso desde el atributo data-aviso-id del formulario

    // =====================================================
    // Función: crea un elemento HTML representando un comentario
    // =====================================================
    const createCommentElement = (comment) => {
        const commentDiv = document.createElement("div");
        commentDiv.classList.add("comentario-item");

        // Se genera el contenido HTML del comentario
        commentDiv.innerHTML = `
            <div class="comentario-info">
                <strong>${comment.nombre}</strong>
                <small>${comment.fecha}</small>
            </div>
            <div class="comentario-texto">
                <p>${comment.texto}</p>
            </div>
        `;
        return commentDiv;
    };

    // =====================================================
    // Función: carga los comentarios desde el backend Flask
    // =====================================================
    const loadComments = () => {
        // Se hace una solicitud GET al endpoint correspondiente
        fetch(`http://127.0.0.1:5000/aviso/${avisoId}/comentarios`)
            .then(response => response.json())
            .then(comments => {
                // Limpia el contenedor de comentarios antes de volver a llenarlo
                commentListDiv.innerHTML = "";

                // Si no hay comentarios, muestra un mensaje
                if (comments.length === 0) {
                    commentListDiv.innerHTML = "<p>No hay comentarios aún. ¡Sé el primero!</p>";
                } else {
                    // Si hay comentarios, los recorre y los agrega al DOM
                    comments.forEach(comment => {
                        const commentEl = createCommentElement(comment);
                        commentListDiv.appendChild(commentEl);
                    });
                }
            })
            .catch(err => {
                // En caso de error, lo muestra en consola y en pantalla
                console.error("Error cargando comentarios:", err);
                commentListDiv.innerHTML = "<p style='color:red;'>Error al cargar comentarios.</p>";
            });
    };

    // =====================================================
    // Manejador del evento de envío del formulario (submit)
    // =====================================================
    form.addEventListener("submit", (event) => {
        // Previene el comportamiento por defecto (recargar la página)
        event.preventDefault();

        // Se obtienen los elementos de entrada y mensajes de validación
        const nombreInput = document.getElementById("c_nombre");
        const textoInput = document.getElementById("c_texto");
        const validationBox = document.getElementById("val-box");
        const validationMessageElem = document.getElementById("val-msg");
        const validationListElem = document.getElementById("val-list");

        // Variables de control para validaciones
        let invalidInputs = []; // Lista de campos inválidos
        let isValid = true;     // Estado general de validación

        // Función auxiliar para marcar un input como inválido
        const setInvalidInput = (inputName) => {
            invalidInputs.push(inputName);
            isValid &&= false; // Marca el formulario como inválido
        };
        
        // --- Validaciones específicas ---
        if (!validateText(nombreInput.value, 3, 80)) {
            setInvalidInput("Nombre (El largo debe estar entre 3 y 80 caracteres)");
        }
        if (!validateText(textoInput.value, 5, Infinity)) {
            setInvalidInput("Texto (El largo debe ser superior a 5 caracteres)");
        }

        // =====================================================
        // Si hay errores de validación
        // =====================================================
        if (!isValid) {
            // Limpia mensajes anteriores
            validationListElem.innerHTML = "";

            // Agrega cada mensaje de error a la lista
            for (input of invalidInputs) {
                let listElement = document.createElement("li");
                listElement.innerText = input;
                validationListElem.append(listElement);
            }

            // Muestra el mensaje principal de error
            validationMessageElem.innerText = "Los siguientes campos son inválidos:";
            validationBox.style.borderLeftColor = "#f44336"; // Rojo
            validationBox.hidden = false; // Muestra el recuadro de error
        } 
        // =====================================================
        // Si pasa la validación → Enviar comentario al servidor
        // =====================================================
        else {
            validationBox.hidden = true; // Oculta mensajes de validación

            // Se prepara el cuerpo del POST en formato JSON
            const data = {
                c_nombre: nombreInput.value,
                c_texto: textoInput.value
            };

            // Envío de datos al backend Flask (ruta POST)
            fetch(`http://127.0.0.1:5000/aviso/${avisoId}/nuevo_comentario`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json', // Se indica formato JSON
                },
                body: JSON.stringify(data), // Se convierte el objeto a JSON
            })
            .then(response => response.json())
            .then(newComment => {
                // Limpia los campos después de enviar el comentario
                nombreInput.value = "";
                textoInput.value = "";

                // Recarga la lista de comentarios actualizada
                loadComments();
            })
            .catch(err => {
                console.error("Error al publicar comentario:", err);
                alert("Error al publicar comentario. Inténtalo de nuevo.");
            });
        }
    });

    // =====================================================
    // Llamada inicial: carga los comentarios al abrir la página
    // =====================================================
    loadComments();
});