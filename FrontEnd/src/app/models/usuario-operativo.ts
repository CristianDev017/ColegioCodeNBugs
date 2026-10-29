export const ROLES_OPERATIVOS = [
  { valor: 'SECRETARIA', nombre: 'Secretaria' },
  { valor: 'MAESTRO', nombre: 'Maestro' },
  { valor: 'BIBLIOTECARIO', nombre: 'Bibliotecario' },
  { valor: 'MANTENIMIENTO', nombre: 'Mantenimiento' }
];

export interface UsuarioOperativo {
  id: number;
  idRol: number;
  rol: string;
  username: string;
  correo: string;
  estado: 'ACTIVO' | 'INACTIVO';
  fechaCreacion: string;
  ultimoAcceso?: string;
  idEmpleado?: number;
  empleado?: string;
}

export interface OpcionEmpleado {
  id: number;
  nombre: string;
}

export interface DatosUsuarioOperativo {
  username: string;
  correo: string;
  password: string;
  rol: string;
  idEmpleado: number;
}