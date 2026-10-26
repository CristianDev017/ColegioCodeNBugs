import { CurrencyPipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PagoService } from '../../../core/pago.service';
import { MESES, Pago } from '../../../models/pago';

@Component({
  selector: 'app-comprobante-pago',
  imports: [RouterLink, CurrencyPipe],
  templateUrl: './comprobante-pago.html'
})
export class ComprobantePago implements OnInit {
  id!: number;
  pago?: Pago;
  cargado = false;
  error = '';

  constructor(private ruta: ActivatedRoute, private servicio: PagoService) {}

  ngOnInit(): void {
    this.id = Number(this.ruta.snapshot.params['id']);
    this.servicio.obtener(this.id).subscribe({
      next: (p) => {
        this.pago = p;
        this.cargado = true;
      },
      error: (e) => {
        this.error = e?.error?.mensaje ?? 'No se pudo cargar el comprobante';
        this.cargado = true;
      }
    });
  }

  detalle(p: Pago): string {
    if (p.concepto === 'MENSUALIDAD') {
      return `Mensualidad de ${MESES[(p.mes ?? 1) - 1]}`;
    }
    if (p.concepto === 'ACTIVIDAD') {
      return `Actividad: ${p.actividad}`;
    }
    return 'Inscripción';
  }

  imprimir(): void {
    window.print();
  }
}