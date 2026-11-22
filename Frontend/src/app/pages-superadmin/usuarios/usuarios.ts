
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Usuarios } from '../../services/usuarios';
import { Usuario } from '../../models/usuarios';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './usuarios.html'
})
export class UsuariosPagina implements OnInit {

  usuarios: Usuario[] = [];

  username = '';
  correo = '';
  password = '';
  idRol = 0;
  idEditar = 0;

  mensaje = '';
  error = '';
  guardando = false;

  roles = [
    { id: 1, nombre: 'SUPER_ADMIN' },
    { id: 2, nombre: 'ADMIN' },
    { id: 3, nombre: 'SECRETARIA' },
    { id: 4, nombre: 'MAESTRO' },
    { id: 5, nombre: 'BIBLIOTECARIO' },
    { id: 6, nombre: 'ESTUDIANTE' },
    { id: 7, nombre: 'MANTENIMIENTO' }
  ];

  constructor(
    private usuariosService: Usuarios,
    private detector: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.listar();
  }

  listar() {
    this.usuariosService.listar().subscribe({
      next: (datos) => {
        this.usuarios = datos;
        this.detector.detectChanges();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudieron cargar los usuarios';
        this.detector.detectChanges();
      }
    });
  }

  guardar() {
    if (this.guardando) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    const username = this.username.trim();
    const correo = this.correo.trim();

    if (!username || !correo || this.idRol === 0) {
      this.error = 'Completa los campos obligatorios';
      this.detector.detectChanges();
      return;
    }

    if (this.idEditar === 0 && !this.password) {
      this.error = 'La contraseña es obligatoria';
      this.detector.detectChanges();
      return;
    }

    this.guardando = true;
    this.detector.detectChanges();

    if (this.idEditar === 0) {
      const datos = {
        username: username,
        correo: correo,
        password: this.password,
        idRol: this.idRol
      };

      this.usuariosService.crear(datos).subscribe({
        next: () => {
          this.limpiar();
          this.mensaje = 'Usuario creado correctamente';
          this.guardando = false;
          this.detector.detectChanges();
          this.listar();
        },
        error: (e) => {
          this.error = e.error?.mensaje || 'No se pudo crear el usuario';
          this.guardando = false;
          this.detector.detectChanges();
        }
      });
    } else {
      const datos = {
        username: username,
        correo: correo,
        idRol: this.idRol
      };

      this.usuariosService.editar(this.idEditar, datos).subscribe({
        next: () => {
          this.limpiar();
          this.mensaje = 'Usuario actualizado correctamente';
          this.guardando = false;
          this.detector.detectChanges();
          this.listar();
        },
        error: (e) => {
          this.error = e.error?.mensaje || 'No se pudo actualizar el usuario';
          this.guardando = false;
          this.detector.detectChanges();
        }
      });
    }
  }

  seleccionar(usuario: Usuario) {
    this.idEditar = usuario.id;
    this.username = usuario.username;
    this.correo = usuario.correo;
    this.password = '';
    this.idRol = usuario.idRol;
    this.mensaje = '';
    this.error = '';

    this.detector.detectChanges();
  }

  cambiarEstado(usuario: Usuario) {
    const nuevoEstado = usuario.estado === 'ACTIVO' ? 'INACTIVO' : 'ACTIVO';
    const accion = nuevoEstado === 'ACTIVO' ? 'activar' : 'desactivar';

    if (!confirm('¿Quieres ' + accion + ' al usuario ' + usuario.username + '?')) {
      return;
    }

    this.mensaje = '';
    this.error = '';

    this.usuariosService.cambiarEstado(usuario.id, nuevoEstado).subscribe({
      next: () => {
        this.mensaje = 'Estado actualizado correctamente';
        this.detector.detectChanges();
        this.listar();
      },
      error: (e) => {
        this.error = e.error?.mensaje || 'No se pudo cambiar el estado';
        this.detector.detectChanges();
      }
    });
  }

  limpiar() {
    this.idEditar = 0;
    this.username = '';
    this.correo = '';
    this.password = '';
    this.idRol = 0;
    this.detector.detectChanges();
  }
}
