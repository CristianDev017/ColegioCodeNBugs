import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MaestroService } from '../../core/maestro.service';
import { CursoMaestro } from '../../models/maestro';

@Component({
  selector: 'app-mis-cursos',
  imports: [RouterLink],
  templateUrl: './mis-cursos.html'
})
export class MisCursos implements OnInit {
  cursos: CursoMaestro[] = [];
  total: number = 0;
  pagina: number = 1;
  tamano: number = 10;
  cargando: boolean = false;
  mensajeError: string = '';

  constructor(private servicio: MaestroService) {}

  ngOnInit(): void {
    this.cargar();
  }

  get totalPaginas(): number {
    return Math.max(1, Math.ceil(this.total / this.tamano));
  }

  cargar(): void {
    this.cargando = true;
    this.servicio.cursos(this.pagina, this.tamano).subscribe({
      next: (respuesta) => {
        this.cursos = respuesta.datos;
        this.total = respuesta.total;
        this.cargando = false;
      },
      error: (e) => {
        this.mensajeError = e?.error?.mensaje ?? 'No se pudieron cargar tus cursos';
        this.cargando = false;
      }
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