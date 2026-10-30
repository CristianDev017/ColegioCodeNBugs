import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { BibliotecaService } from '../../../core/biblioteca.service';
import { ConfigBiblioteca, Prestamo } from '../../../models/biblioteca';

@Component({
  selector: 'app-lista-devoluciones',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-devoluciones.html'
})
export class ListaDevoluciones implements OnInit, OnDestroy {
  prestamos: Prestamo[] = [];
  config?: ConfigBiblioteca;
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';
  prestamoADevolver?: Prestamo;

  observacion = new FormControl('', [Validators.maxLength(500)]);

  filtros = new FormGroup({
    buscar: new FormControl(''),
    estado: new FormControl('ACTIVO')
  });

  private suscripcion?: Subscription;

  constructor(private servicio: BibliotecaService) {}

  ngOnInit(): void {
    this.servicio.config().subscribe({ next: (c) => (this.config = c) });
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

  get multaEstimada(): number {
    if (!this.prestamoADevolver || !this.config) {
      return 0;
    }
    return this.prestamoADevolver.diasAtraso * this.config.multaPorDia;
  }

  cargar(): void {
    const f = this.filtros.value;
    this.cargando = true;
    this.servicio.prestamos((f.buscar ?? '').trim(), f.estado ?? 'ACTIVO', this.pagina, this.tamano).subscribe({
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
    this.filtros.reset({ buscar: '', estado: 'ACTIVO' });
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

  pedirConfirmacion(prestamo: Prestamo): void {
    this.prestamoADevolver = prestamo;
    this.observacion.reset('');
  }

  cancelar(): void {
    this.prestamoADevolver = undefined;
  }

  confirmar(): void {
    if (this.observacion.invalid) {
      this.observacion.markAsTouched();
      return;
    }
    const prestamo = this.prestamoADevolver!;
    this.mensajeExito = '';
    this.mensajeError = '';
    this.servicio.devolver(prestamo.id, (this.observacion.value ?? '').trim()).subscribe({
      next: (resultado) => {
        this.prestamoADevolver = undefined;
        this.mensajeExito = 'Devolución registrada correctamente';
        if (resultado.multa) {
          this.mensajeExito += `. Se generó una multa de Q${resultado.multa.monto.toFixed(2)} por ${resultado.multa.diasAtraso} días de atraso`;
        }
        this.cargar();
      },
      error: (e) => {
        this.prestamoADevolver = undefined;
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo registrar la devolución';
      }
    });
  }
}