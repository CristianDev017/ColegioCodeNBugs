export interface ConfigBiblioteca {
  diasPrestamo: number;
  multaPorDia: number;
}

export interface Prestamo {
  id: number;
  idLibro: number;
  titulo: string;
  tipoSolicitante: string;
  solicitante: string;
  fechaPrestamo: string;
  fechaEsperada: string;
  fechaDevolucion?: string;
  estado: string;
  atrasado: boolean;
  diasAtraso: number;
  observacion?: string;
}

export interface OpcionBiblioteca {
  id: number;
  texto: string;
}

export interface DatosPrestamo {
  idLibro: number;
  tipoSolicitante: string;
  idSolicitante: number;
}

export interface Multa {
  id: number;
  idPrestamo: number;
  titulo: string;
  solicitante: string;
  diasAtraso: number;
  monto: number;
  estado: string;
  fechaGeneracion: string;
}

export interface ResultadoDevolucion {
  prestamo: Prestamo;
  multa?: Multa;
}
