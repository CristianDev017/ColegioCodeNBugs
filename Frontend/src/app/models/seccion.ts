
export interface Seccion {
  idSeccion: number;
  idCicloGrado: number;
  idCiclo: number;
  anio: number;
  idGrado: number;
  grado: string;
  nivel: string;
  carrera: string | null;
  nombre: string;
  cupo: number;
  activo: boolean;
  estadoCiclo: string;
}

export interface PeticionSeccion {
  idCicloGrado: number;
  nombre: string;
  cupo: number;
}

export interface OpcionSeccion {
  idCicloGrado: number;
  idCiclo: number;
  anio: number;
  idGrado: number;
  grado: string;
  nivel: string;
  carrera: string | null;
  cupoDefault: number;
}
