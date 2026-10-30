import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { ReportesService } from '../../core/reportes.service';
import { FilaSolvencia, OpcionReporte } from '../../models/reportes';

@Component({
  selector: 'app-lista-solventes',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-solventes.html'
})
export class ListaSolventes implements OnInit, OnDestroy {
  filas: FilaSolvencia[] = [];
  ciclos: OpcionReporte[] = [];
  grados: OpcionReporte[] = [];
  secciones: OpcionReporte[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeError: string = '';

  filtros = new FormGroup({
    idCiclo: new FormControl<number>(0),
    idGrado: new FormControl<number>(0),
    idSeccion: new FormControl<number>(0),
    estado: new FormControl(''),
    desde: new FormControl(''),
    hasta: new FormControl('')
  });

  private suscripcion?: Subscription;

  constructor(private servicio: ReportesService) {}

  ngOnInit(): void {
    this.servicio.grados().subscribe({ next: (lista) => (this.grados = lista) });
    this.servicio.ciclos().subscribe({
      next: (lista) => {
        this.ciclos = lista;
        if (lista.length > 0) {
          this.filtros.patchValue({ idCiclo: lista[0].id }, { emitEvent: false });
        }
        this.cargar();
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudieron cargar los años lectivos';
      }
    });

    this.filtros.controls.idGrado.valueChanges.subscribe((idGrado) => {
      this.filtros.patchValue({ idSeccion: 0 }, { emitEvent: false });
      this.secciones = [];
      if (idGrado) {
        this.servicio.secciones(idGrado).subscribe({ next: (lista) => (this.secciones = lista) });
      }
    });

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

  armarFiltros(): any {
    const f = this.filtros.value;
    return {
      idCiclo: f.idCiclo ?? 0,
      idGrado: f.idGrado ?? 0,
      idSeccion: f.idSeccion ?? 0,
      estado: f.estado ?? '',
      desde: f.desde ?? '',
      hasta: f.hasta ?? ''
    };
  }

  cargar(): void {
    this.cargando = true;
    this.mensajeError = '';
    this.servicio.solventes(this.armarFiltros(), this.pagina, this.tamano).subscribe({
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
    this.mensajeError = '';
    this.servicio.pdfSolventes(this.armarFiltros()).subscribe({
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
    const primero = this.ciclos.length > 0 ? this.ciclos[0].id : 0;
    this.filtros.reset({ idCiclo: primero, idGrado: 0, idSeccion: 0, estado: '', desde: '', hasta: '' });
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