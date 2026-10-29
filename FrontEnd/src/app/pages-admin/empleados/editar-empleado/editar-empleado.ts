import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EmpleadoService } from '../../../core/empleado.service';
import { Empleado } from '../../../models/empleado';
import { EmpleadoForm } from '../empleado-form/empleado-form';

@Component({
  selector: 'app-editar-empleado',
  imports: [EmpleadoForm, RouterLink],
  templateUrl: './editar-empleado.html'
})
export class EditarEmpleado implements OnInit {
  registro?: Empleado;
  cargando: boolean = true;
  noEncontrado: boolean = false;

  constructor(private ruta: ActivatedRoute, private servicio: EmpleadoService) {}

  ngOnInit(): void {
    const id = Number(this.ruta.snapshot.paramMap.get('id'));
    this.servicio.obtener(id).subscribe({
      next: (empleado) => {
        this.registro = empleado;
        this.cargando = false;
      },
      error: () => {
        this.noEncontrado = true;
        this.cargando = false;
      }
    });
  }
}