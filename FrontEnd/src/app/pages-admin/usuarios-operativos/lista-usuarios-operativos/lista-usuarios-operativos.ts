import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { UsuarioOperativoService } from '../../../core/usuario-operativo.service';
import { ROLES_OPERATIVOS, UsuarioOperativo } from '../../../models/usuario-operativo';

@Component({
  selector: 'app-lista-usuarios-operativos',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-usuarios-operativos.html'
})
export class ListaUsuariosOperativos implements OnInit, OnDestroy {
  roles = ROLES_OPERATIVOS;
  usuarios: UsuarioOperativo[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';
  usuarioAConfirmar?: UsuarioOperativo;

  filtros = new FormGroup({
    buscar: new FormControl(''),
    rol: new FormControl(''),
    estado: new FormControl('')
  });

  private suscripcion?: Subscription;

  constructor(private servicio: UsuarioOperativoService) {}

  ngOnInit(): void {
    this.cargar();
    this.suscripcion = this.filtros.valueChanges.pipe(debounceTime(300)).subscribe(() => {
      this.pagina = 1;
      this.cargar();
    });
  }

  ngOnDestroy(): void {
    this.suscripcion?.unsubscribe();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  nombreRol(valor: string): string {
    return this.roles.find((r) => r.valor === valor)?.nombre ?? valor;
  }

  cargar(): void {
    const f = this.filtros.value;
    this.cargando = true;
    this.servicio.listar((f.buscar ?? '').trim(), f.rol ?? '', f.estado ?? '', this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.usuarios = respuesta.datos;
        this.total = respuesta.total;
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo cargar el listado de usuarios';
        this.cargando = false;
      }
    });
  }

  limpiarFiltros(): void {
    this.filtros.reset({ buscar: '', rol: '', estado: '' });
  }

  anterior(): void {
    if (this.pagina > 1) {
      this.pagina--;
      this.cargar();
    }
  }

  siguiente(): void {
    if (this.pagina < this.totalPaginas) {
      this.pagina++;
      this.cargar();
    }
  }

  pedirConfirmacion(usuario: UsuarioOperativo): void {
    this.usuarioAConfirmar = usuario;
  }

  cancelar(): void {
    this.usuarioAConfirmar = undefined;
  }

  confirmar(): void {
    const usuario = this.usuarioAConfirmar!;
    const activar = usuario.estado === 'INACTIVO';
    const peticion = activar ? this.servicio.activar(usuario.id) : this.servicio.desactivar(usuario.id);
    this.mensajeExito = '';
    this.mensajeError = '';
    peticion.subscribe({
      next: () => {
        this.usuarioAConfirmar = undefined;
        this.mensajeExito = activar ? 'Usuario activado correctamente' : 'Usuario desactivado correctamente';
        this.cargar();
      },
      error: (e) => {
        this.usuarioAConfirmar = undefined;
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo cambiar el estado del usuario';
      }
    });
  }
}