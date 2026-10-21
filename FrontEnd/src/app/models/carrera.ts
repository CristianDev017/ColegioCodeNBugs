export interface Carrera {
  id: number;
  nombre: string;
  descripcion: string | null;
  activo: boolean;
}

export interface DatosCarrera {
  nombre: string;
  descripcion: string | null;
}