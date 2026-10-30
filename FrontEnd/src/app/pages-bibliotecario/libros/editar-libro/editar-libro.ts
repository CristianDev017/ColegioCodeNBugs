import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { LibroService } from '../../../core/libro.service';
import { Libro } from '../../../models/libro';
import { LibroForm } from '../libro-form/libro-form';

@Component({
  selector: 'app-editar-libro',
  imports: [LibroForm, RouterLink],
  templateUrl: './editar-libro.html'
})
export class EditarLibro implements OnInit {
  registro?: Libro;
  cargando: boolean = true;
  noEncontrado: boolean = false;

  constructor(private ruta: ActivatedRoute, private servicio: LibroService) {}

  ngOnInit(): void {
    const id = Number(this.ruta.snapshot.paramMap.get('id'));
    this.servicio.obtener(id).subscribe({
      next: (libro) => {
        this.registro = libro;
        this.cargando = false;
      },
      error: () => {
        this.noEncontrado = true;
        this.cargando = false;
      }
    });
  }
}