export const MESES = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'
];

export interface Pago {
  id: number;
  idEstudiante: number;
  carnet: string;
  estudiante: string;
  idCiclo: number;
  anio: number;
  idActividad?: number;
  actividad?: string;
  concepto: string;
  mes?: number;
  monto: number;
  fechaPago: string;
  usuario: string;
  numeroComprobante: string;
  fechaEmision: string;
}

export interface EstadoCuenta {
  idEstudiante: number;
  carnet: string;
  estudiante: string;
  idCiclo: number;
  anio: number;
  grado: string;
  montoInscripcion?: number;
  montoMensualidad?: number;
  diaVencimiento: number;
  inscripcionPagada: boolean;
  mesesPagados: number[];
}

export interface DatosPago {
  idEstudiante: number;
  idCiclo: number;
  concepto: string;
  mes: number | null;
  idActividad: number | null;
  monto: number;
  fechaPago: string;
}