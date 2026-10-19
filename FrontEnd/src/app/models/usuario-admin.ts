export class UsuarioAdmin {
  id = 0;
  username = '';
  correo = '';
  rol = '';
  estado = '';
  fechaCreacion = '';
  ultimoAcceso: string | null = null;
}

export class DatosUsuarioAdmin {
  username = '';
  correo = '';
  password = '';
  rol = 'ADMIN';
}