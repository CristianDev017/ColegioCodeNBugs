import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { SolvenciaService } from '../../../core/solvencia.service';
import { Solvencia } from '../../../models/solvencia';

@Component({
  selector: 'app-constancia-solvencia',
  imports: [RouterLink],
  templateUrl: './constancia-solvencia.html'
})
export class ConstanciaSolvencia implements OnInit {
  id!: number;
  solvencia?: Solvencia;
  cargado = false;
  error = '';

  constructor(private ruta: ActivatedRoute, private servicio: SolvenciaService) {}

  ngOnInit(): void {
    this.id = Number(this.ruta.snapshot.params['id']);
    this.servicio.obtener(this.id).subscribe({
      next: (s) => {
        this.solvencia = s;
        this.cargado = true;
      },
      error: (e) => {
        this.error = e?.error?.mensaje ?? 'No se pudo cargar la constancia';
        this.cargado = true;
      }
    });
  }

  imprimir(): void {
    window.print();
  }
}