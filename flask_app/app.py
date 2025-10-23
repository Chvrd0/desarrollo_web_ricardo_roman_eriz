# ============================================================
# IMPORTACIÓN DE MÓDULOS Y CONFIGURACIÓN INICIAL
# ============================================================

from flask import Flask, request, render_template, redirect, url_for, session, jsonify
from flask_cors import cross_origin  # Para permitir peticiones AJAX desde el frontend
from utils.validations import *      # Funciones de validación personalizadas
from database import db              # Módulo de base de datos (ORM SQLAlchemy)
from werkzeug.utils import secure_filename
import hashlib
import filetype
import os
from datetime import datetime
import random

# Carpeta donde se almacenarán las fotos subidas por los usuarios
UPLOAD_FOLDER = 'static/uploads'

# Se crea la aplicación Flask
app = Flask(__name__)

# Configuraciones básicas de la app
app.secret_key = "s3cr3t_k3y"                   # Llave para sesiones y seguridad
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER     # Carpeta para los uploads
# app.config['MAX_CONTENT_LENGTH'] = 16 * 1000 * 1000  # Límite opcional de tamaño de archivo


# ============================================================
# RUTAS PRINCIPALES DEL SITIO
# ============================================================


# ------------------------------------------------------------
# PORTADA: Página principal con los últimos 5 avisos publicados
# ------------------------------------------------------------
@app.route("/", methods=["GET"])
def index():
    data = []
    # Se obtienen los 5 avisos más recientes desde la base de datos
    for aviso in db.get_avisos(5, 0):
        img = db.get_first_foto(aviso.id)  # Primera foto asociada

        # Traduce la unidad de medida ('a' o 'm') a texto legible
        um = "año(s)" if aviso.unidad_medida == "a" else "mes(es)"

        # Si no hay sector, se deja vacío
        sec = aviso.sector if aviso.sector else ""
        
        # Se construye un diccionario con los datos del aviso
        data.append({
            "fecha_ingreso": aviso.fecha_ingreso,
            "comuna": db.get_id(aviso.comuna_id, db.Comuna).nombre,
            "sector": sec,
            "tipo": aviso.tipo,
            "cantidad": aviso.cantidad,
            "edad": aviso.edad,
            "unidad_medida": um,
            "descripcion": aviso.descripcion,
            # Ruta a la imagen
            "path_image": url_for(f'{img.ruta_archivo}', filename=f"{img.nombre_archivo}")
        })
    
    # Se renderiza la plantilla con los datos de los avisos
    return render_template("portada/_portada.html", data=data)


# ------------------------------------------------------------
# FORMULARIO: Crear un nuevo aviso de adopción
# ------------------------------------------------------------

# Muestra el formulario al usuario
@app.route("/nuevo-aviso", methods=["GET"])
def formulario():
    return render_template("formulario/_formulario.html")


# Procesa el envío del formulario y guarda los datos
@app.route("/post-aviso", methods=["POST"])
def enviar_formulario():
    # --- Recepción de imágenes ---
    foto1 = request.files.get("foto1")
    foto2 = request.files.get("foto2")
    foto3 = request.files.get("foto3")
    foto4 = request.files.get("foto4")
    foto5 = request.files.get("foto5")

    # Se guarda la lista de fotos subidas (al menos una)
    fotos = [foto1]
    if foto2.filename != "": fotos.append(foto2)
    if foto3.filename != "": fotos.append(foto3)
    if foto4.filename != "": fotos.append(foto4)
    if foto5.filename != "": fotos.append(foto5)

    # --- Datos del formulario ---
    comuna = request.form["select-comuna"]
    sector = request.form.get("sector")
    nombre = request.form["nombre"]
    email = request.form["email"]
    celular = request.form.get("phone")
    tipo = request.form["select-tipo"]
    cantidad = int(request.form["cantidad"])
    edad = int(request.form["edad"])
    unidad_medida = request.form["select-edad"]
    fecha_entrega = request.form["entrega"]
    descripcion = request.form.get("descripcion")

    # Contactos seleccionados (por ejemplo, WhatsApp, correo)
    contactos = request.form.getlist("select-contacto")
    contactos_values = [request.form["contacto-" + c] for c in contactos]

    # --- Validación ---
    validacion = validarAviso(
        comuna, sector, nombre, email, celular, tipo,
        cantidad, edad, unidad_medida, fecha_entrega,
        descripcion, fotos, contactos_values
    )

    # Si pasa las validaciones...
    if validacion[0]:

        # Se genera un nombre único para cada foto usando SHA256
        newFotos = []
        for foto in fotos:
            _filename = hashlib.sha256(
                secure_filename(foto.filename).encode("utf-8")
            ).hexdigest()
            _extension = filetype.guess(foto).extension
            img_filename = f"{_filename}.{_extension}"
            newFotos.append(img_filename)
            # Se guarda el archivo físicamente en /static/uploads
            foto.save(os.path.join(app.config["UPLOAD_FOLDER"], img_filename))

        # Se obtiene el ID de la comuna desde la base de datos
        comunaId = db.get_name(comuna, db.Comuna)

        # Normaliza valores del formulario
        tipo = tipo.lower()  # Perro → perro, Gato → gato
        unidad_medida = "a" if unidad_medida == "Año(s)" else "m"

        # --- Inserción en la base de datos ---
        aviso = db.create_aviso(
            comunaId, sector, nombre, email, celular,
            tipo, cantidad, edad, unidad_medida,
            fecha_entrega, descripcion
        )

        # Asocia las fotos al aviso
        for foto in newFotos:
            db.create_foto('static', 'uploads/' + foto, aviso)

        # Asocia los contactos (correo, WhatsApp, etc.)
        for i in range(len(contactos)):
            db.create_contacto(contactos[i], contactos_values[i], aviso)

        # Mensaje de éxito
        return render_template(
            "formulario/msj_final_formulario.html",
            mensaje="Hemos recibido correctamente tus datos! Buena suerte con tu búsqueda."
        )

    # Si falla la validación
    else:
        return render_template(
            "formulario/msj_final_formulario.html",
            mensaje=f"Falló la validación de los datos. Revisa tus datos e inténtalo nuevamente. {validacion[1]}"
        )


# ------------------------------------------------------------
# LISTADO DE AVISOS: Vista general paginada
# ------------------------------------------------------------
@app.route("/listado", methods=["GET"])
def listado():
    page = int(request.args.get("page", 1))
    total_avisos = db.count(db.Aviso)
    offset = (page - 1) * 5
    total_pages = (total_avisos + 5 - 1) // 5  # Cálculo de páginas

    data = []
    for aviso in db.get_avisos(5, offset):
        img = db.get_first_foto(aviso.id)

        um = "año(s)" if aviso.unidad_medida == "a" else "mes(es)"
        sec = aviso.sector if aviso.sector else ""
        des = aviso.descripcion if aviso.descripcion else ""

        data.append({
            "id": aviso.id,
            "fecha_ingreso": aviso.fecha_ingreso,
            "fecha_entrega": aviso.fecha_entrega,
            "comuna": db.get_id(aviso.comuna_id, db.Comuna).nombre,
            "sector": sec,
            "tipo": aviso.tipo,
            "cantidad": aviso.cantidad,
            "edad": aviso.edad,
            "unidad_medida": um,
            "descripcion": des,
            "nombre": aviso.nombre,
            "email": aviso.email,
            "path_image": url_for(f'{img.ruta_archivo}', filename=f"{img.nombre_archivo}")
        })

    return render_template("listado/_listado.html", data=data, page=page, total_pages=total_pages)


# ------------------------------------------------------------
# DETALLES DE UN AVISO: Vista individual
# ------------------------------------------------------------
@app.route("/aviso/<id>", methods=["GET"])
def detalles(id):
    aviso = db.get_id(id, db.Aviso)
    fotos = db.get_aviso_id(id, db.Foto)
    contactos = db.get_aviso_id(id, db.ContactarPor)
    comuna = db.get_id(aviso.comuna_id, db.Comuna)
    region = db.get_id(comuna.region_id, db.Region)

    return render_template(
        "listado/detalles_listado.html",
        aviso=aviso, fotos=fotos, contactos=contactos,
        comuna=comuna, region=region
    )


# ------------------------------------------------------------
# COMENTARIOS: Rutas asíncronas (AJAX)
# ------------------------------------------------------------

# Obtiene los comentarios de un aviso en formato JSON
@app.route("/aviso/<id>/comentarios", methods=["GET"])
@cross_origin(origin="127.0.0.1", supports_credentials=True)
def get_comentarios(id):
    comentarios = db.get_aviso_id(id, db.Comentario)
    data = [i.to_dict() for i in comentarios]
    return data


# Publica un nuevo comentario (POST desde JS)
@app.route("/aviso/<id>/nuevo_comentario", methods=["POST"])
@cross_origin(origin="127.0.0.1", supports_credentials=True)
def publicarComentario(id):
    data = request.get_json()

    if not data:
        return jsonify({'error': 'Faltan datos'})
    
    nombre = data['c_nombre']
    texto = data['c_texto']

    # Validación de largo de texto
    if validate_length(nombre, 3, 80) and validate_length(texto, 5, float('inf')):
        nuevo_comentario = db.create_comm(nombre, texto, id)
        return jsonify(nuevo_comentario)
    
    return jsonify({'error': 'Faltan datos'})


# ------------------------------------------------------------
# ESTADÍSTICAS: Render y endpoints para gráficos
# ------------------------------------------------------------

@app.route("/estadisticas", methods=["GET"])
def estadisticas():
    return render_template("estadisticas/_estadisticas.html")


# --- Datos para gráfico de línea (avisos por día) ---
@app.route("/get-line-data", methods=["GET"])
@cross_origin(origin="127.0.0.1", supports_credentials=True)
def get_line_stats():
    line_data = db.count_by_day()
    print(line_data)
    data = [{
        "dia": i.dia,
        "cantidad": i.cantidad
    } for i in line_data]
    print(data)
    return jsonify(data)


# --- Datos para gráfico de torta (avisos por tipo) ---
@app.route("/get-pie-data", methods=["GET"])
@cross_origin(origin="127.0.0.1", supports_credentials=True)
def get_pie_stats():
    pie_data = db.count_by_type()
    data = [{
        "name": i.tipo.capitalize(),
        "y": i.cantidad
    } for i in pie_data]
    return jsonify(data)


# --- Datos para gráfico de barras (avisos por mes y tipo) ---
@app.route("/get-bar-data", methods=["GET"])
@cross_origin(origin="127.0.0.1", supports_credentials=True)
def get_bar_stats():
    bar_data = db.count_by_month()
    data_procesada = {}
    tipos_animales = []  # Conjunto para identificar tipos ('perro', 'gato')

    # Procesamiento de datos
    for row in bar_data:
        mes, tipo, cantidad = row.mes, row.tipo, row.cantidad
        if mes not in data_procesada:
            data_procesada[mes] = {}
        data_procesada[mes][tipo] = cantidad
        if tipo not in tipos_animales:
            tipos_animales.append(tipo)

    # Ejes X (meses)
    labels = sorted(data_procesada.keys())
    series = []

    # Eje Y (series por tipo de animal)
    for tipo in tipos_animales:
        datos_tipo = [data_procesada[mes].get(tipo, 0) for mes in labels]
        series.append({
            'name': tipo.capitalize(),
            'data': datos_tipo
        })

    return jsonify({'labels': labels, 'series': series})


# ============================================================
# EJECUCIÓN LOCAL
# ============================================================

if __name__ == "__main__":
    app.run(debug=True)