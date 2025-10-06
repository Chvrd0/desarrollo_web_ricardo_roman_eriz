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

class ContactarPor(Base):
    __tablename__ = "contactar_por"
    id = Column(Integer, primary_key=True)
    via = Column(String(30), nullable=False)
    aviso_id = Column(Integer, ForeignKey("aviso_adopcion.id"), nullable=False)

    aviso = relationship("Aviso", back_populates="contactos")


class Foto(Base):
    __tablename__ = "foto"
    id = Column(Integer, primary_key=True)
    ruta = Column(String(255), nullable=False)
    aviso_id = Column(Integer, ForeignKey("aviso_adopcion.id"), nullable=False)

    aviso = relationship("Aviso", back_populates="fotos")


# --- Database Functions ---
def get_user_by_id(id):
    session = SessionLocal()
    user = session.query(Usuario).filter_by(id=id).first()
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
    session.close()

def get_avisos(page_size):
    session = SessionLocal()
    confesiones = session.query(Aviso).limit(page_size).all()
    session.close()
    return confesiones



