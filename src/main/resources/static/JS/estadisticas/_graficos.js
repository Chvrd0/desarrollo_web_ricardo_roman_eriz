// ============================
// Gráfico 1: Avisos publicados por día (línea)
// ============================

// Se crea un gráfico con Highcharts dentro del elemento HTML con id="grafico-1"
Highcharts.chart("grafico-1", {
    chart: {
        type: "line", // Tipo de gráfico: línea
    },
    title: {
        text: "Numero de Avisos Enviados en el Tiempo", // Título del gráfico
    },
    xAxis: {
        type: "datetime", // El eje X representa fechas
        dateTimeLabelFormats: {
            month: "%b %e, %Y", // Formato para mostrar fechas (Ej: Oct 21, 2025)
        },
        title: {
            text: "Fecha", // Título del eje X
        },
    },
    yAxis: {
        title: {
            text: "Numero de Avisos", // Título del eje Y
        },
    },
    legend: {
        align: "left",
        verticalAlign: "top",
        borderWidth: 0, // Quita el borde de la leyenda
    },
    tooltip: {
        shared: true, // Muestra tooltip compartido al pasar el mouse
        crosshairs: true, // Muestra una línea cruzada en los ejes
    },
    series: [
        {
            name: "Avisos", // Nombre de la serie
            data: [], // Se deja vacío inicialmente (se llenará con fetch)
            lineWidth: 1, // Grosor de la línea
            marker: {
                enabled: true, // Habilita los puntos sobre la línea
                radius: 4, // Tamaño de los puntos
            },
            color: "#FC2865", // Color de la línea
        },
    ],
});


// ============================
// Gráfico 2: Porcentaje de avisos por tipo (torta)
// ============================

Highcharts.chart('grafico-2', {
    chart: {
        type: 'pie', // Tipo de gráfico: torta o pastel
    },
    title: {
        text: 'Porcentaje de Avisos por Tipo', // Título del gráfico
    },
    series: [{
        name: 'Tipos de Avisos', // Nombre de la serie
        data: [] // Se completará con los datos del servidor
    }]
});


// ============================
// Gráfico 3: Cantidad de avisos por mes y tipo (barras)
// ============================

Highcharts.chart('grafico-3', {
    chart: {
        type: 'column'
    },
    title: {
        text: 'Cantidad de Anuncios de Mascotas por Mes'
    },
    xAxis: {
        categories: []
    },
    yAxis: {
        min: 0,
        title: {
            text: 'Cantidad de Anuncios'
        }
    },
    series: [{
        name: 'Perros',
        data: [10, 15]
    }, {
        name: 'Gatos',
        data: [5, 7]
    }]
});


// ============================
// PETICIÓN 1: Datos del gráfico de líneas
// ============================

// Se solicita al backend Flask la información en formato JSON
fetch("/get-line-data")
    .then((response) => response.json()) // Se convierte la respuesta a JSON
    .then((data) => {
        // El backend envía una lista de objetos { dia: "YYYY-MM-DD", cantidad: N }
        console.log(data);
        let parsedData = data.map((item) => {
            // Se separa el año, mes y día de la fecha
            const [year, month, day] = item.dia
                .split("-")
                .map((part) => parseInt(part, 10));

            // Highcharts usa timestamps en milisegundos => Date.UTC convierte a ese formato
            return [
                Date.UTC(year, month - 1, day), // El mes se resta 1 (enero = 0)
                item.cantidad, // Valor correspondiente al día
            ];
        });
        console.log(parsedData);

        // Se busca el gráfico correspondiente a "grafico-1"
        const chart = Highcharts.charts.find(
            (chart) => chart && chart.renderTo.id === "grafico-1"
        );

        // Se actualiza el gráfico con los nuevos datos
        chart.update({
            series: [
                {
                    data: parsedData, // Se insertan los datos formateados
                },
            ],
        });
    })
    .catch((error) => console.error("Error:", error)); // Si hay error en el fetch, se muestra en consola


// ============================
// PETICIÓN 2: Datos del gráfico de torta
// ============================

fetch("/get-pie-data")
    .then((response) => response.json()) // Convierte respuesta a JSON
    .then((data) => {
        console.log(data);
        // Se busca el gráfico de torta
        const chart = Highcharts.charts.find(
            (chart) => chart && chart.renderTo.id === "grafico-2"
        );

        // Se actualiza el gráfico con los datos obtenidos
        chart.update({
            series: [
                {
                    data: data, // Lista de pares [tipo, porcentaje]
                },
            ],
        });
    })
    .catch((error) => console.error("Error:", error)); // Manejo de error


// ============================
// PETICIÓN 3: Datos del gráfico de barras
// ============================

fetch("/get-bar-data") 
    .then((response) => response.json()) // Convierte respuesta a JSON
    .then((data) => {
        console.log(data.series);
        // Se busca el gráfico de barras
        const chart = Highcharts.charts.find(
            (chart) => chart && chart.renderTo.id === "grafico-3"
        );
        
        // Se actualiza el gráfico con los datos del servidor
        chart.update({
            xAxis: {
                categories: data.labels // Etiquetas del eje X (por ejemplo, meses)
            },
            series: data.series // Conjunto de series 
        });
    })
    .catch((error) => console.error("Error:", error)); // Manejo de error