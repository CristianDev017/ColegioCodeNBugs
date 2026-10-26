export interface Inscripcion {
  id: number;
  idEstudiante: number;
  carnet: string;
  estudiante: string;
  idSeccion: number;
  grado: string;
  seccion: string;
  anio: number;
  fechaInscripcion: string;
  estado: string;
}

export interface SeccionDisponible {
  idSeccion: number;
  grado: string;
  seccion: string;
  cupo: number;
  inscritos: number;
  disponibles: number;
}