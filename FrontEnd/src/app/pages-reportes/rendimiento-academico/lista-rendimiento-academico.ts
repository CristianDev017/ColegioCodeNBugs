
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, debounceTime } from 'rxjs';
import { ReportesService } from '../../servicios/reportes.service';
import { OpcionReporte, RendimientoAcademico } from '../../models/reportes';

@Component({
  selector: 'app-lista-rendimiento-academico',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-rendimiento-academico.html'
})
export class ListaRendimientoAcademico implements OnInit, OnDestroy {

  filas: RendimientoAcademico[] = [];
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
    idSeccion: new FormControl<number>(0)
  });

  private suscripciones = new Subscription();

  constructor(private servicio: ReportesService) {}

  ngOnInit(): void {

    this.suscripciones.add(
      this.servicio.grados().subscribe({
        next: (lista) => {
          this.grados = lista;
        },
        error: () => {
          this.mensajeError = 'No se pudieron cargar los grados';
        }
      })
    );

    this.suscripciones.add(
      this.servicio.ciclos().subscribe({
        next: (lista) => {
          this.ciclos = lista;

          if (lista.length > 0) {
            this.filtros.patchValue(
              { idCiclo: lista[0].id },
              { emitEvent: false }
            );
          }

          this.cargar();
        },
        error: () => {
          this.mensajeError = 'No se pudieron cargar los años lectivos';
        }
      })
    );

    this.suscripciones.add(
      this.filtros.controls.idGrado.valueChanges.subscribe((idGrado) => {

        this.filtros.patchValue(
          { idSeccion: 0 },
          { emitEvent: false }
        );

        this.secciones = [];

        if (idGrado && idGrado > 0) {
          this.suscripciones.add(
            this.servicio.secciones(idGrado).subscribe({
              next: (lista) => {
                this.secciones = lista;
              },
              error: () => {
                this.mensajeError = 'No se pudieron cargar las secciones';
              }
            })
          );
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

    const idCiclo = f.idCiclo ?? 0;
    const idGrado = f.idGrado ?? 0;
    const idSeccion = f.idSeccion ?? 0;

    if (idCiclo <= 0) {
      this.filas = [];
      this.total = 0;
      return;
    }

    this.cargando = true;
    this.mensajeError = '';

    this.suscripciones.add(
      this.servicio.rendimientoAcademico(
        idCiclo,
        idGrado,
        idSeccion,
        this.pagina,
        this.tamano
      ).subscribe({
        next: (respuesta) => {
          this.filas = respuesta.datos;
          this.total = respuesta.total;
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

    const idCiclo = f.idCiclo ?? 0;
    const idGrado = f.idGrado ?? 0;
    const idSeccion = f.idSeccion ?? 0;

    if (idCiclo <= 0) {
      this.mensajeError = 'Debes seleccionar un año lectivo';
      return;
    }

    this.mensajeError = '';

    this.suscripciones.add(
      this.servicio.pdfRendimientoAcademico(
        idCiclo,
        idGrado,
        idSeccion
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

    const primero = this.ciclos.length > 0
      ? this.ciclos[0].id
      : 0;

    this.filtros.reset({
      idCiclo: primero,
      idGrado: 0,
      idSeccion: 0
    });
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
