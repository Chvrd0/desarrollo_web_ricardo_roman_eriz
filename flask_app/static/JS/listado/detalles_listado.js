const validateText = (text, min, max) => {
    if (!text) return false;
    let lengthValid = text.trim().length <= max && text.trim().length >= min;
    return lengthValid;
};

document.addEventListener("DOMContentLoaded", () => {
    
    const form = document.getElementById("form-comentario");
    const commentListDiv = document.getElementById("lista-comentarios");
    const avisoId = form.dataset.avisoId;

    const createCommentElement = (comment) => {
        const commentDiv = document.createElement("div");
        commentDiv.classList.add("comentario-item");
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

    const loadComments = () => {
        fetch(`http://127.0.0.1:5000/aviso/${avisoId}/comentarios`)
            .then(response => response.json())
            .then(comments => {
                commentListDiv.innerHTML = ""; // Limpia la lista
                if (comments.length === 0) {
                    commentListDiv.innerHTML = "<p>No hay comentarios aún. ¡Sé el primero!</p>";
                } else {
                    comments.forEach(comment => {
                        const commentEl = createCommentElement(comment);
                        commentListDiv.appendChild(commentEl);
                    });
                }
            })
            .catch(err => {
                console.error("Error cargando comentarios:", err);
                commentListDiv.innerHTML = "<p style='color:red;'>Error al cargar comentarios.</p>";
            });
    };

    form.addEventListener("submit", (event) => {
        event.preventDefault();

        const nombreInput = document.getElementById("c_nombre");
        const textoInput = document.getElementById("c_texto");
        const validationBox = document.getElementById("val-box");
        const validationMessageElem = document.getElementById("val-msg");
        const validationListElem = document.getElementById("val-list");

        let invalidInputs = [];
        let isValid = true;
        const setInvalidInput = (inputName) => {
        invalidInputs.push(inputName);
        isValid &&= false;
    };
        
        if (!validateText(nombreInput.value, 3, 80)) {
            setInvalidInput("Nombre (El largo debe estar entre 3 y 80 caracteres)");
        }
        if (!validateText(textoInput.value, 5, Infinity)) {
            setInvalidInput("Texto (El largo ser superior a 5 caracteres)");
        }

        if (!isValid) {
            validationListElem.innerHTML = "";
            for (input of invalidInputs) {
            let listElement = document.createElement("li");
                listElement.innerText = input;
                validationListElem.append(listElement);
            };
            validationMessageElem.innerText = "Los siguientes campos son inválidos:";
            validationBox.style.borderLeftColor = "#f44336";
            validationBox.hidden = false;
        } else {
            validationBox.hidden = true;

            const data = {
                c_nombre: nombreInput.value,
                c_texto: textoInput.value
            };

            fetch(`http://127.0.0.1:5000/aviso/${avisoId}/nuevo_comentario`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
            })
            .then(response => response.json())
            .then(newComment => {
                nombreInput.value = "";
                textoInput.value = "";
                loadComments();
            })
            .catch(err => {
                console.error("Error al publicar comentario:", err);
                alert("Error al publicar comentario. Inténtalo de nuevo.");
            });
        }
    });

    loadComments();
});