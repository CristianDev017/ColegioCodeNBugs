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