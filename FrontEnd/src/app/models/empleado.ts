export const PUESTOS = ['Maestro', 'Secretaría', 'Biblioteca', 'Limpieza', 'Mantenimiento'];

export interface Empleado {
  id: number;
  nombres: string;
  apellidos: string;
  dpi: string;
  fechaNacimiento: string;
  telefono?: string;
  direccion?: string;
  puesto: string;
  fechaContratacion: string;
  salario: number;
  estado: 'ACTIVO' | 'INACTIVO';
}

export interface DatosEmpleado {
  nombres: string;
  apellidos: string;
  dpi: string;
  fechaNacimiento: string;
  telefono: string | null;
  direccion: string | null;
  puesto: string;
  fechaContratacion: string;
  salario: number;
}