import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { TarifaService } from '../../../core/tarifa.service';
import { Tarifa } from '../../../models/tarifa';
import { TarifaForm } from '../tarifa-form/tarifa-form';

@Component({
  selector: 'app-editar-tarifa',
  imports: [TarifaForm, RouterLink],
  templateUrl: './editar-tarifa.html'
})
export class EditarTarifa implements OnInit {
  id!: number;
  registro?: Tarifa;
  existe = false;
  cargado = false;

  constructor(private ruta: ActivatedRoute, private servicio: TarifaService) {}

  ngOnInit(): void {
    this.id = Number(this.ruta.snapshot.params['id']);
    this.servicio.obtener(this.id).subscribe({
      next: (tarifa) => {
        this.registro = tarifa;
        this.existe = true;
        this.cargado = true;
      },
      error: () => (this.cargado = true)
    });
  }
}