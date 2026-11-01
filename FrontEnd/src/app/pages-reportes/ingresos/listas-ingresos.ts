
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { ReportesService } from '../../servicios/reportes.service';
import { IngresoConcepto } from '../../models/reportes';

@Component({
  selector: 'app-lista-ingresos',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-ingresos.html'
})
export class ListaIngresos implements OnInit, OnDestroy {

  filas: IngresoConcepto[] = [];

  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  totalIngresos: number = 0;

  cargando: boolean = false;
  mensajeError: string = '';

  filtros = new FormGroup({
    desde: new FormControl(''),
    hasta: new FormControl('')
  });

  private suscripciones = new Subscription();

  constructor(private servicio: ReportesService) {}

  ngOnInit(): void {

    this.cargar();

    this.suscripciones.add(
      this.filtros.valueChanges
        .pipe(debounceTime(300))
        .subscribe(() => {
          this.pagina = 1;
          this.cargar();
        })
    );
  }

  ngOnDestroy(): void {
    this.suscripciones.unsubscribe();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {

    const f = this.filtros.value;

    const desde = f.desde ?? '';
    const hasta = f.hasta ?? '';

    if (desde && hasta && desde > hasta) {
      this.mensajeError = 'La fecha inicial no puede ser mayor que la final';
      this.filas = [];
      this.total = 0;
      this.totalIngresos = 0;
      return;
    }

    this.cargando = true;
    this.mensajeError = '';

    this.suscripciones.add(
      this.servicio.ingresos(
        desde,
        hasta,
        this.pagina,
        this.tamano
      ).subscribe({
        next: (respuesta) => {
          this.filas = respuesta.datos;
          this.total = respuesta.total;
          this.totalIngresos = respuesta.totalIngresos;
          this.cargando = false;
        },
        error: (e) => {
          this.mensajeError =
            e?.error?.mensaje ?? 'No se pudo cargar el reporte';
          this.cargando = false;
        }
      })
    );
  }

  descargarPdf(): void {

    const f = this.filtros.value;

    const desde = f.desde ?? '';
    const hasta = f.hasta ?? '';

    if (desde && hasta && desde > hasta) {
      this.mensajeError = 'La fecha inicial no puede ser mayor que la final';
      return;
    }

    this.mensajeError = '';

    this.suscripciones.add(
      this.servicio.pdfIngresos(
        desde,
        hasta
      ).subscribe({
        next: (archivo) => {
          const url = URL.createObjectURL(archivo);
          window.open(url, '_blank');
        },
        error: () => {
          this.mensajeError = 'No se pudo generar el PDF';
        }
      })
    );
  }

  limpiarFiltros(): void {

    this.filtros.reset({
      desde: '',
      hasta: ''
    });

    this.pagina = 1;
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
