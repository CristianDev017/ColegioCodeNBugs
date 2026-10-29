export interface Solvencia {
  id: number;
  idEstudiante: number;
  carnet: string;
  estudiante: string;
  idCiclo: number;
  anio: number;
  grado?: string;
  fechaEmision: string;
  usuario: string;
}

export interface Pendiente {
  concepto: string;
  mes?: number;
  detalle: string;
  monto: number;
  fechaVencimiento?: string;
}

export interface VerificacionSolvencia {
  solvente: boolean;
  pendientes: Pendiente[];
}

export interface DatosSolvencia {
  idEstudiante: number;
  idCiclo: number;
}