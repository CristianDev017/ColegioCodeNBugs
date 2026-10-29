import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { UsuarioOperativoService } from '../../../core/usuario-operativo.service';
import { UsuarioOperativo } from '../../../models/usuario-operativo';
import { UsuarioOperativoForm } from '../usuario-operativo-form/usuario-operativo-form';

@Component({
  selector: 'app-editar-usuario-operativo',
  imports: [UsuarioOperativoForm, RouterLink],
  templateUrl: './editar-usuario-operativo.html'
})
export class EditarUsuarioOperativo implements OnInit {
  registro?: UsuarioOperativo;
  cargando: boolean = true;
  noEncontrado: boolean = false;

  constructor(private ruta: ActivatedRoute, private servicio: UsuarioOperativoService) {}

  ngOnInit(): void {
    const id = Number(this.ruta.snapshot.paramMap.get('id'));
    this.servicio.obtener(id).subscribe({
      next: (usuario) => {
        this.registro = usuario;
        this.cargando = false;
      },
      error: () => {
        this.noEncontrado = true;
        this.cargando = false;
      }
    });
  }
}