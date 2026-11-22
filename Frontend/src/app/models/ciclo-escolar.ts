export interface CicloEscolar {
  idCiclo: number;
  anio: number;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
  cantidadGrados: number;
}

export interface PeticionCiclo {
  anio: number;
  fechaInicio: string;
  fechaFin: string;
}
