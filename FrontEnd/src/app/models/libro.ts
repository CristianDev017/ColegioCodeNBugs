export interface Libro {
  id: number;
  titulo: string;
  autor: string;
  editorial: string;
  cantidadTotal: number;
  cantidadDisponible: number;
  activo: boolean;
}

export interface DatosLibro {
  titulo: string;
  autor: string;
  editorial: string;
  cantidadTotal: number;
}