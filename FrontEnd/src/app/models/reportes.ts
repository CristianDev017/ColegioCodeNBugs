
export interface FilaMora {
  libro: string;
  solicitante: string;
  tipoSolicitante: string;
  fechaPrestamo: string;
  fechaEsperada: string;
  diasAtraso: number;
  multaEstimada?: number;
}

export interface OpcionReporte {
  id: number;
  texto: string;
}

export interface FilaSolvencia {
  carnet: string;
  estudiante: string;
  grado: string;
  seccion: string;
  estado: string;
  detalle: string;
  montoAdeudado: number;
}

export interface RendimientoAcademico {
  curso: string;
  grado: string;
  seccion: string;
  alumnosEvaluados: number;
  promedio: number | null;
  rendimiento: string;
}

export interface HistorialPago {
  idPago: number;
  carnet: string;
  estudiante: string;
  anio: number;
  concepto: string;
  detalle: string;
  fechaPago: string;
  monto: number;
  comprobante: string | null;
}

export interface PaginaHistorialPago {
  datos: HistorialPago[];
  total: number;
  pagina: number;
  tamano: number;
  totalPagado: number;
  saldoPendiente: number;
}

export interface IngresoConcepto {
  concepto: string;
  cantidadPagos: number;
  total: number;
}

export interface PaginaIngresos {
  datos: IngresoConcepto[];
  total: number;
  pagina: number;
  tamano: number;
  totalIngresos: number;
}
