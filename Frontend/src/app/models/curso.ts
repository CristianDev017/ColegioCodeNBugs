
export interface Curso {
  idCurso: number;
  nombre: string;
  descripcion: string | null;
  activo: boolean;
}

export interface PeticionCurso {
  nombre: string;
  descripcion: string;
}
