import { CurrencyPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CicloEscolarService } from '../../../core/ciclo-escolar.service';
import { TarifaService } from '../../../core/tarifa.service';
import { CicloEscolar } from '../../../models/ciclo-escolar';
import { Tarifa } from '../../../models/tarifa';

@Component({
  selector: 'app-lista-tarifas',
  imports: [ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './lista-tarifas.html'
})
export class ListaTarifas implements OnInit {
  tarifas: Tarifa[] = [];
  ciclos: CicloEscolar[] = [];
  pagina = 1;
  tamano = 10;
  total = 0;
  error = '';

  filtro: FormGroup;

  constructor(
    private fb: FormBuilder,
    private servicio: TarifaService,
    private ciclosServicio: CicloEscolarService
  ) {
    this.filtro = this.fb.group({
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
    const idCiclo = Number(this.filtro.value.idCiclo ?? 0);
    this.servicio.listar(idCiclo, this.pagina, this.tamano).subscribe({
      next: (r) => {
        this.tarifas = r.datos;
        this.total = r.total;
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