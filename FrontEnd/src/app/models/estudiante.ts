export interface FichaMedica {
  alergias?: string;
  condiciones?: string;
  contactoEmergencia: string;
  telefonoEmergencia: string;
}

export interface Encargado {
  id: number;
  nombres: string;
  apellidos: string;
  dpi: string;
  telefono: string;
  correo?: string;
  direccion?: string;
  parentesco: string;
}

export interface Estudiante {
  id: number;
  carnet: string;
  nombres: string;
  apellidos: string;
  dpi?: string;
  fechaNacimiento: string;
  genero: string;
  direccion?: string;
  estado: string;
  ficha?: FichaMedica;
  encargado?: Encargado;
}

export interface DatosEncargado {
  nombres: string;
  apellidos: string;
  dpi: string;
  telefono: string;
  correo: string | null;
  direccion: string | null;
  parentesco: string;
}

export interface DatosEstudiante {
  nombres: string;
  apellidos: string;
  dpi: string | null;
  fechaNacimiento: string;
  genero: string;
  direccion: string | null;
  alergias: string | null;
  condiciones: string | null;
  contactoEmergencia: string;
  telefonoEmergencia: string;
  encargado: DatosEncargado;
}