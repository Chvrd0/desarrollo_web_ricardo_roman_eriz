// Gráfico 1: Cantidad de avisos publicados por día (Gráfico de líneas)
Highcharts.chart("grafico-1", {
    chart: {
        type: "line",
    },
    title: {
        text: "Numero de Avisos Enviados en el Tiempo",
    },
    xAxis: {
        type: "datetime",
        dateTimeLabelFormats: {
        month: "%b %e, %Y",
        },
        title: {
        text: "Fecha",
        },
    },
    yAxis: {
        title: {
        text: "Numero de Avisos",
        },
    },
    legend: {
        align: "left",
        verticalAlign: "top",
        borderWidth: 0,
    },

    tooltip: {
        shared: true,
        crosshairs: true,
    },

    series: [
        {
        name: "Avisos",
        data: [],
        lineWidth: 1,
        marker: {
            enabled: true,
            radius: 4,
        },
        color: "#FC2865",
        },
    ],
});

// Gráfico 2: Porcentaje de avisos de cada tipo (Gráfico de torta)
Highcharts.chart('grafico-2', {
    chart: {
        type: 'pie'
    },
    title: {
        text: 'Porcentaje de Avisos por Tipo'
    },
    series: [{
        name: 'Tipos de Avisos',
        data: []
    }]
});

// Gráfico 3: Cantidad de avisos por mes y tipo (Gráfico de barras)
Highcharts.chart('grafico-3', {
    chart: {
        type: 'bar'
    },
    title: {
        text: 'Gráfico de Barras Múltiples'
    },
    xAxis: {
        categories: []
    },
    yAxis: {
        min: 0,
        title: {
            text: 'Valores'
        }
    },
    series: []
});


fetch("http://127.0.0.1:5000/get-line-data")
    .then((response) => response.json())
    .then((data) => {
        let parsedData = data.map((item) => {
        const [year, month, day] = item.dia
            .split("-")
            .map((part) => parseInt(part, 10));
        return [
            Date.UTC(year, month - 1, day), // javascript month indices start from 0 !
            item.cantidad,
        ];
        });

        // Get the chart by ID
        const chart = Highcharts.charts.find(
        (chart) => chart && chart.renderTo.id === "grafico-1"
        );

        // Update the chart with new data
        chart.update({
            series: [
                {
                data: parsedData,
                },
            ],
        });
    })
    .catch((error) => console.error("Error:", error));

fetch("http://127.0.0.1:5000/get-pie-data")
    .then((response) => response.json())
    .then((data) => {
        // Get the chart by ID
        const chart = Highcharts.charts.find(
        (chart) => chart && chart.renderTo.id === "grafico-2"
        );

        // Update the chart with new data
        chart.update({
            series: [
                {
                data: data,
                },
            ],
        });
    })
    .catch((error) => console.error("Error:", error));

fetch("http://127.0.0.1:5000/get-bar-data") 
    .then((response) => response.json())
    .then((data) => {
        // Get the chart by ID
        const chart = Highcharts.charts.find(
            (chart) => chart && chart.renderTo.id === "grafico-3"
        );
        

        chart.update({
            xAxis: {
                categories: data.labels,
            },
            series: data.series,
        });
    })
    .catch((error) => console.error("Error:", error));
