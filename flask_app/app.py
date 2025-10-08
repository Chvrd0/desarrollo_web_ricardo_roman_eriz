from flask import Flask, request, render_template, redirect, url_for, session
from utils.validations import *
from database import db
from werkzeug.utils import secure_filename
import hashlib
import filetype
import os
from datetime import datetime

UPLOAD_FOLDER = 'static/uploads'

app = Flask(__name__)


app.secret_key = "s3cr3t_k3y"
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
# app.config['MAX_CONTENT_LENGTH'] = 16 * 1000 * 1000



# --- Routes ---
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

@app.route("/nuevo-aviso", methods=["GET"])
def formulario():
    return render_template("formulario/_formulario.html")


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

    if validarAviso(comuna, sector, nombre, email, celular, tipo, cantidad, edad, unidad_medida, fecha_entrega, descripcion, fotos, contactos_values):

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
        return redirect(url_for("index"))
    else:
        return redirect(url_for("formulario"))


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

@app.route("/estadisticas", methods=["GET"])
def estadisticas():
    return render_template("estadisticas/_estadisticas.html")

@app.route("/aviso/<id>")
def detalles(id):
    aviso = db.get_id(id, db.Aviso)
    fotos = db.get_aviso_id(id, db.Foto)
    contactos = db.get_aviso_id(id, db.ContactarPor)
    comuna = db.get_id(aviso.comuna_id, db.Comuna)
    region = db.get_id(comuna.region_id, db.Region)

    return render_template("listado/detalles_listado.html", aviso=aviso, fotos=fotos, contactos=contactos, comuna=comuna, region=region)


if __name__ == "__main__":
    app.run(debug=True)
