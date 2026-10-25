import { Routes } from '@angular/router';

import { authGuard } from './core/auth.guard';
import { Login } from './pages/login/login';
import { Inicio } from './pages/inicio/inicio';
import { UsuariosAdmin } from './pages/usuarios-admin/usuarios-admin';
import { NoAutorizado } from './pages/no-autorizado/no-autorizado';
import { Ciclos } from './pages/ciclos/ciclos';
import { Grados } from './pages-admin/grados/grados';
import { Carreras } from './pages/carreras/carreras';
import { GradosDefecto } from './pages/grados-defecto/grados-defecto';
import { Cursos } from './pages-admin/cursos/cursos';
import { GestionCurriculo } from './pages-admin/curriculo/curriculo';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'inicio', component: Inicio, canActivate: [authGuard] },
    {
    path: 'usuarios-admin',
    component: UsuariosAdmin,
    canActivate: [authGuard],
    data: { roles: ['SUPER_ADMIN'] }
  },

  { path: 'carreras', component: Carreras, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } } ,
  { path: 'ciclos', component: Ciclos, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } },
  { path: 'grados-defecto', component: GradosDefecto, canActivate: [authGuard], data: { roles: ['SUPER_ADMIN'] } },



  { path: 'grados', component: Grados, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'cursos', component: Cursos, canActivate: [authGuard], data: { roles: ['ADMIN'] } },
  { path: 'curriculo', component: GestionCurriculo, canActivate: [authGuard], data: { roles: ['ADMIN'] } },

  
  { path: 'no-autorizado', component: NoAutorizado },
  { path: '', pathMatch: 'full', redirectTo: 'inicio' },
  { path: '**', redirectTo: 'inicio' }
  
  
];

