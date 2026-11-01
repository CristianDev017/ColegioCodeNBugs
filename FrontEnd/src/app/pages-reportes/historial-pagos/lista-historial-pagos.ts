
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { ReportesService } from '../../core/reportes.service';
import { HistorialPago, OpcionReporte } from '../../models/reportes';

@Component({
  selector: 'app-lista-historial-pagos',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-historial-pagos.html'
})
export class ListaHistorialPagos implements OnInit, OnDestroy {

  filas: HistorialPago[] = [];
  estudiantes: OpcionReporte[] = [];

  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;

  totalPagado: number = 0;
  saldoPendiente: number = 0;

  cargando: boolean = false;
  mensajeError: string = '';

  filtros = new FormGroup({
    idEstudiante: new FormControl<number>(0),
    desde: new FormControl(''),
    hasta: new FormControl('')
  });

  private suscripciones = new Subscription();

  constructor(private servicio: ReportesService) {}

  ngOnInit(): void {

    this.suscripciones.add(
      this.servicio.estudiantes().subscribe({
        next: (lista) => {
          this.estudiantes = lista;
        },
        error: () => {
          this.mensajeError = 'No se pudieron cargar los estudiantes';
        }
      })
    );

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

    const idEstudiante = f.idEstudiante ?? 0;
    const desde = f.desde ?? '';
    const hasta = f.hasta ?? '';

    if (idEstudiante <= 0) {
      this.filas = [];
      this.total = 0;
      this.totalPagado = 0;
      this.saldoPendiente = 0;
      this.mensajeError = '';
      return;
    }

    if (desde && hasta && desde > hasta) {
      this.mensajeError = 'La fecha inicial no puede ser mayor que la final';
      this.filas = [];
      this.total = 0;
      this.totalPagado = 0;
      this.saldoPendiente = 0;
      return;
    }

    this.cargando = true;
    this.mensajeError = '';

    this.suscripciones.add(
      this.servicio.historialPagos(
        idEstudiante,
        desde,
        hasta,
        this.pagina,
        this.tamano
      ).subscribe({
        next: (respuesta) => {
          this.filas = respuesta.datos;
          this.total = respuesta.total;
          this.totalPagado = respuesta.totalPagado;
          this.saldoPendiente = respuesta.saldoPendiente;
          this.cargando = false;
        },
        error: (e) => {
          this.mensajeError =
            e?.error?.mensaje ?? 'No se pudo cargar el historial de pagos';
          this.cargando = false;
        }
      })
    );
  }

  descargarPdf(): void {

    const f = this.filtros.value;

    const idEstudiante = f.idEstudiante ?? 0;
    const desde = f.desde ?? '';
    const hasta = f.hasta ?? '';

    if (idEstudiante <= 0) {
      this.mensajeError = 'Debes seleccionar un estudiante';
      return;
    }

    if (desde && hasta && desde > hasta) {
      this.mensajeError = 'La fecha inicial no puede ser mayor que la final';
      return;
    }

    this.mensajeError = '';

    this.suscripciones.add(
      this.servicio.pdfHistorialPagos(
        idEstudiante,
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
      idEstudiante: 0,
      desde: '',
      hasta: ''
    });

    this.filas = [];
    this.total = 0;
    this.totalPagado = 0;
    this.saldoPendiente = 0;
    this.pagina = 1;
    this.mensajeError = '';
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
