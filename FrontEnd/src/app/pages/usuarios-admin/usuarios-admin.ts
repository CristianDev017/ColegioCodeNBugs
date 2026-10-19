import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { UsuarioAdminService } from '../../core/usuario-admin.service';
import { UsuarioAdmin } from '../../models/usuario-admin';

@Component({
  selector: 'app-usuarios-admin',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './usuarios-admin.html',
  styleUrl: './usuarios-admin.scss'
})
export class UsuariosAdmin implements OnInit {

  usuarios: UsuarioAdmin[] = [];
  total = 0;
  pagina = 1;
  tamano = 10;
  buscarControl = new FormControl('');

  mensaje = '';
  mensajeEsError = false;

  mostrarFormulario = false;
  idEditando: number | null = null;
  formulario: FormGroup;
  guardando = false;
  errorFormulario = '';

  usuarioAConfirmar: UsuarioAdmin | null = null;

  constructor(private fb: FormBuilder, private servicio: UsuarioAdminService) {
    this.formulario = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      correo: ['', [Validators.required, Validators.email]],
      password: [''],
      rol: ['ADMIN', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  get desde(): number {
    return this.total === 0 ? 0 : (this.pagina - 1) * this.tamano + 1;
  }

  get hasta(): number {
    return Math.min(this.pagina * this.tamano, this.total);
  }

  get paginas(): number[] {
    const inicio = Math.max(1, this.pagina - 2);
    const fin = Math.min(this.totalPaginas, inicio + 4);
    const lista: number[] = [];
    for (let i = Math.max(1, fin - 4); i <= fin; i++) {
      lista.push(i);
    }
    return lista;
  }

  cargar(): void {
    this.servicio.listar(this.pagina, this.tamano, this.buscarControl.value ?? '').subscribe({
      next: (respuesta) => {
        this.usuarios = respuesta.datos;
        this.total = respuesta.total;
      },
      error: (e) => this.mostrar(e.error?.mensaje ?? 'No se pudo cargar la lista', true)
    });
  }

  buscar(): void {
    this.pagina = 1;
    this.cargar();
  }

  limpiarBusqueda(): void {
    this.buscarControl.setValue('');
    this.buscar();
  }

  cambiarPagina(numero: number): void {
    if (numero < 1 || numero > this.totalPaginas || numero === this.pagina) {
      return;
    }
    this.pagina = numero;
    this.cargar();
  }

  cambiarTamano(valor: string): void {
    this.tamano = Number(valor);
    this.pagina = 1;
    this.cargar();
  }

  nuevo(): void {
    this.idEditando = null;
    this.formulario.reset({ username: '', correo: '', password: '', rol: 'ADMIN' });
    this.formulario.controls['password'].setValidators([Validators.required, Validators.minLength(6)]);
    this.formulario.controls['password'].updateValueAndValidity();
    this.errorFormulario = '';
    this.mostrarFormulario = true;
  }

  editar(usuario: UsuarioAdmin): void {
    this.idEditando = usuario.id;
    this.formulario.reset({
      username: usuario.username,
      correo: usuario.correo,
      password: '',
      rol: usuario.rol
    });
    this.formulario.controls['password'].setValidators([Validators.minLength(6)]);
    this.formulario.controls['password'].updateValueAndValidity();
    this.errorFormulario = '';
    this.mostrarFormulario = true;
  }

  cancelarFormulario(): void {
    this.mostrarFormulario = false;
  }

  invalido(campo: string): boolean {
    const control = this.formulario.controls[campo];
    return control.touched && control.invalid;
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.guardando = true;
    this.errorFormulario = '';

    const datos = this.formulario.value;
    const peticion = this.idEditando === null
      ? this.servicio.crear(datos)
      : this.servicio.editar(this.idEditando, datos);

    peticion.subscribe({
      next: () => {
        const eraNuevo = this.idEditando === null;
        this.formulario.reset({ username: '', correo: '', password: '', rol: 'ADMIN' });
        this.mostrarFormulario = false;
        this.guardando = false;
        this.mostrar(eraNuevo ? 'Usuario creado correctamente' : 'Usuario actualizado correctamente', false);
        this.cargar();
      },
      error: (e) => {
        this.errorFormulario = e.error?.mensaje ?? 'No se pudo guardar el usuario';
        this.guardando = false;
      }
    });
  }

  pedirConfirmacion(usuario: UsuarioAdmin): void {
    this.usuarioAConfirmar = usuario;
  }

  cancelarConfirmacion(): void {
    this.usuarioAConfirmar = null;
  }

  confirmar(): void {
    const usuario = this.usuarioAConfirmar;
    if (usuario === null) {
      return;
    }
    const peticion = usuario.estado === 'ACTIVO'
      ? this.servicio.desactivar(usuario.id)
      : this.servicio.activar(usuario.id);

    peticion.subscribe({
      next: () => {
        this.usuarioAConfirmar = null;
        this.mostrar(usuario.estado === 'ACTIVO' ? 'Usuario desactivado' : 'Usuario activado', false);
        this.cargar();
      },
      error: (e) => {
        this.usuarioAConfirmar = null;
        this.mostrar(e.error?.mensaje ?? 'No se pudo completar la operación', true);
      }
    });
  }

  private mostrar(texto: string, esError: boolean): void {
    this.mensaje = texto;
    this.mensajeEsError = esError;
  }
}
