from flask import Flask, request, render_template, redirect, url_for, session, jsonify
from flask_cors import cross_origin
from utils.validations import *
from database import db
from werkzeug.utils import secure_filename
import hashlib
import filetype
import os
from datetime import datetime
import random

UPLOAD_FOLDER = 'static/uploads'

app = Flask(__name__)


app.secret_key = "s3cr3t_k3y"
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
# app.config['MAX_CONTENT_LENGTH'] = 16 * 1000 * 1000



# --- Routes ---
#-------------------------Portada--------------------------------------------------------------------------------------
# Renderizar la pestaña inicial, con la información de los últimos 5 avisos.
@app.route("/", methods=["GET"])
def index():
    data = []
    for aviso in db.get_avisos(5, 0):
        img = db.get_first_foto(aviso.id)
        if aviso.unidad_medida == "a":
            um = "año(s)"
        else:
            um = "mes(es)"

        if aviso.sector == None:
            sec = ""
        else:
            sec = aviso.sector
        
        data.append({
            "fecha_ingreso": aviso.fecha_ingreso,
            "comuna": db.get_id(aviso.comuna_id, db.Comuna).nombre,
            "sector": sec,
            "tipo": aviso.tipo,
            "cantidad": aviso.cantidad,
            "edad": aviso.edad,
            "unidad_medida": um,
            "descripcion": aviso.descripcion,
            "path_image": url_for(f'{img.ruta_archivo}', filename=f"{img.nombre_archivo}")
        })
    
    return render_template("portada/_portada.html", data=data)


#-------------------------Formulario--------------------------------------------------------------------------------------
# Renderizar la pestaña para el formulario:
@app.route("/nuevo-aviso", methods=["GET"])
def formulario():
    return render_template("formulario/_formulario.html")

# Publicar el nuevo aviso:
@app.route("/post-aviso", methods=["POST"])
def enviar_formulario():

    foto1 = request.files.get("foto1")
    foto2 = request.files.get("foto2")
    foto3 = request.files.get("foto3")
    foto4 = request.files.get("foto4")
    foto5 = request.files.get("foto5")
    fotos = [foto1]
    if foto2.filename != "": fotos.append(foto2)
    if foto3.filename != "": fotos.append(foto3)
    if foto4.filename != "": fotos.append(foto4)
    if foto5.filename != "": fotos.append(foto5)

    comuna=request.form["select-comuna"]
    sector=request.form.get("sector")
    nombre=request.form["nombre"]
    email=request.form["email"]
    celular=request.form.get("phone")
    tipo=request.form["select-tipo"]
    cantidad=int(request.form["cantidad"])
    edad=int(request.form["edad"])
    unidad_medida=request.form["select-edad"]
    fecha_entrega=request.form["entrega"]
    descripcion=request.form.get("descripcion")

    contactos = request.form.getlist("select-contacto")
    contactos_values = []
    for contacto in contactos:
        contactos_values.append(request.form["contacto-"+contacto])

    validacion = validarAviso(comuna, sector, nombre, email, celular, tipo, cantidad, edad, unidad_medida, fecha_entrega, descripcion, fotos, contactos_values)

    if validacion[0]:

        newFotos = []
        for foto in fotos:
            _filename = hashlib.sha256(
                secure_filename(foto.filename) # nombre del archivo
                .encode("utf-8") # encodear a bytes
                ).hexdigest()
            _extension = filetype.guess(foto).extension
            img_filename = f"{_filename}.{_extension}"
            newFotos.append(img_filename)
            foto.save(os.path.join(app.config["UPLOAD_FOLDER"], img_filename))

        comunaId = db.get_name(comuna, db.Comuna)

        if tipo == "Perro":
            tipo = "perro"
        elif tipo == "Gato":
            tipo = "gato"

        if unidad_medida== "Año(s)":
            unidad_medida = "a"
        elif unidad_medida =="Mes(es)":
            unidad_medida = "m"

        aviso = db.create_aviso(comunaId, sector, nombre, email, celular, tipo, cantidad, edad, unidad_medida, fecha_entrega, descripcion)
        for foto in newFotos:
            db.create_foto('static', 'uploads/' + foto, aviso)
        
        for i in range(len(contactos)):
            db.create_contacto(contactos[i], contactos_values[i], aviso)
        return render_template("formulario/msj_final_formulario.html", mensaje = "Hemos recibido correctamente tus datos! Buena suerte con tu búsqueda.")
    else:
        return render_template("formulario/msj_final_formulario.html", mensaje = f"Falló la validación de los datos. Revisa tus datos e inténtalo nuevamnete. {validacion[1]}")



#-------------------------Listado de Avisos--------------------------------------------------------------------------------------
# Renderizar la pestaña del listado de los avisos, con la información de los avisos.
@app.route("/listado", methods=["GET"])
def listado():
    page = int(request.args.get("page", 1))
    total_avisos = db.count(db.Aviso)
    offset = (page - 1) * 5
    total_pages = (total_avisos + 5 - 1) // 5

    data = []
    for aviso in db.get_avisos(5, offset):
        img = db.get_first_foto(aviso.id)
        if aviso.unidad_medida == "a":
            um = "año(s)"
        else:
            um = "mes(es)"

        if aviso.sector == None:
            sec = ""
        else:
            sec = aviso.sector

        if aviso.descripcion == None:
            des = ""
        else: 
            des = aviso.descripcion
        
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



#-------------------------Aviso Detallado--------------------------------------------------------------------------------------
# Renderizar la pestaña de un aviso en particular:
@app.route("/aviso/<id>")
def detalles(id):
    aviso = db.get_id(id, db.Aviso)
    fotos = db.get_aviso_id(id, db.Foto)
    contactos = db.get_aviso_id(id, db.ContactarPor)
    comuna = db.get_id(aviso.comuna_id, db.Comuna)
    region = db.get_id(comuna.region_id, db.Region)

    return render_template("listado/detalles_listado.html", aviso=aviso, fotos=fotos, contactos=contactos, comuna=comuna, region=region)

# Obtener asincrónicamente los comentarios de este aviso.
@app.route("/aviso/<id>/comentarios", methods=["GET"])
@cross_origin(origin="127.0.0.1", supports_credentials=True)
def get_comentarios(id):
    comentarios = db.get_aviso_id(id, db.Comentario)
    data = [i.to_dict() for i in comentarios]
    return data

# Publicar asincrónicamente un nuevo comentario.
@app.route("/aviso/<id>/nuevo_comentario", methods=["POST"])
@cross_origin(origin="127.0.0.1", supports_credentials=True)
def publicarComentario(id):
    data = request.get_json()

    if not data:
        return jsonify({'error': 'Faltan datos'})
    
    nombre = data['c_nombre']
    texto = data['c_texto']
    if validate_length(nombre, 3, 80) and validate_length(texto, 5, float('inf')):
        nuevo_comentario = db.create_comm(nombre, texto, id)
        return jsonify(nuevo_comentario)
    
    return jsonify({'error': 'Faltan datos'})



#-------------------------Estadisticas--------------------------------------------------------------------------------------
# Renderizar la pestaña de las estadísticas.
@app.route("/estadisticas", methods=["GET"])
def estadisticas():
    return render_template("estadisticas/_estadisticas.html")

# Obtener de forma asíncrona los datos para el gráfico 1 (Cantida de avisos por día):
@app.route("/get-line-data", methods=["GET"])
@cross_origin(origin="127.0.0.1", supports_credentials=True)
def get_line_stats():
    line_data = db.count_by_day()

    data = [{
        "dia": i.fecha_ingreso.strftime('%Y-%m-%d'),
        "cantidad": i.cantidad
    } for i in line_data]

    return jsonify(data)

# Obtener de forma asíncrona los datos para el gráfico 2 (Cantidad de avisos por tipo de mascota):
@app.route("/get-pie-data", methods=["GET"])
@cross_origin(origin="127.0.0.1", supports_credentials=True)
def get_pie_stats():
    pie_data = db.count_by_type()

    data = [{
        "name": i.tipo.capitalize(),
        "y": i.cantidad
    } for i in pie_data]

    return jsonify(data)

# Obtener de forma asíncrona los datos para el gráfico 3 (Cantidad de avisos mensuales por tipo de mascota):
@app.route("/get-bar-data", methods=["GET"])
@cross_origin(origin="127.0.0.1", supports_credentials=True)
def get_bar_stats():
    bar_data = db.count_by_month()
    data_procesada = {}
    tipos_animales = set() # Para saber qué tipos hay (ej. 'perro', 'gato')
    
    for row in bar_data:
        mes = row.mes
        tipo = row.tipo
        cantidad = row.cantidad
        
        if mes not in data_procesada:
            data_procesada[mes] = {}
        
        data_procesada[mes][tipo] = cantidad
        tipos_animales.add(tipo)

    # Ahora formateamos para Highcharts
    labels = sorted(data_procesada.keys()) # Meses ordenados: ['2025-09', '2025-10']
    series = []
    
    for tipo in sorted(list(tipos_animales)):
        # Para cada tipo (perro, gato), creamos su lista de datos
        datos_tipo = []
        for mes in labels:
            # Añade la cantidad si existe, o 0 si no hubo en ese mes
            datos_tipo.append(data_procesada[mes].get(tipo, 0))
            
        series.append({
            'name': tipo.capitalize(),
            'data': datos_tipo
        })

    # El JSON final tendrá las etiquetas (meses) y las series (datos de perros, datos de gatos)
    return jsonify({'labels': labels, 'series': series})

if __name__ == "__main__":
    app.run(debug=True)
