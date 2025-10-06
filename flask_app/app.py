from flask import Flask, request, render_template, redirect, url_for, session
from utils.validations import validate_login_user, validate_register_user, validate_confession
from database import db
from werkzeug.utils import secure_filename
import hashlib
import filetype
import os

UPLOAD_FOLDER = 'static/uploads'

app = Flask(__name__)


app.secret_key = "s3cr3t_k3y"
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
# app.config['MAX_CONTENT_LENGTH'] = 16 * 1000 * 1000



# --- Routes ---
@app.route("/", methods=["GET"])
def index():
    data = []
    for aviso in db.get_avisos(page_size=5):
        img_filename = f"uploads/{aviso.descripcion}.jpg" if hasattr(aviso, "foto") else "IMG/default.jpg"
        
        data.append({
            "fecha_ingreso": aviso.fecha_ingreso,
            "comuna": aviso.comuna,
            "sector": aviso.sector,
            "tipo": aviso.tipo,
            "cantidad": aviso.cantidad,
            "edad": aviso.edad,
            "unidad_medida": aviso.unidad_medida,
            "descripcion": aviso.descripcion,
            "path_image": url_for('static', filename=img_filename)
        })
    
    return render_template("portada/_portada.html", data=data)

@app.route("/nuevo-aviso", methods=["POST"])
def nuevoAviso():
    return render_template("formulario/_formulario")

@app.route("/listado", methods=["GET"])
def listado():
    data = []
    for aviso in db.get_avisos(page_size=5):
        img_filename = f"uploads/{aviso.descripcion}.jpg" if hasattr(aviso, "foto") else "IMG/default.jpg"
        
        data.append({
            "fecha_ingreso": aviso.fecha_ingreso,
            "comuna": aviso.comuna,
            "sector": aviso.sector,
            "tipo": aviso.tipo,
            "cantidad": aviso.cantidad,
            "edad": aviso.edad,
            "unidad_medida": aviso.unidad_medida,
            "descripcion": aviso.descripcion,
            "path_image": url_for('static', filename=img_filename)
        })
    return render_template("formulario/_listado", data=data)

@app.route("/estadisticas", methods=["POST"])
def estadisticas():
    return render_template("estadisticas/_estadisticas")



if __name__ == "__main__":
    app.run(debug=True)
