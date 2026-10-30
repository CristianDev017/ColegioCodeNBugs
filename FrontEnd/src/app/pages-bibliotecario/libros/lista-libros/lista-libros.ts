import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { LibroService } from '../../../core/libro.service';
import { Libro } from '../../../models/libro';

@Component({
  selector: 'app-lista-libros',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-libros.html'
})
export class ListaLibros implements OnInit, OnDestroy {
  libros: Libro[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';
  libroAConfirmar?: Libro;

  filtros = new FormGroup({
    buscar: new FormControl(''),
    estado: new FormControl('')
  });

  private suscripcion?: Subscription;

  constructor(private servicio: LibroService) {}

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

  cargar(): void {
    const f = this.filtros.value;
    this.cargando = true;
    this.servicio.listar((f.buscar ?? '').trim(), f.estado ?? '', this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.libros = respuesta.datos;
        this.total = respuesta.total;
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo cargar el catálogo';
        this.cargando = false;
      }
    });
  }

  limpiarFiltros(): void {
    this.filtros.reset({ buscar: '', estado: '' });
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

  pedirConfirmacion(libro: Libro): void {
    this.libroAConfirmar = libro;
  }

  cancelar(): void {
    this.libroAConfirmar = undefined;
  }

  confirmar(): void {
    const libro = this.libroAConfirmar!;
    const activar = !libro.activo;
    const peticion = activar ? this.servicio.activar(libro.id) : this.servicio.desactivar(libro.id);
    this.mensajeExito = '';
    this.mensajeError = '';
    peticion.subscribe({
      next: () => {
        this.libroAConfirmar = undefined;
        this.mensajeExito = activar ? 'Libro activado correctamente' : 'Libro desactivado correctamente';
        this.cargar();
      },
      error: (e) => {
        this.libroAConfirmar = undefined;
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo cambiar el estado del libro';
      }
    });
  }
}