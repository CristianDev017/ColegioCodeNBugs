export interface Asignacion {
  id: number;
  idEmpleado: number;
  maestro: string;
  idCurriculoCurso: number;
  curso: string;
  idSeccion: number;
  seccion: string;
  idCiclo: number;
  anio: number;
}

export interface OpcionAsignacion {
  id: number;
  nombre: string;
}

export interface CursoSeccion {
  idCurriculoCurso: number;
  curso: string;
  idAsignacion?: number;
  idEmpleado?: number;
  maestro?: string;
}

export interface DatosAsignacion {
  idEmpleado: number;
  idCurriculoCurso: number;
  idSeccion: number;
}