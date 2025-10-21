from sqlalchemy import create_engine, Column, Integer, BigInteger, String, ForeignKey, DateTime, func
from sqlalchemy.orm import sessionmaker, declarative_base, relationship

DB_NAME = "tarea2"
DB_USERNAME = "cc5002"
DB_PASSWORD = "programacionweb"
DB_HOST = "localhost"
DB_PORT = 3306

DATABASE_URL = f"mysql+pymysql://{DB_USERNAME}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

engine = create_engine(DATABASE_URL, echo=False, future=True)
SessionLocal = sessionmaker(bind=engine)

Base = declarative_base()

# --- Models ---
class Region(Base):
    __tablename__ = "region"
    id = Column(Integer, primary_key=True)
    nombre = Column(String(100), nullable=False)

    comunas = relationship("Comuna", back_populates="region")


class Comuna(Base):
    __tablename__ = "comuna"
    id = Column(Integer, primary_key=True)
    nombre = Column(String(100), nullable=False)
    region_id = Column(Integer, ForeignKey("region.id"), nullable=False)

    region = relationship("Region", back_populates="comunas")
    avisos = relationship("Aviso", back_populates="comuna")



class Aviso(Base):
    __tablename__ = 'aviso_adopcion'

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    fecha_ingreso = Column(DateTime, nullable=False, default=func.now())

    comuna_id = Column(Integer, ForeignKey("comuna.id"), nullable=False)
    comuna = relationship("Comuna", back_populates="avisos")

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

    fotos = relationship("Foto", back_populates="aviso", cascade="all, delete-orphan")
    contactos = relationship("ContactarPor", back_populates="aviso", cascade="all, delete-orphan")
    comentarios = relationship("Comentario", back_populates="aviso", cascade="all, delete-orphan")

class ContactarPor(Base):
    __tablename__ = "contactar_por"
    id = Column(Integer, primary_key=True)
    nombre = Column(String(50), nullable=False)
    identificador = Column(String(150), nullable=False)
    aviso_id = Column(Integer, ForeignKey("aviso_adopcion.id"), nullable=False)

    aviso = relationship("Aviso", back_populates="contactos")


class Foto(Base):
    __tablename__ = "foto"
    id = Column(Integer, primary_key=True)
    ruta_archivo = Column(String(255), nullable=False)
    nombre_archivo = Column(String(255), nullable=False)
    aviso_id = Column(Integer, ForeignKey("aviso_adopcion.id"), nullable=False)

    aviso = relationship("Aviso", back_populates="fotos")

class Comentario(Base):
    __tablename__ = "comentario"

    id = Column(Integer, primary_key=True)
    nombre = Column(String(80), nullable=False)
    texto = Column(String(250), nullable=False)
    fecha = Column(DateTime, nullable=False, default=func.now())
    aviso_id = Column(Integer, ForeignKey("aviso_adopcion.id"), nullable=False)

    aviso = relationship("Aviso", back_populates="comentarios")


# --- Database Functions ---
def get_id(id, Tabla):
    session = SessionLocal()
    value = session.query(Tabla).filter_by(id=id).first()
    session.close()
    return value

def get_name(name, Tabla):
    session = SessionLocal()
    user = session.query(Tabla).filter_by(nombre=name).first()
    session.close()
    return user

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
    session.refresh(new_aviso)
    aviso_id = new_aviso.id
    session.close()
    return aviso_id

def create_foto(path, name, aviso_id):
    session = SessionLocal()
    new_foto = Foto(ruta_archivo=path, nombre_archivo=name, aviso_id=aviso_id)
    session.add(new_foto)
    session.commit()
    session.close()

def create_contacto(via, value, aviso_id):
    session = SessionLocal()
    new_contacto = ContactarPor(nombre = via, identificador = value, aviso_id = aviso_id)
    session.add(new_contacto)
    session.commit()
    session.close()

def get_avisos(page_size, page):
    session = SessionLocal()
    avisos = session.query(Aviso).order_by(Aviso.fecha_ingreso.desc()).offset(page).limit(page_size).all()
    session.close()
    return avisos


def get_regiones():
    session = SessionLocal()
    regiones = session.query(Region).all()
    session.close()
    return regiones

def get_comunas(region):
    session = SessionLocal()
    comunas = session.query(Region).filter_by(id=region.id).all()
    session.close()
    return comunas

def get_first_foto(_id):
    session = SessionLocal()
    foto = session.query(Foto).filter_by(aviso_id=_id).first()
    session.close()
    return foto

def count(Tabla):
    session = SessionLocal()
    total = session.query(func.count(Tabla.id)).scalar()
    session.close()
    return total

def count_by_type():
    session = SessionLocal()
    total = session.query(Aviso.tipo, func.count(Aviso.id).label("cantidad")).group_by(Aviso.tipo).all()
    session.close()
    return total

def count_by_day():
    session = SessionLocal()
    total = session.query(Aviso.fecha_ingreso, func.count(Aviso.id).label("cantidad")).group_by(Aviso.fecha_ingreso).order_by("cantidad").all()
    session.close()
    return total

def count_by_month():
    session = SessionLocal()
    total = session.query(func.date_format(Aviso.fecha_ingreso, '%Y-%m').label("mes"), Aviso.tipo, func.count(Aviso.id).label("cantidad")).group_by("mes", Aviso.tipo).order_by("mes").all()
    session.close()
    return total


def get_aviso_id(_id, Tabla):
    session = SessionLocal()
    info = session.query(Tabla).filter_by(aviso_id=_id).all()
    session.close()
    return info

def create_comm(nombre, texto, aviso_id):
    session = SessionLocal()
    new_comm = Comentario(nombre = nombre, texto = texto, aviso_id = aviso_id)
    session.add(new_comm)
    session.commit()
    session.refresh(new_comm)
    aviso_id = new_comm.id
    session.close()
