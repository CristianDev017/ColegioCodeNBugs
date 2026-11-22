
export interface Carrera {
  idCarrera: number;
  nombre: string;
  descripcion: string | null;
  activo: boolean;
}

export interface PeticionCarrera {
  nombre: string;
  descripcion: string;
}
