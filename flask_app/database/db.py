# =====================================================
# CONFIGURACIÓN DE LA BASE DE DATOS (SQLAlchemy + MySQL)
# =====================================================

from sqlalchemy import create_engine, Column, Integer, BigInteger, String, ForeignKey, DateTime, func
from sqlalchemy.orm import sessionmaker, declarative_base, relationship

# Parámetros de conexión a la base de datos MySQL
DB_NAME = "tarea2"
DB_USERNAME = "cc5002"
DB_PASSWORD = "programacionweb"
DB_HOST = "localhost"
DB_PORT = 3306

# URL de conexión para SQLAlchemy
DATABASE_URL = f"mysql+pymysql://{DB_USERNAME}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

# Se crea el motor de conexión (engine)
# echo=False evita que se impriman las consultas SQL
engine = create_engine(DATABASE_URL, echo=False, future=True)

# Se define una fábrica de sesiones (para interactuar con la BD)
SessionLocal = sessionmaker(bind=engine)

# Se crea la clase base a partir de la cual heredarán los modelos
Base = declarative_base()


# =====================================================
# DEFINICIÓN DE MODELOS (Tablas de la base de datos)
# =====================================================

# --- Tabla REGION ---
class Region(Base):
    __tablename__ = "region"

    id = Column(Integer, primary_key=True)          # Identificador único
    nombre = Column(String(100), nullable=False)    # Nombre de la región

    # Relación uno-a-muchos con Comuna
    comunas = relationship("Comuna", back_populates="region")


# --- Tabla COMUNA ---
class Comuna(Base):
    __tablename__ = "comuna"

    id = Column(Integer, primary_key=True)
    nombre = Column(String(100), nullable=False)
    region_id = Column(Integer, ForeignKey("region.id"), nullable=False)

    # Relaciones inversas
    region = relationship("Region", back_populates="comunas")
    avisos = relationship("Aviso", back_populates="comuna")


# --- Tabla AVISO (principal del sistema de adopciones) ---
class Aviso(Base):
    __tablename__ = 'aviso_adopcion'

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    fecha_ingreso = Column(DateTime, nullable=False, default=func.now())  # Fecha automática al crear

    comuna_id = Column(Integer, ForeignKey("comuna.id"), nullable=False)
    comuna = relationship("Comuna", back_populates="avisos")

    # Datos del aviso
    sector = Column(String(255), nullable=True)
    nombre = Column(String(255), nullable=False)
    email = Column(String(255), nullable=False)
    celular = Column(String(255), nullable=True)
    tipo = Column(String(255), nullable=False)
    cantidad = Column(String(255), nullable=False)
    edad = Column(String(255), nullable=False)
    unidad_medida = Column(String(255), nullable=False)
    fecha_entrega = Column(String(255), nullable=False)
    descripcion = Column(String(255), nullable=True)

    # Relaciones con otras tablas
    fotos = relationship("Foto", back_populates="aviso", cascade="all, delete-orphan")
    contactos = relationship("ContactarPor", back_populates="aviso", cascade="all, delete-orphan")
    comentarios = relationship("Comentario", back_populates="aviso", cascade="all, delete-orphan")


# --- Tabla CONTACTAR_POR (formas de contacto) ---
class ContactarPor(Base):
    __tablename__ = "contactar_por"

    id = Column(Integer, primary_key=True)
    nombre = Column(String(50), nullable=False)        # Ej: "WhatsApp", "Correo"
    identificador = Column(String(150), nullable=False) # Ej: número o email
    aviso_id = Column(Integer, ForeignKey("aviso_adopcion.id"), nullable=False)

    aviso = relationship("Aviso", back_populates="contactos")


# --- Tabla FOTO ---
class Foto(Base):
    __tablename__ = "foto"

    id = Column(Integer, primary_key=True)
    ruta_archivo = Column(String(255), nullable=False)  # Ruta en el servidor
    nombre_archivo = Column(String(255), nullable=False)
    aviso_id = Column(Integer, ForeignKey("aviso_adopcion.id"), nullable=False)

    aviso = relationship("Aviso", back_populates="fotos")


# --- Tabla COMENTARIO ---
class Comentario(Base):
    __tablename__ = "comentario"

    id = Column(Integer, primary_key=True)
    nombre = Column(String(80), nullable=False)           # Autor del comentario
    texto = Column(String(250), nullable=False)
    fecha = Column(DateTime, nullable=False, default=func.now()) # Fecha automática
    aviso_id = Column(Integer, ForeignKey("aviso_adopcion.id"), nullable=False)

    aviso = relationship("Aviso", back_populates="comentarios")

    # Convierte el comentario a diccionario (útil para respuestas JSON)
    def to_dict(self):
        return {
            'id': self.id,
            'nombre': self.nombre,
            'texto': self.texto,
            'fecha': self.fecha.strftime("%Y-%m-%d %H:%M")
        }


# =====================================================
# FUNCIONES DE ACCESO Y MANEJO DE DATOS (CRUD)
# =====================================================

# Obtiene un objeto por ID desde una tabla
def get_id(id, Tabla):
    session = SessionLocal()
    value = session.query(Tabla).filter_by(id=id).first()
    session.close()
    return value

# Obtiene un objeto por nombre desde una tabla
def get_name(name, Tabla):
    session = SessionLocal()
    user = session.query(Tabla).filter_by(nombre=name).first()
    session.close()
    return user


# Crea un nuevo aviso en la base de datos
def create_aviso(comuna, sector, nombre, email, celular, tipo, cantidad, edad, unidad_medida, fecha_entrega, descripcion):
    session = SessionLocal()
    new_aviso = Aviso(
        comuna=comuna,
        sector=sector,
        nombre=nombre,
        email=email,
        celular=celular,
        tipo=tipo,
        cantidad=cantidad,
        edad=edad,
        unidad_medida=unidad_medida,
        fecha_entrega=fecha_entrega,
        descripcion=descripcion
    )
    session.add(new_aviso)
    session.commit()
    session.refresh(new_aviso)  # Refresca para obtener el ID asignado
    aviso_id = new_aviso.id
    session.close()
    return aviso_id


# Crea una nueva foto asociada a un aviso
def create_foto(path, name, aviso_id):
    session = SessionLocal()
    new_foto = Foto(ruta_archivo=path, nombre_archivo=name, aviso_id=aviso_id)
    session.add(new_foto)
    session.commit()
    session.close()


# Crea una nueva forma de contacto asociada a un aviso
def create_contacto(via, value, aviso_id):
    session = SessionLocal()
    new_contacto = ContactarPor(nombre=via, identificador=value, aviso_id=aviso_id)
    session.add(new_contacto)
    session.commit()
    session.close()


# Obtiene una lista paginada de avisos (para mostrar por partes)
def get_avisos(page_size, page):
    session = SessionLocal()
    avisos = session.query(Aviso)\
                    .order_by(Aviso.fecha_ingreso.desc())\
                    .offset(page)\
                    .limit(page_size)\
                    .all()
    session.close()
    return avisos


# Devuelve todas las regiones
def get_regiones():
    session = SessionLocal()
    regiones = session.query(Region).all()
    session.close()
    return regiones


# Devuelve todas las comunas de una región específica
def get_comunas(region):
    session = SessionLocal()
    comunas = session.query(Region).filter_by(id=region.id).all() 
    session.close()
    return comunas


# Obtiene la primera foto asociada a un aviso (para mostrar en listados)
def get_first_foto(_id):
    session = SessionLocal()
    foto = session.query(Foto).filter_by(aviso_id=_id).first()
    session.close()
    return foto


# Cuenta el total de filas en una tabla
def count(Tabla):
    session = SessionLocal()
    total = session.query(func.count(Tabla.id)).scalar()
    session.close()
    return total


# Cuenta la cantidad de avisos agrupados por tipo (para gráfico de torta)
def count_by_type():
    session = SessionLocal()
    total = session.query(
        Aviso.tipo,
        func.count(Aviso.id).label("cantidad")
    ).group_by(Aviso.tipo).all()
    session.close()
    return total


# Cuenta la cantidad de avisos agrupados por día (para gráfico de líneas)
def count_by_day():
    session = SessionLocal()
    total = session.query(
        func.date_format(Aviso.fecha_ingreso, '%Y-%m-%d').label("dia"),
        func.count(Aviso.id).label("cantidad")
    ).group_by("dia").order_by("cantidad").all()
    session.close()
    return total


# Cuenta la cantidad de avisos agrupados por mes y tipo (para gráfico de barras)
def count_by_month():
    session = SessionLocal()
    total = session.query(
        func.date_format(Aviso.fecha_ingreso, '%Y-%m').label("mes"),
        Aviso.tipo,
        func.count(Aviso.id).label("cantidad")
    ).group_by("mes", Aviso.tipo).order_by("mes").all()
    session.close()
    return total


# Obtiene los registros asociados a un aviso (como fotos o comentarios)
def get_aviso_id(_id, Tabla):
    session = SessionLocal()
    info = session.query(Tabla).filter_by(aviso_id=_id).all()
    session.close()
    return info


# Crea un nuevo comentario asociado a un aviso
def create_comm(nombre, texto, aviso_id):
    session = SessionLocal()
    new_comm = Comentario(nombre=nombre, texto=texto, aviso_id=aviso_id)
    session.add(new_comm)
    session.commit()
    session.refresh(new_comm)
    dict_comm = new_comm.to_dict()  # Se devuelve en formato JSON
    session.close()
    return dict_comm