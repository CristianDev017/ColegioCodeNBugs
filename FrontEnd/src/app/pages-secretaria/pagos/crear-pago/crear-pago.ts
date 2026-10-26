import { CurrencyPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { debounceTime } from 'rxjs';
import { ActividadService } from '../../../core/actividad.service';
import { CicloEscolarService } from '../../../core/ciclo-escolar.service';
import { EstudianteService } from '../../../core/estudiante.service';
import { PagoService } from '../../../core/pago.service';
import { Actividad } from '../../../models/actividad';
import { CicloEscolar } from '../../../models/ciclo-escolar';
import { Estudiante } from '../../../models/estudiante';
import { DatosPago, EstadoCuenta, MESES, Pago } from '../../../models/pago';

@Component({
  selector: 'app-crear-pago',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './crear-pago.html'
})
export class CrearPago implements OnInit {
  ciclos: CicloEscolar[] = [];
  actividades: Actividad[] = [];
  resultados: Estudiante[] = [];
  seleccionado: Estudiante | null = null;
  estado: EstadoCuenta | null = null;

  pagina = 1;
  tamano = 8;
  total = 0;

  meses = MESES.map((nombre, i) => ({ numero: i + 1, nombre }));

  ultimoPago: Pago | null = null;
  mensajeExito = '';
  error = '';
  errorEstado = '';

  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private pagos: PagoService,
    private estudiantes: EstudianteService,
    private ciclosServicio: CicloEscolarService,
    private actividadesServicio: ActividadService
  ) {
    this.form = this.fb.group({
      buscar: [''],
      idCiclo: [null],
      concepto: ['INSCRIPCION'],
      mes: [null],
      idActividad: [null],
      monto: [null],
      fechaPago: [this.fechaHoy()]
    });

    this.form.get('buscar')!.valueChanges.pipe(debounceTime(300)).subscribe(() => {
      this.pagina = 1;
      this.cargarEstudiantes();
    });
    this.form.get('idCiclo')!.valueChanges.subscribe(() => this.cargarEstadoCuenta());
    this.form.get('idActividad')!.valueChanges.subscribe(() => this.actualizarMonto());
    this.form.get('concepto')!.valueChanges.subscribe(() => {
      this.form.patchValue({ mes: null, idActividad: null }, { emitEvent: false });
      this.actualizarMonto();
    });
  }

  ngOnInit(): void {
    this.ciclosServicio.listar(1, 50).subscribe({
      next: (r) => {
        this.ciclos = r.datos.filter((c) => c.estado.toUpperCase() !== 'CERRADO');
        const activo = this.ciclos.find((c) => c.estado.toUpperCase() === 'ACTIVO');
        if (activo) {
          this.form.patchValue({ idCiclo: activo.id });
        }
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudieron cargar los años lectivos')
    });
    this.actividadesServicio.listarActivas().subscribe({
      next: (lista) => (this.actividades = lista),
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudieron cargar las actividades')
    });
    this.cargarEstudiantes();
  }

  get puedeGuardar(): boolean {
    const v = this.form.value;
    if (!this.seleccionado || !this.estado || v.idCiclo === null) {
      return false;
    }
    if (!(Number(v.monto) > 0) || !v.fechaPago) {
      return false;
    }
    if (v.concepto === 'MENSUALIDAD' && v.mes === null) {
      return false;
    }
    if (v.concepto === 'ACTIVIDAD' && v.idActividad === null) {
      return false;
    }
    return true;
  }

  get fechaMaxima(): string {
    return this.fechaHoy();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargarEstudiantes(): void {
    const texto = String(this.form.value.buscar ?? '').trim();
    this.estudiantes.listar(texto, 'ACTIVO', this.pagina, this.tamano).subscribe({
      next: (r) => {
        this.resultados = r.datos;
        this.total = r.total;
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo cargar la lista de estudiantes')
    });
  }

  irA(p: number): void {
    if (p < 1 || p > this.totalPaginas) {
      return;
    }
    this.pagina = p;
    this.cargarEstudiantes();
  }

  elegir(e: Estudiante): void {
    this.seleccionado = e;
    this.cargarEstadoCuenta();
  }

  quitarSeleccion(): void {
    this.seleccionado = null;
    this.estado = null;
    this.errorEstado = '';
  }

  cargarEstadoCuenta(): void {
    this.estado = null;
    this.errorEstado = '';
    const idCiclo = this.form.value.idCiclo;
    if (!this.seleccionado || idCiclo === null) {
      return;
    }
    this.pagos.estadoCuenta(this.seleccionado.id, Number(idCiclo)).subscribe({
      next: (e) => {
        this.estado = e;
        if (e.inscripcionPagada && this.form.value.concepto === 'INSCRIPCION') {
          this.form.patchValue({ concepto: 'MENSUALIDAD', mes: null }, { emitEvent: false });
        }
        this.actualizarMonto();
      },
      error: (e) => (this.errorEstado = e?.error?.mensaje ?? 'No se pudo cargar el estado de cuenta')
    });
  }

  montoEsperado(): number | null {
    const v = this.form.value;
    if (v.concepto === 'INSCRIPCION') {
      return this.estado?.montoInscripcion ?? null;
    }
    if (v.concepto === 'MENSUALIDAD') {
      return this.estado?.montoMensualidad ?? null;
    }
    const actividad = this.actividades.find((a) => a.id === v.idActividad);
    return actividad ? actividad.costo : null;
  }

  actualizarMonto(): void {
    this.form.patchValue({ monto: this.montoEsperado() }, { emitEvent: false });
  }

  mesPagado(numero: number): boolean {
    return this.estado ? this.estado.mesesPagados.includes(numero) : false;
  }

  textoMesesPagados(): string {
    if (!this.estado || this.estado.mesesPagados.length === 0) {
      return 'ninguno';
    }
    return this.estado.mesesPagados.map((m) => MESES[m - 1]).join(', ');
  }

  registrar(): void {
    this.mensajeExito = '';
    this.error = '';
    this.ultimoPago = null;
    if (!this.seleccionado || !this.puedeGuardar) {
      return;
    }
    const v = this.form.value;
    const datos: DatosPago = {
      idEstudiante: this.seleccionado.id,
      idCiclo: Number(v.idCiclo),
      concepto: v.concepto,
      mes: v.concepto === 'MENSUALIDAD' ? Number(v.mes) : null,
      idActividad: v.concepto === 'ACTIVIDAD' ? Number(v.idActividad) : null,
      monto: Number(v.monto),
      fechaPago: v.fechaPago
    };
    this.pagos.registrar(datos).subscribe({
      next: (p) => {
        this.ultimoPago = p;
        this.mensajeExito = `Pago registrado. Comprobante ${p.numeroComprobante}`;
        this.form.patchValue({ mes: null, idActividad: null, monto: null }, { emitEvent: false });
        this.cargarEstadoCuenta();
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo registrar el pago')
    });
  }

  limpiar(): void {
    this.seleccionado = null;
    this.estado = null;
    this.ultimoPago = null;
    this.mensajeExito = '';
    this.error = '';
    this.errorEstado = '';
    this.form.patchValue(
      { buscar: '', concepto: 'INSCRIPCION', mes: null, idActividad: null, monto: null, fechaPago: this.fechaHoy() },
      { emitEvent: false }
    );
    this.pagina = 1;
    this.cargarEstudiantes();
  }

  private fechaHoy(): string {
    const d = new Date();
    const mes = String(d.getMonth() + 1).padStart(2, '0');
    const dia = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${mes}-${dia}`;
  }
}
