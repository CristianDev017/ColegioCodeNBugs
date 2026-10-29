export interface CursoMaestro {
  idAsignacion: number;
  curso: string;
  seccion: string;
  idCiclo: number;
  anio: number;
  estudiantes: number;
}

export interface AlumnoNomina {
  idInscripcion: number;
  carnet: string;
  estudiante: string;
  estado?: string;
}

export interface RegistroAsistencia {
  idInscripcion: number;
  estado: string;
}

export interface Ponderacion {
  id: number;
  nombre: string;
  porcentaje: number;
  notaMaxima: number;
}

export interface DatosPonderacion {
  nombre: string;
  porcentaje: number;
  notaMaxima: number;
}

export interface PaginaPonderaciones {
  datos: Ponderacion[];
  total: number;
  pagina: number;
  tamano: number;
  suma: number;
}