export interface Actividad {
  id: number;
  nombre: string;
  costo: number;
  cupoTotal: number;
  inscritos: number;
  disponibles: number;
  activo: boolean;
}

export interface DatosActividad {
  nombre: string;
  costo: number;
  cupoTotal: number;
}