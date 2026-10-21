export interface Grado {
  id: number;
  idNivel: number;
  nivel: string;
  idCarrera: number | null;
  carrera: string | null;
  nombre: string;
  cupoDefault: number;
  activo: boolean;
}

export interface DatosGrado {
  idNivel: number;
  idCarrera: number | null;
  nombre: string;
  cupoDefault: number;
}