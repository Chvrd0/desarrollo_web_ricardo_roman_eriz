let formulario = document.getElementById("publicar-btn");
formulario.addEventListener("click", () => {
            let portada_formulario = document.forms["p_formulario"];
            portada_formulario.submit();
        });

let listado = document.getElementById("listado-btn");
listado.addEventListener("click", () => {
            let portada_listado = document.forms["p_listado"];
            portada_listado.submit();
        });

let stats = document.getElementById("stats-btn");
stats.addEventListener("click", () => {
            let portada_estadisticas = document.forms["p_estadisticas"];
            portada_estadisticas.submit();
        });