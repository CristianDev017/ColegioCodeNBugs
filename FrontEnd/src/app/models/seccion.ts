export interface Seccion {
  id: number;
  idGrado: number;
  grado: string;
  nivel: string;
  nombre: string;
  cupo: number;
  activo: boolean;
}

export interface DatosSeccion {
  idGrado: number;
  nombre: string;
  cupo: number | null;
}