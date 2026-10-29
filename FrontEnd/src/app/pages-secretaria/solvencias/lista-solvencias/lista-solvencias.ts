import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth.service';
import { CicloEscolarService } from '../../../core/ciclo-escolar.service';
import { SolvenciaService } from '../../../core/solvencia.service';
import { CicloEscolar } from '../../../models/ciclo-escolar';
import { Solvencia } from '../../../models/solvencia';

@Component({
  selector: 'app-lista-solvencias',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './lista-solvencias.html'
})
export class ListaSolvencias implements OnInit {
  solvencias: Solvencia[] = [];
  ciclos: CicloEscolar[] = [];
  pagina = 1;
  tamano = 10;
  total = 0;
  error = '';

  filtro: FormGroup;

  constructor(
    private fb: FormBuilder,
    private servicio: SolvenciaService,
    private ciclosServicio: CicloEscolarService,
    public auth: AuthService
  ) {
    this.filtro = this.fb.group({
      buscar: [''],
      idCiclo: [0]
    });
  }

  ngOnInit(): void {
    this.ciclosServicio.listar(1, 50).subscribe({
      next: (r) => (this.ciclos = r.datos),
      error: () => (this.ciclos = [])
    });
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {
    const buscar = String(this.filtro.value.buscar ?? '').trim();
    const idCiclo = Number(this.filtro.value.idCiclo ?? 0);
    this.servicio.listar(buscar, idCiclo, this.pagina, this.tamano).subscribe({
      next: (r) => {
        this.solvencias = r.datos;
        this.total = r.total;
        this.error = '';
      },
      error: (e) => (this.error = e?.error?.mensaje ?? 'No se pudo cargar la lista')
    });
  }

  filtrar(): void {
    this.pagina = 1;
    this.cargar();
  }

  irA(p: number): void {
    if (p < 1 || p > this.totalPaginas) {
      return;
    }
    this.pagina = p;
    this.cargar();
  }
}