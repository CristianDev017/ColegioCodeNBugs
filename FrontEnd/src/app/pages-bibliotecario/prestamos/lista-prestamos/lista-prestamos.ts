import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { BibliotecaService } from '../../../core/biblioteca.service';
import { Prestamo } from '../../../models/biblioteca';

@Component({
  selector: 'app-lista-prestamos',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-prestamos.html'
})
export class ListaPrestamos implements OnInit, OnDestroy {
  prestamos: Prestamo[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeError: string = '';

  filtros = new FormGroup({
    buscar: new FormControl(''),
    estado: new FormControl('')
  });

  private suscripcion?: Subscription;

  constructor(private servicio: BibliotecaService) {}

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
    this.servicio.prestamos((f.buscar ?? '').trim(), f.estado ?? '', this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.prestamos = respuesta.datos;
        this.total = respuesta.total;
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudieron cargar los préstamos';
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
}
