import { Routes } from '@angular/router';

import { authGuard } from './core/auth.guard';

import { Login } from './pages/login/login';
import { Inicio } from './pages/inicio/inicio';
import { NoAutorizado } from './pages/no-autorizado/no-autorizado';

import { UsuariosAdmin } from './pages-superadmin/usuarios-admin/usuarios-admin';
import { Ciclos } from './pages-superadmin/ciclos/ciclos';
import { Carreras } from './pages-superadmin/carreras/carreras';
import { GradosDefecto } from './pages-superadmin/grados-defecto/grados-defecto';

import { Grados } from './pages-admin/grados/grados';
import { Cursos } from './pages-admin/cursos/cursos';
import { Secciones } from './pages-admin/secciones/secciones';
import { GestionCurriculo } from './pages-admin/curriculo/curriculo';
import { ListaTarifas } from './pages-admin/tarifas/lista-tarifas/lista-tarifas';
import { CrearTarifa } from './pages-admin/tarifas/crear-tarifa/crear-tarifa';
import { EditarTarifa } from './pages-admin/tarifas/editar-tarifa/editar-tarifa';
import { ListaActividades } from './pages-admin/actividades/lista-actividades/lista-actividades';
import { CrearActividad } from './pages-admin/actividades/crear-actividad/crear-actividad';
import { EditarActividad } from './pages-admin/actividades/editar-actividad/editar-actividad';

import { ListaEstudiantes } from './pages-secretaria/estudiantes/lista-estudiantes/lista-estudiantes';
import { CrearEstudiante } from './pages-secretaria/estudiantes/crear-estudiante/crear-estudiante';
import { EditarEstudiante } from './pages-secretaria/estudiantes/editar-estudiante/editar-estudiante';
import { ListaInscripciones } from './pages-secretaria/inscripciones/lista-inscripciones/lista-inscripciones';
import { CrearInscripcion } from './pages-secretaria/inscripciones/crear-inscripcion/crear-inscripcion';
import { ListaPagos } from './pages-secretaria/pagos/lista-pagos/lista-pagos';
import { CrearPago } from './pages-secretaria/pagos/crear-pago/crear-pago';
import { ComprobantePago } from './pages-secretaria/pagos/comprobante-pago/comprobante-pago';

export const routes: Routes = [
  // publicas
  { path: 'login', component: Login },
  { path: 'no-autorizado', component: NoAutorizado },

  // cualquier usuario con sesion
  { path: 'inicio', component: Inicio, canActivate: [authGuard] },

  // super admin
  { path: 'usuarios-admin', component: UsuariosAdmin, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } },
  { path: 'ciclos', component: Ciclos, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } },
  { path: 'carreras', component: Carreras, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } },
  { path: 'grados-defecto', component: GradosDefecto, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } },

  // admin: catalogo
  { path: 'grados', component: Grados, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'secciones', component: Secciones, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'cursos', component: Cursos, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'curriculo', component: GestionCurriculo, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

  // admin: tarifas
  { path: 'tarifas', component: ListaTarifas, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'tarifas/nueva', component: CrearTarifa, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'tarifas/editar/:id', component: EditarTarifa, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

  // admin: actividades
  { path: 'actividades', component: ListaActividades, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'actividades/nueva', component: CrearActividad, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'actividades/editar/:id', component: EditarActividad, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

  // secretaria: estudiantes
  { path: 'estudiantes', component: ListaEstudiantes, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
  { path: 'estudiantes/nuevo', component: CrearEstudiante, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
  { path: 'estudiantes/editar/:id', component: EditarEstudiante, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },

  // secretaria: inscripciones
  { path: 'inscripciones', component: ListaInscripciones, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
  { path: 'inscripciones/nueva', component: CrearInscripcion, canActivate: [authGuard], data: { roles: ['SECRETARIA'] } },


    // secretaria: pagos
  { path: 'pagos', component: ListaPagos, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
  { path: 'pagos/nuevo', component: CrearPago, canActivate: [authGuard], data: { roles: ['SECRETARIA'] } },
  { path: 'pagos/comprobante/:id', component: ComprobantePago, canActivate: [authGuard], data: { roles: ['SECRETARIA', 'ADMIN'] } },
 
 
  // redirecciones
  { path: '', pathMatch: 'full', redirectTo: 'inicio' },
  { path: '**', redirectTo: 'inicio' }
];

