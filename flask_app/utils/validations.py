import re
import filetype
from datetime import *

def validate_length(value, min, max):
    return len(value) >= min and len(value) <= max

def validate_number(value, min, max):
    return value >= min and value <= max

def validate_required(value):
    return value != None


def validate_email(value):
    return bool(re.match(r"^[\w\.-]+@[\w\.-]+\.\w+$", value))

def validate_num(value):
    if value == None: return True
    return bool(re.match(r"^\+56[\s\-\.]?9[\s\-\.]?\d{4}[\s\-\.]?\d{4}$", value))

def validate_date(value):
    fecha_hora = value.split("T")
    fecha_hora[0] = fecha_hora[0].split("-")
    fecha_hora[1] = fecha_hora[1].split(":")
    valido = datetime.now() + timedelta(hours=3)
    ingresado = datetime(int(fecha_hora[0][0]), int(fecha_hora[0][1]), int(fecha_hora[0][2]), int(fecha_hora[1][0]), int(fecha_hora[1][1])+1, 0)
    return ingresado >= valido

def validate_img(img):
    ALLOWED_EXTENSIONS = {"png", "jpg", "jpeg", "gif"}
    ALLOWED_MIMETYPES = {"image/jpeg", "image/png", "image/gif"}

    # check if a file was submitted
    if img is None:
        print("None")
        return False

    # check if the browser submitted an empty file
    if img.filename == "":
        print("Empty")
        return False
    
    # check file extension
    ftype_guess = filetype.guess(img)
    if ftype_guess.extension not in ALLOWED_EXTENSIONS:
        print("extension")
        return False
    # check mimetype
    if ftype_guess.mime not in ALLOWED_MIMETYPES:
        print("Mime")
        return False
    return True

def validarAviso(comuna, sector, nombre, email, celular, tipo, cantidad, edad, unidad_medida, fecha_entrega, descripcion, fotos, contactos):
    comunaV = validate_required(comuna)
    sectorV = sector == None or validate_length(sector, 0, 100)
    nombreV = validate_length(nombre, 3, 200)
    emailV = validate_email(email)
    celularV = validate_num(celular)
    tipoV = validate_required(tipo)
    cantidadV = validate_number(cantidad, 1, float('inf'))
    edadV = validate_number(edad, 1, float('inf'))
    unidad_medidaV = validate_required(unidad_medida)
    fecha_entregaV = validate_date(fecha_entrega)
    fotoV = True
    for foto in fotos:
        fotoV = fotoV and validate_img(foto)
    contactosV = True
    for contacto in contactos:
        contactosV = contactosV and validate_length(contacto, 4, 50)
    return comunaV and sectorV and nombreV and emailV and celularV and tipoV and cantidadV and edadV and unidad_medidaV and fecha_entregaV and fotoV and contactosV


