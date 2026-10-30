import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { BibliotecaService } from '../../../core/biblioteca.service';
import { Multa } from '../../../models/biblioteca';

@Component({
  selector: 'app-lista-multas',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-multas.html'
})
export class ListaMultas implements OnInit, OnDestroy {
  multas: Multa[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';
  multaAConfirmar?: Multa;

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
    this.servicio.multas((f.buscar ?? '').trim(), f.estado ?? '', this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.multas = respuesta.datos;
        this.total = respuesta.total;
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudieron cargar las multas';
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

  pedirConfirmacion(multa: Multa): void {
    this.multaAConfirmar = multa;
  }

  cancelar(): void {
    this.multaAConfirmar = undefined;
  }

  confirmar(): void {
    const multa = this.multaAConfirmar!;
    this.mensajeExito = '';
    this.mensajeError = '';
    this.servicio.pagarMulta(multa.id).subscribe({
      next: () => {
        this.multaAConfirmar = undefined;
        this.mensajeExito = 'Multa marcada como pagada';
        this.cargar();
      },
      error: (e) => {
        this.multaAConfirmar = undefined;
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo marcar la multa como pagada';
      }
    });
  }
}