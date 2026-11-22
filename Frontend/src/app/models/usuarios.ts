
export interface Usuario {
  id: number;
  idRol: number;
  rol: string;
  username: string;
  correo: string;
  estado: string;
}

export interface PeticionUsuario {
  username: string;
  correo: string;
  password: string;
  idRol: number;
}

export interface PeticionEditarUsuario {
  username: string;
  correo: string;
  idRol: number;
}
