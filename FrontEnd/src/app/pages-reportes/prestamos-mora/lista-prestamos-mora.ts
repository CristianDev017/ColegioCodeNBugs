import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { ReportesService } from '../../core/reportes.service';
import { FilaMora } from '../../models/reportes';

@Component({
  selector: 'app-lista-prestamos-mora',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-prestamos-mora.html'
})
export class ListaPrestamosMora implements OnInit, OnDestroy {
  filas: FilaMora[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeError: string = '';

  filtros = new FormGroup({
    buscar: new FormControl(''),
    tipo: new FormControl('')
  });

  private suscripcion?: Subscription;

  constructor(private servicio: ReportesService) {}

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
    this.servicio.prestamosMora((f.buscar ?? '').trim(), f.tipo ?? '', this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.filas = respuesta.datos;
        this.total = respuesta.total;
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudo cargar el reporte';
        this.cargando = false;
      }
    });
  }

  descargarPdf(): void {
    const f = this.filtros.value;
    this.mensajeError = '';
    this.servicio.pdfPrestamosMora((f.buscar ?? '').trim(), f.tipo ?? '').subscribe({
      next: (archivo) => {
        const url = URL.createObjectURL(archivo);
        window.open(url, '_blank');
      },
      error: () => {
        this.mensajeError = 'No se pudo generar el PDF';
      }
    });
  }

  limpiarFiltros(): void {
    this.filtros.reset({ buscar: '', tipo: '' });
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