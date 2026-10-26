export interface Tarifa {
  id: number;
  idGrado: number;
  grado: string;
  idCiclo: number;
  anio: number;
  montoInscripcion: number;
  montoMensualidad: number;
  diaVencimiento: number;
}

export interface DatosTarifa {
  idGrado: number;
  idCiclo: number;
  montoInscripcion: number;
  montoMensualidad: number;
  diaVencimiento: number;
}