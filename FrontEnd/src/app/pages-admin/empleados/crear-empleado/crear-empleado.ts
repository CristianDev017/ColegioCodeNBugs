import { Component } from '@angular/core';
import { EmpleadoForm } from '../empleado-form/empleado-form';

@Component({
  selector: 'app-crear-empleado',
  imports: [EmpleadoForm],
  templateUrl: './crear-empleado.html'
})
export class CrearEmpleado {}